package root.dto;

import java.util.List;

public class SendResponse {
    private List<String> result;

    public SendResponse(List<String> result) {
        this.result = result;
    }

    public List<String> getResult() {
        return result;
    }
}
