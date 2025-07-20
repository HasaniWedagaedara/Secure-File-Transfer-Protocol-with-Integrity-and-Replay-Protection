public class MyFile {
    //each file here(row) is a MyFile object
    private int id;
    private  String name;
    private  byte[] data;
    private String filExtension;

    public MyFile(int id, String name, byte[] data, String filExtension) {
        this.id = id;
        this.name = name;
        this.data = data;
        this.filExtension = filExtension;


    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public void setFilExtension(String filExtension) {
        this.filExtension = filExtension;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }

    public String getFilExtension() {
        return filExtension;
    }
}
