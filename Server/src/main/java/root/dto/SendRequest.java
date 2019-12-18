package root.dto;

public class SendRequest {
    private int src;
    private int dst;
    private String msg;
    private String copy;

    public int getSrc() {
        return src;
    }

    public int getDst() {
        return dst;
    }

    public String getMsg() {
        return msg;
    }

    public String getCopy() {
        return copy;
    }
}
