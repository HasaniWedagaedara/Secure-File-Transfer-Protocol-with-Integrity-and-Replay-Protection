import java.io.Serializable;

public class SecureFile implements Serializable {
    private static final long serialVersionUID = 1L;

    private String fileName;
    private byte[] encryptedData;
    private byte[] iv;
    private String hmac;
    private String nonce;
    private String timestamp;


    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public byte[] getEncryptedData() { return encryptedData; }
    public void setEncryptedData(byte[] encryptedData) { this.encryptedData = encryptedData; }
    public byte[] getIv() { return iv; }
    public void setIv(byte[] iv) { this.iv = iv; }
    public String getHmac() { return hmac; }
    public void setHmac(String hmac) { this.hmac = hmac; }
    public String getNonce() { return nonce; }
    public void setNonce(String nonce) { this.nonce = nonce; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
