package hexlet.code.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
@Setter
public class ErrorDTO {

    public ErrorDTO(HttpStatus status) {
        this.status = status.value();
        this.error = status.getReasonPhrase();
    }

    private int status;
    private String error;
    private List<String> message;
}
