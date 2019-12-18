package root.dto;

import java.util.List;

public class RecvResponse {
    private List<String> result;

    public RecvResponse(List<String> result) {
        this.result = result;
    }

    public List<String> getResult() {
        return result;
    }
}
