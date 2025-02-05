package ehu.java.springdemo.dto;

import ehu.java.springdemo.entity.User;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class UserResponseDto {
    private Long id;
    private String login;
    private String role;
    private String email;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.role = user.getRole();
        this.email = user.getEmail();
    }
}

