package ehu.java.springdemo.controller;

import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriminalService criminalService;

    @MockitoBean
    private RequestService requestService;

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testUserDashboard() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/user/user_dashboard"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/user_dashboard"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/user/logout"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.view().name("redirect:/logout"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testCreateRequestForm() throws Exception {
        mockMvc.perform(get("/user/create_request"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/request-form"))
                .andExpect(model().attributeExists("request"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testViewMyRequests() throws Exception {
        when(requestService.getUserRequests(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/my_requests"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/my_requests"))
                .andExpect(model().attributeExists("requests"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testViewCriminals() throws Exception {
        when(criminalService.findAllCriminals()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/criminals"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("user/criminals"))
                .andExpect(model().attributeExists("criminals"));
    }
}
