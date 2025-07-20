package Utils;

import java.io.File;
import java.security.KeyPair;

public class KeyLoader {

    public static boolean keysExist(String pubPath, String privPath) {
        return new File(pubPath).exists() && new File(privPath).exists();
    }

    public static void saveKeys(KeyPair keyPair, String pubPath, String privPath) throws Exception {
        RSAUtils.saveKey(keyPair.getPublic(), pubPath);
        RSAUtils.saveKey(keyPair.getPrivate(), privPath);
    }

    public static java.security.PublicKey loadPublicKey(String pubPath) throws Exception {
        return RSAUtils.loadPublicKey(pubPath);
    }

    public static java.security.PrivateKey loadPrivateKey(String privPath) throws Exception {
        return RSAUtils.loadPrivateKey(privPath);
    }
}
