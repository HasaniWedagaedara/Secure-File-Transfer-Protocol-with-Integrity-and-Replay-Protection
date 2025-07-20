package Utils;

import java.io.*;
import java.security.*;
import java.security.spec.*;
import java.util.Base64;

public class RSAUtils {

    public static KeyPair generateKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    public static byte[] sign(byte[] data, PrivateKey privateKey) throws Exception {
        Signature signer = Signature.getInstance("SHA256withRSA");
        signer.initSign(privateKey);
        signer.update(data);
        return signer.sign();
    }

    public static boolean verify(byte[] data, byte[] signature, PublicKey publicKey) throws Exception {
        Signature verifier = Signature.getInstance("SHA256withRSA");
        verifier.initVerify(publicKey);
        verifier.update(data);
        return verifier.verify(signature);
    }

    // Save keys as Base64 strings in files
    public static void saveKey(Key key, String filePath) throws IOException {
        byte[] encoded = key.getEncoded();
        String keyStr = Base64.getEncoder().encodeToString(encoded);
        try (FileWriter fw = new FileWriter(filePath)) {
            fw.write(keyStr);
        }
    }

    // Load public key from Base64 file
    public static PublicKey loadPublicKey(String filePath) throws Exception {
        String keyStr = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        byte[] decoded = Base64.getDecoder().decode(keyStr);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePublic(spec);
    }

    // Load private key from Base64 file
    public static PrivateKey loadPrivateKey(String filePath) throws Exception {
        String keyStr = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(filePath)));
        byte[] decoded = Base64.getDecoder().decode(keyStr);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
        KeyFactory factory = KeyFactory.getInstance("RSA");
        return factory.generatePrivate(spec);
    }
}
