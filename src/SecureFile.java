import java.io.Serializable;

public class SecureFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] fileContent;
    private long timestamp;
    private String nonce;
    private byte[] signature;

    public SecureFile(String fileName, byte[] fileContent, long timestamp, String nonce, byte[] signature) {
        this.fileName = fileName;
        this.fileContent = fileContent;
        this.timestamp = timestamp;
        this.nonce = nonce;
        this.signature = signature;
    }

    // Getters and setters
    public String getFileName() { return fileName; }
    public byte[] getFileContent() { return fileContent; }
    public long getTimestamp() { return timestamp; }
    public String getNonce() { return nonce; }
    public byte[] getSignature() { return signature; }
    public void setSignature(byte[] signature) { this.signature = signature; }
}
