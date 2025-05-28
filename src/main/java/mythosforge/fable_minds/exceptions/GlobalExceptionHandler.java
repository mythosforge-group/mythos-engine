package mythosforge.fable_minds.exceptions;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(BusinessException ex) {
    ErrorResponse error = new ErrorResponse("Erro de Negócio", ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
  }



  static class ErrorResponse {
    private String titulo;
    private String mensagem;

    public ErrorResponse(String titulo, String mensagem) {
      this.titulo = titulo;
      this.mensagem = mensagem;
    }

    public String getTitulo() {
      return titulo;
    }

    public String getMensagem() {
      return mensagem;
    }
  }
}
