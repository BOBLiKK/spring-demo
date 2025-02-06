package ehu.java.springdemo.controller;

import ehu.java.springdemo.config.SecurityConfig;
import ehu.java.springdemo.dto.UserRegistrationDto;
import ehu.java.springdemo.dto.UserResponseDto;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@WebMvcTest(MainController.class)
@Import(SecurityConfig.class)
class MainControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @WithMockUser
    @Test
    void testHome() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("index"))
                .andExpect(model().attributeExists("title"));
    }

    @Test
    void testLogin() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("login"));
    }

    @Test
    void testRegister() throws Exception {
        mockMvc.perform(get("/register"))
                .andExpect(status().isOk())
                .andExpect(view().name("register"))
                .andExpect(model().attributeExists("user"));
    }

    @Test
    void testDoRegister_Success() throws Exception {
        when(userService.validateRegistration(any(), any())).thenReturn(false);
        doNothing().when(userService).registerNewUser(any());

        mockMvc.perform(post("/do-register")
                        .flashAttr("user", new UserRegistrationDto()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    void testDoRegister_Fail() throws Exception {
        when(userService.validateRegistration(any(), any())).thenReturn(true);

        mockMvc.perform(post("/do-register")
                        .flashAttr("user", new UserRegistrationDto()))
                .andExpect(status().isOk())
                .andExpect(view().name("register"));
    }

    @WithMockUser(username = "testUser")
    @Test
    void testGetUserInfo() throws Exception {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setLogin("testUser");
        mockUser.setRole(User.Role.valueOf("USER"));
        mockUser.setEmail("test@example.com");

        UserResponseDto userResponseDto = new UserResponseDto(mockUser);
        when(userService.getUserInfo("testUser")).thenReturn(userResponseDto);

        mockMvc.perform(get("/user/info"))
                .andExpect(status().isOk());
    }


    @WithMockUser(username = "testUser")
    @Test
    void testRedirectAfterLogin_User() throws Exception {
        User user = new User();
        user.setRole(User.Role.valueOf("USER"));

        when(userService.findUserByLogin("testUser")).thenReturn(user);

        mockMvc.perform(get("/redirect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/user_dashboard"));
    }

    @WithMockUser(username = "admin")
    @Test
    void testRedirectAfterLogin_Admin() throws Exception {
        User user = new User();
        user.setRole(User.Role.valueOf("ADMIN"));

        when(userService.findUserByLogin("admin")).thenReturn(user);

        mockMvc.perform(get("/redirect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/admin_dashboard"));
    }

    @Test
    void testRedirectAfterLogin_Unauthenticated() throws Exception {
        mockMvc.perform(get("/redirect"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("**/login"));
    }
}