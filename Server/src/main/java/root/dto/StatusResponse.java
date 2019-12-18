package root.dto;

public class StatusResponse {
    private Status result;

    public StatusResponse(Status result) {
        this.result = result;
    }

    public Status getResult() {
        return result;
    }
}
