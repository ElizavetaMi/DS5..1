import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationDto {

    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "Invalid login")
    private String login;

    private String password;
    private String status;
}
