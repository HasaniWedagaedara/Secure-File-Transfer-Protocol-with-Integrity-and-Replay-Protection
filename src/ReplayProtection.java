import java.util.HashSet;
import java.util.Set;

public class ReplayProtection {
    private static final Set<String> used = new HashSet<>();

    public static synchronized boolean isNonceUsed(String nonce) {
        return used.contains(nonce);
    }

    public static synchronized void addNonce(String nonce) {
        used.add(nonce);
    }
}
