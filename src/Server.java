import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import java.time.Instant;
import java.time.Duration;

public class Server {
    public static void main(String[] args) throws IOException {
        JFrame jFrame = new JFrame("Server Side");
        jFrame.setSize(400, 400);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panel);

        JLabel title = new JLabel("Secure File Receiver");
        title.setFont(new Font("Arial", Font.BOLD, 25));
        title.setBorder(new EmptyBorder(20,0,10,0));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        jFrame.add(title);
        jFrame.add(scroll);
        jFrame.setVisible(true);

        ServerSocket server = new ServerSocket(1234);
        while (true) {
            try (Socket sock = server.accept();
                 ObjectInputStream in = new ObjectInputStream(sock.getInputStream())) {

                SecureFile sf = (SecureFile) in.readObject();

                if (checkReplay(sf.getNonce(), sf.getTimestamp(), panel)) {
                    System.out.println("Replay detected! Discarding.");
                    continue;
                }

                SecretKey aesKey = CryptoUtils.generateAESKey(); // Actually you need shared key; demo uses ephemeral
                IvParameterSpec iv = new IvParameterSpec(sf.getIv());

                byte[] dec = CryptoUtils.decrypt(sf.getEncryptedData(), aesKey, iv);
                panel.add(createFileRow(sf.getFileName()));
                panel.revalidate();

                System.out.println("Received and decrypted: " + sf.getFileName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private static boolean checkReplay(String nonce, String ts, JPanel panel) {
        if (ReplayProtection.isNonceUsed(nonce)) return true;
        Instant t = Instant.parse(ts);
        if (Duration.between(t, Instant.now()).toMinutes() > 5) return true;
        ReplayProtection.addNonce(nonce);
        return false;
    }

    private static JPanel createFileRow(String name) {
        JPanel row = new JPanel();
        row.add(new JLabel(name));
        return row;
    }
}
