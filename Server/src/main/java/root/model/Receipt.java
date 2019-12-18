package root.model;

import java.util.Date;

public class Receipt {
    private String data;
    private int id;
    private String receipt;

    public String getData() {
        return data;
    }

    public int getId() {
        return id;
    }

    public String getReceipt() {
        return receipt;
    }

    public Receipt(String data, int id, String receipt) {
        this.data = data;
        this.id = id;
        this.receipt = receipt;
    }
}
