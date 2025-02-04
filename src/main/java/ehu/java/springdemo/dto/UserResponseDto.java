package ehu.java.springdemo.dto;

import ehu.java.springdemo.entity.User;
import lombok.Data;

@Data
public class UserResponseDto {
    private Long id;
    private String login;
    private String role;

    public UserResponseDto(User user) {
        this.id = user.getId();
        this.login = user.getLogin();
        this.role = user.getRole();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}

