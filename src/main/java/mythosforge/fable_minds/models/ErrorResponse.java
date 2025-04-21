package mythosforge.fable_minds.models;

public class ErrorResponse {
    private String message;
    private String details;

    // Construtor público
    public ErrorResponse(String message, String details) {
        this.message = message;
        this.details = details;
    }

    // Getters e setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
