package ehu.java.springdemo.service;

import ehu.java.springdemo.entity.Criminal;
import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.entity.Request.RequestStatus;
import ehu.java.springdemo.repository.CriminalRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class CriminalServiceTest {

    @Autowired
    private CriminalService criminalService;

    @MockitoBean
    private CriminalRepository criminalRepository;

    @MockitoBean
    private RequestService requestService;

    @Test
    void findAllCriminals_ReturnsAllCriminals() {
        Criminal criminal1 = createTestCriminal(1L);
        Criminal criminal2 = createTestCriminal(2L);
        List<Criminal> criminals = Arrays.asList(criminal1, criminal2);
        when(criminalRepository.findAll()).thenReturn(criminals);

        List<Criminal> result = criminalService.findAllCriminals();
        assertEquals(2, result.size());
        assertTrue(result.contains(criminal1));
        assertTrue(result.contains(criminal2));
    }

    @Test
    void findCriminalById_CriminalExists_ReturnsCriminal() {
        Criminal criminal = createTestCriminal(1L);
        when(criminalRepository.findById(1L)).thenReturn(Optional.of(criminal));

        Optional<Criminal> result = criminalService.findCriminalById(1L);
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void findCriminalById_CriminalNotExists_ReturnsEmpty() {
        when(criminalRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Criminal> result = criminalService.findCriminalById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void saveCriminal_CallsRepositorySave() {
        Criminal criminal = createTestCriminal(1L);
        criminalService.saveCriminal(criminal);
        verify(criminalRepository, times(1)).save(criminal);
    }

    @Test
    void deleteCriminalById_CallsRepositoryDeleteById() {
        Long id = 1L;
        criminalService.deleteCriminalById(id);
        verify(criminalRepository, times(1)).deleteById(id);
    }

    @Test
    void getCriminalPhotoResponse_CriminalExists_ReturnsPhotoResponse() {
        byte[] photoBytes = new byte[]{1, 2, 3};
        Criminal criminal = createTestCriminal(1L);
        criminal.setPhoto(photoBytes);
        when(criminalRepository.findById(1L)).thenReturn(Optional.of(criminal));

        ResponseEntity<byte[]> response = criminalService.getCriminalPhotoResponse(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("image/*", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertArrayEquals(photoBytes, response.getBody());
    }

    @Test
    void getCriminalPhotoResponse_CriminalNotExists_ReturnsNotFound() {
        when(criminalRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = criminalService.getCriminalPhotoResponse(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void createCriminal_WithNonEmptyPhoto_SetsPhotoAndSaves() throws IOException {
        Criminal criminal = createTestCriminal(1L);
        byte[] photoBytes = new byte[]{10, 20, 30};
        MultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", photoBytes);

        criminalService.createCriminal(criminal, photoFile);
        assertArrayEquals(photoBytes, criminal.getPhoto());
        verify(criminalRepository, times(1)).save(criminal);
    }

    @Test
    void createCriminal_WithEmptyPhoto_DoesNotSetPhotoAndSaves() throws IOException {
        Criminal criminal = createTestCriminal(1L);
        MultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", new byte[0]);

        criminalService.createCriminal(criminal, photoFile);
        assertNull(criminal.getPhoto());
        verify(criminalRepository, times(1)).save(criminal);
    }

    @Test
    void updateCriminal_UpdatesFieldsAndPhoto() throws IOException {
        Criminal existingCriminal = createTestCriminal(1L);
        when(criminalRepository.findById(1L)).thenReturn(Optional.of(existingCriminal));

        Criminal updatedData = new Criminal();
        updatedData.setFirstName("UpdatedFirstName");
        updatedData.setLastName("UpdatedLastName");
        updatedData.setDateOfBirth(LocalDate.of(1990, 1, 1));
        updatedData.setNationality("UpdatedNationality");
        updatedData.setReasonForWanted("UpdatedReason");
        updatedData.setReward(2.00);
        updatedData.setComment("UpdatedComment");

        byte[] newPhoto = new byte[]{50, 60, 70};
        MultipartFile photoFile = new MockMultipartFile("photo", "newphoto.jpg", "image/jpeg", newPhoto);

        criminalService.updateCriminal(1L, updatedData, photoFile);

        assertEquals("UpdatedFirstName", existingCriminal.getFirstName());
        assertEquals("UpdatedLastName", existingCriminal.getLastName());
        assertEquals(LocalDate.of(1990, 1, 1), existingCriminal.getDateOfBirth());
        assertEquals("UpdatedNationality", existingCriminal.getNationality());
        assertEquals("UpdatedReason", existingCriminal.getReasonForWanted());
        assertEquals(2.00, existingCriminal.getReward());
        assertEquals("UpdatedComment", existingCriminal.getComment());
        assertArrayEquals(newPhoto, existingCriminal.getPhoto());

        verify(criminalRepository, times(1)).save(existingCriminal);
    }

    @Test
    void updateCriminal_WithoutPhotoFile_KeepsExistingPhoto() throws IOException {
        byte[] originalPhoto = new byte[]{5, 5, 5};
        Criminal existingCriminal = createTestCriminal(1L);
        existingCriminal.setPhoto(originalPhoto);
        when(criminalRepository.findById(1L)).thenReturn(Optional.of(existingCriminal));

        Criminal updatedData = new Criminal();
        updatedData.setFirstName("NewFirstName");
        updatedData.setLastName("NewLastName");
        updatedData.setDateOfBirth(LocalDate.of(2000, 2, 2));
        updatedData.setNationality("NewNationality");
        updatedData.setReasonForWanted("NewReason");
        updatedData.setReward(2.00);
        updatedData.setComment("NewComment");

        MultipartFile emptyPhoto = new MockMultipartFile("photo", "empty.jpg", "image/jpeg", new byte[0]);

        criminalService.updateCriminal(1L, updatedData, emptyPhoto);

        assertEquals("NewFirstName", existingCriminal.getFirstName());
        assertEquals("NewLastName", existingCriminal.getLastName());
        assertEquals(LocalDate.of(2000, 2, 2), existingCriminal.getDateOfBirth());
        assertEquals("NewNationality", existingCriminal.getNationality());
        assertEquals("NewReason", existingCriminal.getReasonForWanted());
        assertEquals(2.00, existingCriminal.getReward());
        assertEquals("NewComment", existingCriminal.getComment());
        assertArrayEquals(originalPhoto, existingCriminal.getPhoto());

        verify(criminalRepository, times(1)).save(existingCriminal);
    }

    @Test
    void createCriminalFromRequest_CreatesCriminalAndUpdatesRequestStatus() {
        Request request = createTestRequest(1L);
        when(requestService.findRequestById(1L)).thenReturn(Optional.of(request));

        criminalService.createCriminalFromRequest(1L);

        ArgumentCaptor<Criminal> criminalCaptor = ArgumentCaptor.forClass(Criminal.class);
        verify(criminalRepository, times(1)).save(criminalCaptor.capture());
        Criminal savedCriminal = criminalCaptor.getValue();

        assertEquals(request.getFirstName(), savedCriminal.getFirstName());
        assertEquals(request.getLastName(), savedCriminal.getLastName());
        assertEquals(request.getDateOfBirth(), savedCriminal.getDateOfBirth());
        assertEquals(request.getNationality(), savedCriminal.getNationality());
        assertEquals(request.getReasonForWanted(), savedCriminal.getReasonForWanted());
        assertEquals(request.getReward(), savedCriminal.getReward());
        assertArrayEquals(request.getPhoto(), savedCriminal.getPhoto());
        assertEquals(request.getComment(), savedCriminal.getComment());

        verify(requestService, times(1)).updateRequestStatus(1L, RequestStatus.APPROVED);
    }

    private Criminal createTestCriminal(Long id) {
        Criminal criminal = new Criminal();
        criminal.setId(id);
        criminal.setFirstName("John");
        criminal.setLastName("Doe");
        criminal.setDateOfBirth(LocalDate.of(1980, 1, 1));
        criminal.setNationality("TestNationality");
        criminal.setReasonForWanted("TestReason");
        criminal.setReward(1.00);
        criminal.setComment("TestComment");
        return criminal;
    }

    private Request createTestRequest(Long id) {
        Request request = new Request();
        request.setId(id);
        request.setFirstName("Alice");
        request.setLastName("Smith");
        request.setDateOfBirth(LocalDate.of(1995, 5, 15));
        request.setNationality("TestCountry");
        request.setReasonForWanted("TestReason");
        request.setReward(1.00);
        request.setComment("TestComment");
        request.setPhoto(new byte[]{99, 100, 101});
        return request;
    }
}
