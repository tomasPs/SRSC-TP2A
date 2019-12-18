package root.dto;

import java.util.List;

public class AllResponse {
    private List<String[]> result;

    public AllResponse(List<String[]> result) {
        this.result = result;
    }

    public List<String[]> getResult() {
        return result;
    }
}
