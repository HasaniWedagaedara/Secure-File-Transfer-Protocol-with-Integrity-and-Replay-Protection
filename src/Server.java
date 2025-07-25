import Utils.KeyLoader;
import Utils.RSAUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.PublicKey;

public class Server {

    private static final int SERVER_PORT = 1234;
    private static final int CLIENT_RECEIVE_PORT = 5678;

    private static final String BOB_PRIVATE_KEY = "bob_private.key";
    private static final String BOB_PUBLIC_KEY = "bob_public.key";
    private static final String ALICE_PUBLIC_KEY = "alice_public.key";

    public static void main(String[] args) {

        new File("ServerFiles/").mkdirs();

        try {
            if (!KeyLoader.keysExist(BOB_PUBLIC_KEY, BOB_PRIVATE_KEY)) {
                KeyPair keyPair = RSAUtils.generateKeyPair();
                KeyLoader.saveKeys(keyPair, BOB_PUBLIC_KEY, BOB_PRIVATE_KEY);
            }
            PrivateKey privateKey = KeyLoader.loadPrivateKey(BOB_PRIVATE_KEY);
            FileTransfer.setPrivateKey(privateKey);

            PublicKey alicePublicKey = KeyLoader.loadPublicKey(ALICE_PUBLIC_KEY);
            FileTransfer.setPublicKey(alicePublicKey);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // GUI Components
        JFrame frame = new JFrame("Bob (Server)");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Bob Server: Listening on port " + SERVER_PORT);
        label.setBorder(new EmptyBorder(20, 0, 20, 0));
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);

        // File list display
        DefaultListModel<String> fileListModel = new DefaultListModel<>();
        JList<String> fileList = new JList<>(fileListModel);
        JScrollPane scrollPane = new JScrollPane(fileList);
        scrollPane.setPreferredSize(new Dimension(550, 200));
        scrollPane.setBorder(new EmptyBorder(10, 20, 10, 20));
        panel.add(scrollPane);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Start server thread
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                System.out.println("Server listening on port " + SERVER_PORT);
                while (true) {
                    Socket socket = serverSocket.accept();
                    // Receive file and return saved file name
                    String savedFilePath = FileTransfer.receiveFile(socket, "ServerFiles/");
                    if (savedFilePath != null) {
                        File savedFile = new File(savedFilePath);
                        String fileNameOnly = savedFile.getName();
                        SwingUtilities.invokeLater(() -> fileListModel.addElement(fileNameOnly));
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
