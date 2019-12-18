package root.dto;

import java.util.List;

public class NewResponse {
    private List<String> result;

    public NewResponse(List<String> result) {
        this.result = result;
    }

    public List<String> getResult() {
        return result;
    }
}
