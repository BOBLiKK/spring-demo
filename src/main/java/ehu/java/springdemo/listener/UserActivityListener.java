package ehu.java.springdemo.listener;

import ehu.java.springdemo.event.UserLoginEvent;
import ehu.java.springdemo.event.UserLogoutEvent;
import ehu.java.springdemo.event.UserRegistrationEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserActivityListener {

    @EventListener
    public void handleUserRegistration(UserRegistrationEvent event) {
        log.info(" Registration: User {} registered", event.getUsername());
    }

    @EventListener
    public void handleUserLogin(UserLoginEvent event) {
        log.info(" Login: User {} with the role {} loggen in", event.getUsername(), event.getRole());
    }

    @EventListener
    public void handleUserLogout(UserLogoutEvent event) {
        log.info(" Logout: User {} logged out", event.getUsername());
    }
}
