package hexlet.code.dto.user;

import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {
    //@NotBlank
    private JsonNullable<String> firstName;
    //@NotBlank
    private JsonNullable<String> lastName;
    @Email
    private JsonNullable<String> email;
}
