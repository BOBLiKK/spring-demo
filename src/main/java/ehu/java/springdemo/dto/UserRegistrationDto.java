package ehu.java.springdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
public class UserRegistrationDto {

    @NotBlank(message = "Login cannot be empty. ")
    @Size(min = 4, max = 20, message = "Login must contain between 4 and 20 symbols. ")
    private String login;

    @NotBlank(message = "Password cannot be empty. ")
    @Size(min = 6, message = "Password must contain at least 6 symbols. ")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit. ")
    private String password;

    @NotBlank(message = "Email cannot be empty. ")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", message = "Wrong email format. ")
    private String email;

    @NotBlank(message = "Password confirmation required. ")
    private String confirmPassword;
}
