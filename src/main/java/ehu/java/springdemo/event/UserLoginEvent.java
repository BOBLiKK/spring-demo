package ehu.java.springdemo.event;

import org.springframework.context.ApplicationEvent;

public class UserLoginEvent extends ApplicationEvent {
    private final String username;
    private final String role;

    public UserLoginEvent(Object source, String username, String role) {
        super(source);
        this.username = username;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public String getRole() {
        return role;
    }
}
