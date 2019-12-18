package root.dto;

import root.model.Receipt;

import java.util.List;

public class Status {
    private String msg;
    private List<Receipt> receipts;

    public Status(String msg, List<Receipt> receipts) {
        this.msg = msg;
        this.receipts = receipts;
    }

    public String getMsg() {
        return msg;
    }

    public List<Receipt> getReceipts() {
        return receipts;
    }
}
