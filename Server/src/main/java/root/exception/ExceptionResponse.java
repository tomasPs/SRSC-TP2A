package root.exception;

public class ExceptionResponse {
    private String error;

    public ExceptionResponse(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }
}