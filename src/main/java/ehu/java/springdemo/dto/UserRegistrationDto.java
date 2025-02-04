package ehu.java.springdemo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationDto {

    @NotBlank(message = "Login cannot be empty. ")
    @Size(min = 4, max = 20, message = "Login must contain between 4 and 20 symbols. ")
    private String login;

    @NotBlank(message = "Password cannot be empty. ")
    @Size(min = 6, message = "Password must contain at least 6 symbols. ")
    @Pattern(regexp = ".*\\d.*", message = "Password must contain at least one digit. ")
    private String password;

    @NotBlank(message = "Password confirmation required. ")
    private String confirmPassword;

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }
}
