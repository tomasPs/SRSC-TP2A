package root.dto;

import root.model.User;

import java.util.List;

public class ListResponse {
    private List<User> result;

    public ListResponse(List<User> result) {
        this.result = result;
    }

    public List<User> getResult() {
        return result;
    }
}
