import javax.crypto.*;
import javax.crypto.spec.*;
import java.security.*;
import java.util.Base64;
import java.util.UUID;
import java.time.Instant;

public class CryptoUtils {

    // Generate AES key
    public static SecretKey generateAESKey() throws Exception {
        KeyGenerator gen = KeyGenerator.getInstance("AES");
        gen.init(128); // 128-bit AES key
        return gen.generateKey();
    }

    // Generate IV
    public static IvParameterSpec generateIV() {
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv);
        return new IvParameterSpec(iv);
    }

    // Encrypt data with AES
    public static byte[] encrypt(byte[] data, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key, iv);
        return cipher.doFinal(data);
    }

    // Decrypt AES
    public static byte[] decrypt(byte[] encrypted, SecretKey key, IvParameterSpec iv) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key, iv);
        return cipher.doFinal(encrypted);
    }

    // Compute HMAC (SHA256)
    public static String computeHMAC(byte[] data, SecretKey key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(key);
        byte[] hmacBytes = mac.doFinal(data);
        return Base64.getEncoder().encodeToString(hmacBytes);
    }

    // Base64 encoding/decoding helpers
    public static String encodeBase64(byte[] input) {
        return Base64.getEncoder().encodeToString(input);
    }

    public static byte[] decodeBase64(String input) {
        return Base64.getDecoder().decode(input);
    }

    // Generate UUID nonce
    public static String generateNonce() {
        return UUID.randomUUID().toString();
    }

    // Get current timestamp in ISO format
    public static String getTimestamp() {
        return Instant.now().toString();
    }
}
