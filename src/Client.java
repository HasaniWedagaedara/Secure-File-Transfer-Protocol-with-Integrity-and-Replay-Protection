import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

public class Client {
    public static void main(String[] args) {
        final File[] fileToSend = new File[1];

        JFrame jFrame = new JFrame("Client Side");
        jFrame.setSize(450, 450);
        jFrame.setLayout(new BoxLayout(jFrame.getContentPane(), BoxLayout.Y_AXIS));
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JLabel jlTitle = new JLabel("File Sender");
        jlTitle.setFont(new Font("Arial", Font.BOLD, 25));
        jlTitle.setBorder(new EmptyBorder(20, 0, 10, 0));
        jlTitle.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel jlFileName = new JLabel("choose a file to send");
        jlFileName.setFont(new Font("Arial", Font.BOLD, 20));
        jlFileName.setBorder(new EmptyBorder(50, 0, 10, 0));
        jlFileName.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel jpButton = new JPanel();
        jpButton.setBorder(new EmptyBorder(75, 0, 10, 0));

        JButton jbSendFile = new JButton("Send file");
        jbSendFile.setPreferredSize(new Dimension(150, 75));
        jbSendFile.setFont(new Font("Arial", Font.BOLD, 20));

        JButton jbChooseFile = new JButton("Choose file");
        jbChooseFile.setPreferredSize(new Dimension(150, 75));
        jbChooseFile.setFont(new Font("Arial", Font.BOLD, 20));

        jpButton.add(jbSendFile);
        jpButton.add(jbChooseFile);

        jbChooseFile.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Choose a file to send");
            if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
                fileToSend[0] = chooser.getSelectedFile();
                jlFileName.setText("Selected: " + fileToSend[0].getName());
            }
        });

        jbSendFile.addActionListener(e -> {
            if (fileToSend[0] == null) {
                jlFileName.setText("Please choose a file first.");
            } else {
                try (Socket socket = new Socket("localhost", 1234);
                     ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

                    byte[] plain = java.nio.file.Files.readAllBytes(fileToSend[0].toPath());

                    SecretKey aesKey = CryptoUtils.generateAESKey();
                    IvParameterSpec iv = CryptoUtils.generateIV();
                    byte[] enc = CryptoUtils.encrypt(plain, aesKey, iv);

                    String nonce = CryptoUtils.generateNonce();
                    String ts = CryptoUtils.getTimestamp();

                    ByteArrayOutputStream ms = new ByteArrayOutputStream();
                    ms.write(iv.getIV());
                    ms.write(enc);
                    byte[] dataForHmac = ms.toByteArray();
                    String hmac = CryptoUtils.computeHMAC(dataForHmac, aesKey);

                    SecureFile sf = new SecureFile();
                    sf.setFileName(fileToSend[0].getName());
                    sf.setEncryptedData(enc);
                    sf.setIv(iv.getIV());
                    sf.setNonce(nonce);
                    sf.setTimestamp(ts);
                    sf.setHmac(hmac);

                    out.writeObject(sf);
                    System.out.println("Sent secure file: " + fileToSend[0].getName());

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        jFrame.add(jlTitle);
        jFrame.add(jlFileName);
        jFrame.add(jpButton);
        jFrame.setVisible(true);
    }
}
