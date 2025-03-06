package mate.academy.dto.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.experimental.Accessors;
import mate.academy.lib.FieldMatch;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@FieldMatch(first = "password", second = "repeatPassword", message = "Passwords must match")
@Accessors(chain = true)
public class UserRegistrationRequestDto {
    @NotNull
    private String email;
    @NotNull
    @Size(min = 7, max = 100)
    private String password;
    @NotNull
    @Size(min = 7, max = 100)
    private String repeatPassword;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
}
