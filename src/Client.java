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

public class Client {

    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 1234;
    private static final int CLIENT_RECEIVE_PORT = 5678;

    private static final String ALICE_PRIVATE_KEY = "alice_private.key";
    private static final String ALICE_PUBLIC_KEY = "alice_public.key";
    private static final String BOB_PUBLIC_KEY = "bob_public.key";

    public static void main(String[] args) {

        new File("ClientFiles/").mkdirs();

        try {
            if (!KeyLoader.keysExist(ALICE_PUBLIC_KEY, ALICE_PRIVATE_KEY)) {
                KeyPair keyPair = RSAUtils.generateKeyPair();
                KeyLoader.saveKeys(keyPair, ALICE_PUBLIC_KEY, ALICE_PRIVATE_KEY);
            }
            PrivateKey privateKey = KeyLoader.loadPrivateKey(ALICE_PRIVATE_KEY);
            FileTransfer.setPrivateKey(privateKey);

            PublicKey bobPublicKey = KeyLoader.loadPublicKey(BOB_PUBLIC_KEY);
            FileTransfer.setPublicKey(bobPublicKey);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        JFrame frame = new JFrame("Alice (Client)");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);

        JLabel label = new JLabel("Select a file to send to server");
        label.setBorder(new EmptyBorder(20, 0, 20, 0));
        label.setFont(new Font("Arial", Font.BOLD, 20));
        label.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton chooseButton = new JButton("Choose File");
        JButton sendButton = new JButton("Send File");

        JLabel fileLabel = new JLabel("No file selected");
        fileLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(label);
        panel.add(chooseButton);
        panel.add(fileLabel);
        panel.add(sendButton);

        frame.add(panel, BorderLayout.CENTER);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        final File[] fileToSend = {null};

        chooseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                fileToSend[0] = chooser.getSelectedFile();
                fileLabel.setText("Selected: " + fileToSend[0].getName());
            }
        });

        sendButton.addActionListener(e -> {
            if (fileToSend[0] == null) {
                JOptionPane.showMessageDialog(null, "No file selected!");
                return;
            }
            try {
                FileTransfer.sendFile(fileToSend[0], SERVER_ADDRESS, SERVER_PORT);
                JOptionPane.showMessageDialog(null, "File sent securely!");
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null, "Failed to send file: " + ex.getMessage());
            }
        });

        // Start background thread to receive files
        new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(CLIENT_RECEIVE_PORT)) {
                System.out.println("Client listening on port " + CLIENT_RECEIVE_PORT);
                while (true) {
                    Socket socket = serverSocket.accept();
                    FileTransfer.receiveFile(socket, "ClientFiles/");
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }).start();
    }
}
