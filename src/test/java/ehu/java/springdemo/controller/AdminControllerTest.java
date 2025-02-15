package ehu.java.springdemo.controller;

import ehu.java.springdemo.config.SecurityConfig;
import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.service.CriminalService;
import ehu.java.springdemo.service.RequestService;
import ehu.java.springdemo.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Collections;
import java.util.Optional;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@WebMvcTest(AdminController.class)
@Import(SecurityConfig.class)
public class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CriminalService criminalService;

    @MockitoBean
    private RequestService requestService;

    @MockitoBean
    private UserService userService;


    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testAdminDashboard() throws Exception {
        mockMvc.perform(get("/admin/admin_dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/admin_dashboard"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testViewCriminals() throws Exception {
        when(criminalService.findAllCriminals()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/admin/criminals"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/criminals"))
                .andExpect(model().attributeExists("criminals"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testNewCriminalForm() throws Exception {
        mockMvc.perform(get("/admin/criminals/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/criminal-form"))
                .andExpect(model().attributeExists("criminal"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testEditCriminalForm() throws Exception {
        Criminal criminal = new Criminal();
        when(criminalService.findCriminalById(1L)).thenReturn(Optional.of(criminal));

        mockMvc.perform(get("/admin/criminals/edit/1"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/criminal-form"))
                .andExpect(model().attributeExists("criminal"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testDeleteCriminal() throws Exception {
        mockMvc.perform(post("/admin/criminals/delete/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/criminals"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testViewAllRequests() throws Exception {
        when(requestService.findAllRequests()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/admin/requests"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/requests"))
                .andExpect(model().attributeExists("requests"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testApproveRequest() throws Exception {
        mockMvc.perform(post("/admin/requests/1/approve"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/requests"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testDeclineRequest() throws Exception {
        mockMvc.perform(post("/admin/requests/1/decline"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/requests"));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testGetCriminalPhoto() throws Exception {
        byte[] testPhoto = new byte[]{0x12, 0x34, 0x56};
        when(criminalService.getCriminalPhotoResponse(1L))
                .thenReturn(ResponseEntity.ok().body(testPhoto));

        mockMvc.perform(get("/admin/criminals/photo/1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(testPhoto));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testGetRequestPhoto() throws Exception {
        byte[] testPhoto = new byte[]{0x65, 0x43, 0x21};
        when(requestService.getRequestPhotoResponse(1L))
                .thenReturn(ResponseEntity.ok().body(testPhoto));

        mockMvc.perform(get("/admin/requests/photo/1"))
                .andExpect(status().isOk())
                .andExpect(content().bytes(testPhoto));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testAddCriminal() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "photoFile",
                "test.jpg",
                "image/jpeg",
                "test image content".getBytes()
        );

        mockMvc.perform(multipart("/admin/criminals/add")
                        .file(file)
                        .param("name", "John Doe")
                        .param("crimeDetails", "Some details")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/criminals"));

        verify(criminalService).createCriminal(any(Criminal.class), eq(file));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testUpdateCriminal() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "photoFile",
                "update.jpg",
                "image/jpeg",
                "updated content".getBytes()
        );

        mockMvc.perform(multipart("/admin/criminals/update/1")
                        .file(file)
                        .param("name", "Updated Name")
                        .param("crimeDetails", "Updated details")
                        .with(csrf()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/criminals?success=CriminalUpdated"));

        verify(criminalService).updateCriminal(eq(1L), any(Criminal.class), eq(file));
    }

    @WithMockUser(username = "adminUser", roles = "ADMIN")
    @Test
    void testLogout() throws Exception {
        mockMvc.perform(get("/admin/logout"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/logout"));
    }

    @Test
    @WithMockUser(username = "user", roles = "USER")
    void testAdminDashboardAccessDeniedForUserRole() throws Exception {
        mockMvc.perform(get("/admin/admin_dashboard"))
                .andExpect(status().isForbidden());
    }
}
