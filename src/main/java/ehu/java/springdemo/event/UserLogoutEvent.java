package ehu.java.springdemo.event;

import org.springframework.context.ApplicationEvent;

public class UserLogoutEvent extends ApplicationEvent {
    private final String username;

    public UserLogoutEvent(Object source, String username) {
        super(source);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }
}
