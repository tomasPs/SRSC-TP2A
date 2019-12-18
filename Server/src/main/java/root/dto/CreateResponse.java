package root.dto;

public class CreateResponse {
    private int result;

    public CreateResponse(int id) {
        this.result = id;
    }

    public int getResult() {
        return result;
    }
}
