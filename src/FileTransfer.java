import Utils.AESUtils;
import Utils.FileUtils;
import Utils.RSAUtils;

import java.io.*;
import java.net.Socket;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashSet;
import java.util.Set;

public class FileTransfer {

    // Shared AES key (must be identical for sender and receiver)
    private static final byte[] AES_KEY = AESUtils.getKeyFromString("1234567890123456");

    private static PrivateKey privateKey;
    private static PublicKey publicKey;

    private static final Set<String> usedNonces = new HashSet<>();
    private static final long ALLOWED_TIME_WINDOW_MS = 5 * 60 * 1000; // 5 minutes

    public static void setPrivateKey(PrivateKey key) {
        privateKey = key;
    }

    public static void setPublicKey(PublicKey key) {
        publicKey = key;
    }

    // Generate random nonce
    private static String generateNonce() {
        byte[] nonceBytes = new byte[16];
        new SecureRandom().nextBytes(nonceBytes);
        return Base64.getEncoder().encodeToString(nonceBytes);
    }

    // Serialize an object to byte array
    private static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    // Deserialize byte array to SecureFilePayload
    private static SecureFile deserialize(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return (SecureFile) ois.readObject();
        }
    }

    // Send file securely
    public static void sendFile(File file, String host, int port) throws Exception {
        try (Socket socket = new Socket(host, port);
             DataOutputStream out = new DataOutputStream(socket.getOutputStream())) {

            byte[] fileBytes = FileUtils.readFile(file.getAbsolutePath());
            long timestamp = System.currentTimeMillis();
            String nonce = generateNonce();

            SecureFile payload = new SecureFile(file.getName(), fileBytes, timestamp, nonce, null);

            // Sign payload (without signature)
            byte[] dataToSign = serialize(new SecureFile(file.getName(), fileBytes, timestamp, nonce, null));
            byte[] signature = RSAUtils.sign(dataToSign, privateKey);
            payload.setSignature(signature);

            // Serialize full payload
            byte[] serializedPayload = serialize(payload);

            // Encrypt with AES
            byte[] encryptedPayload = AESUtils.encrypt(serializedPayload, AES_KEY);

            // Send length + encrypted payload
            out.writeInt(encryptedPayload.length);
            out.write(encryptedPayload);
            out.flush();

            System.out.println("Sent file: " + file.getName());
        }
    }

    // Receive file securely
    public static String receiveFile(Socket socket, String saveDir) {
        try (DataInputStream in = new DataInputStream(socket.getInputStream())) {
            int length = in.readInt();
            if (length <= 0) return null;

            byte[] encryptedPayload = new byte[length];
            in.readFully(encryptedPayload);

            byte[] decryptedPayload = AESUtils.decrypt(encryptedPayload, AES_KEY);
            SecureFile payload = deserialize(decryptedPayload);

            long now = System.currentTimeMillis();
            if (Math.abs(now - payload.getTimestamp()) > ALLOWED_TIME_WINDOW_MS) {
                System.err.println("Rejected: Timestamp invalid");
                return null;
            }

            if (usedNonces.contains(payload.getNonce())) {
                System.err.println("Rejected: Nonce replay");
                return null;
            }

            byte[] dataToVerify = serialize(new SecureFile(
                    payload.getFileName(),
                    payload.getFileContent(),
                    payload.getTimestamp(),
                    payload.getNonce(),
                    null));

            if (!RSAUtils.verify(dataToVerify, payload.getSignature(), publicKey)) {
                System.err.println("Rejected: Signature invalid");
                return null;
            }

            usedNonces.add(payload.getNonce());
            File outFile = new File(saveDir + payload.getFileName());
            FileUtils.writeFile(outFile.getAbsolutePath(), payload.getFileContent());

            System.out.println("Received and saved file: " + outFile.getAbsolutePath());
            return outFile.getAbsolutePath(); // <-- return the saved path
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
