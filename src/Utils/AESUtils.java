package Utils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Arrays;

public class AESUtils {

    private static final String AES_ALGORITHM = "AES";

    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding"); // simple ECB mode for demo (replace with CBC + IV in prod)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] encryptedData, byte[] key) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(key, AES_ALGORITHM);
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        return cipher.doFinal(encryptedData);
    }

    // Helper to get 16-byte AES key from string
    public static byte[] getKeyFromString(String keyStr) {
        byte[] keyBytes = keyStr.getBytes();
        return Arrays.copyOf(keyBytes, 16);
    }
}
