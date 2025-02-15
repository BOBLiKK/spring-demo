package ehu.java.springdemo.controller;

import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import static ehu.java.springdemo.constant.AttrbiuteNameConstant.CURRENT_USER;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
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
        mockMvc.perform(get("/user/user_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/user_dashboard"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/user/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/logout"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testCreateRequestForm() throws Exception {
        mockMvc.perform(get("/user/create_request"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/request-form"))
                .andExpect(model().attributeExists("request"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testViewMyRequests() throws Exception {
        when(requestService.getUserRequests(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/my_requests"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/my_requests"))
                .andExpect(model().attributeExists("requests"));
    }

    @WithMockUser(username = "testUser", roles = "USER")
    @Test
    void testViewCriminals() throws Exception {
        when(criminalService.findAllCriminals()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/user/criminals"))
                .andExpect(status().isOk())
                .andExpect(view().name("user/criminals"))
                .andExpect(model().attributeExists("criminals"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testSaveRequestSuccess() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "photoFile",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        MockHttpSession session = new MockHttpSession();
        User testUser = new User();
        testUser.setId(1L);
        session.setAttribute(CURRENT_USER, testUser);

        mockMvc.perform(multipart("/user/save_request")
                        .file(file)
                        .param("description", "Test request")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/user_dashboard"));

        verify(requestService).createRequest(any(Request.class), eq(1L), eq(file));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testSaveRequestWithError() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "photoFile",
                "test.jpg",
                "image/jpeg",
                "test content".getBytes()
        );

        MockHttpSession session = new MockHttpSession();
        User testUser = new User();
        testUser.setId(1L);
        session.setAttribute(CURRENT_USER, testUser);

        doThrow(new RuntimeException()).when(requestService)
                .createRequest(any(), any(), any());

        mockMvc.perform(multipart("/user/save_request")
                        .file(file)
                        .param("description", "Test request")
                        .session(session)
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/user/create_request?error"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetCriminalPhoto() throws Exception {
        byte[] testPhoto = {0x12, 0x34, 0x56};
        when(criminalService.getCriminalPhotoResponse(1L))
                .thenReturn(ResponseEntity.ok().body(testPhoto));

        mockMvc.perform(get("/user/criminals/photo/1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(testPhoto));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testGetCriminalPhotoNotFound() throws Exception {
        when(criminalService.getCriminalPhotoResponse(1L))
                .thenReturn(ResponseEntity.notFound().build());

        mockMvc.perform(get("/user/criminals/photo/1"))
                .andExpect(status().isNotFound());
    }
}

