package mythosforge.fable_minds.models;
import lombok.Data;
@Data
public class ErrorResponse {
    private final String message;
    private final String details;
}