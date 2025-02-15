package ehu.java.springdemo.service;

import ehu.java.springdemo.entity.Request;
import ehu.java.springdemo.entity.Request.RequestStatus;
import ehu.java.springdemo.entity.User;
import ehu.java.springdemo.repository.RequestRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.multipart.MultipartFile;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class RequestServiceTest {

    @Autowired
    private RequestService requestService;

    @MockitoBean
    private RequestRepository requestRepository;

    private final Long TEST_USER_ID = 1L;
    private final Long TEST_REQUEST_ID = 100L;
    private final String TEST_FIRST_NAME = "Alice";
    private final String TEST_LAST_NAME = "Smith";
    private final String TEST_COMMENT = "Test comment";
    private final LocalDate TEST_DOB = LocalDate.of(1995, 5, 15);
    private final String TEST_NATIONALITY = "TestNationality";
    private final String TEST_REASON = "Test reason";
    private final double TEST_REWARD = 5000.0;
    private final RequestStatus TEST_STATUS_PENDING = RequestStatus.PENDING;
    private final RequestStatus TEST_STATUS_APPROVED = RequestStatus.APPROVED;

    @Test
    void saveRequest_CallsRepositorySave() {
        Request request = createTestRequest();
        requestService.saveRequest(request);
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void findRequestById_RequestExists_ReturnsRequest() {
        Request request = createTestRequest();
        when(requestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(request));

        Optional<Request> result = requestService.findRequestById(TEST_REQUEST_ID);
        assertTrue(result.isPresent());
        assertEquals(TEST_REQUEST_ID, result.get().getId());
    }

    @Test
    void findRequestById_RequestNotExists_ReturnsEmpty() {
        when(requestRepository.findById(999L)).thenReturn(Optional.empty());
        Optional<Request> result = requestService.findRequestById(999L);
        assertFalse(result.isPresent());
    }

    @Test
    void findByUserId_ReturnsListOfRequests() {

        Object[] row = new Object[]{
                TEST_REQUEST_ID,
                TEST_COMMENT,
                TEST_DOB,
                TEST_FIRST_NAME,
                TEST_LAST_NAME,
                TEST_NATIONALITY,
                TEST_REASON,
                TEST_REWARD,
                TEST_STATUS_PENDING,
                TEST_USER_ID
        };
        List<Object[]> results = Collections.singletonList(row);

        when(requestRepository.findRequestSummaryByUserId(TEST_USER_ID)).thenReturn(results);

        List<Request> requests = requestService.findByUserId(TEST_USER_ID);
        assertEquals(1, requests.size());
        Request r = requests.get(0);
        assertEquals(TEST_REQUEST_ID, r.getId());
        assertEquals(TEST_COMMENT, r.getComment());
        assertEquals(TEST_DOB, r.getDateOfBirth());
        assertEquals(TEST_FIRST_NAME, r.getFirstName());
        assertEquals(TEST_LAST_NAME, r.getLastName());
        assertEquals(TEST_NATIONALITY, r.getNationality());
        assertEquals(TEST_REASON, r.getReasonForWanted());
        assertEquals(TEST_REWARD, r.getReward());
        assertEquals(TEST_STATUS_PENDING, r.getStatus());
        assertEquals(TEST_USER_ID, r.getUserId());
    }

    @Test
    void findAllRequests_ReturnsAllRequests() {
        Request request1 = createTestRequest();
        Request request2 = createTestRequest();
        request2.setId(200L);
        List<Request> requests = Arrays.asList(request1, request2);
        when(requestRepository.findAll()).thenReturn(requests);

        List<Request> result = requestService.findAllRequests();
        assertEquals(2, result.size());
        assertTrue(result.contains(request1));
        assertTrue(result.contains(request2));
    }

    @Test
    void updateRequestStatus_UpdatesStatusAndSaves() {
        Request request = createTestRequest();
        when(requestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(request));

        requestService.updateRequestStatus(TEST_REQUEST_ID, TEST_STATUS_APPROVED);
        assertEquals(TEST_STATUS_APPROVED, request.getStatus());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void createRequest_SetsPhotoUserIdStatusAndSaves() throws IOException {
        Request request = createTestRequest();
        request.setPhoto(null);
        byte[] photoBytes = new byte[]{1, 2, 3};
        MultipartFile photoFile = new MockMultipartFile("photo", "photo.jpg", "image/jpeg", photoBytes);

        requestService.createRequest(request, TEST_USER_ID, photoFile);

        assertArrayEquals(photoBytes, request.getPhoto());
        assertEquals(TEST_USER_ID, request.getUserId());
        assertEquals(TEST_STATUS_PENDING, request.getStatus());
        verify(requestRepository, times(1)).save(request);
    }

    @Test
    void getUserRequests_UserInSession_ReturnsUserRequests() {
        User user = new User();
        user.setId(TEST_USER_ID);
        HttpSession session = new MockHttpSession();
        session.setAttribute("currentUser", user);

        Request req1 = createTestRequest();
        Request req2 = createTestRequest();
        req2.setId(200L);
        List<Object[]> results = Arrays.asList(
                new Object[]{req1.getId(), req1.getComment(), req1.getDateOfBirth(),
                        req1.getFirstName(), req1.getLastName(), req1.getNationality(),
                        req1.getReasonForWanted(), req1.getReward(), req1.getStatus(), user.getId()},
                new Object[]{req2.getId(), req2.getComment(), req2.getDateOfBirth(),
                        req2.getFirstName(), req2.getLastName(), req2.getNationality(),
                        req2.getReasonForWanted(), req2.getReward(), req2.getStatus(), user.getId()}
        );
        when(requestRepository.findRequestSummaryByUserId(TEST_USER_ID)).thenReturn(results);
        List<Request> userRequests = requestService.getUserRequests(session);
        assertEquals(2, userRequests.size());
    }

    @Test
    void getUserRequests_NoUserInSession_ReturnsEmptyList() {
        HttpSession session = new MockHttpSession();
        List<Request> userRequests = requestService.getUserRequests(session);
        assertTrue(userRequests.isEmpty());
    }

    @Test
    void getRequestPhotoResponse_RequestExists_ReturnsPhotoResponse() {
        byte[] photoBytes = new byte[]{10, 20, 30};
        Request request = createTestRequest();
        request.setPhoto(photoBytes);
        when(requestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.of(request));

        ResponseEntity<byte[]> response = requestService.getRequestPhotoResponse(TEST_REQUEST_ID);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("image/*", response.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertArrayEquals(photoBytes, response.getBody());
    }

    @Test
    void getRequestPhotoResponse_RequestNotExists_ReturnsNotFound() {
        when(requestRepository.findById(TEST_REQUEST_ID)).thenReturn(Optional.empty());
        ResponseEntity<byte[]> response = requestService.getRequestPhotoResponse(TEST_REQUEST_ID);
        assertEquals(404, response.getStatusCodeValue());
    }

    private Request createTestRequest() {
        Request request = new Request();
        request.setId(TEST_REQUEST_ID);
        request.setComment(TEST_COMMENT);
        request.setDateOfBirth(TEST_DOB);
        request.setFirstName(TEST_FIRST_NAME);
        request.setLastName(TEST_LAST_NAME);
        request.setNationality(TEST_NATIONALITY);
        request.setReasonForWanted(TEST_REASON);
        request.setReward(TEST_REWARD);
        request.setStatus(TEST_STATUS_PENDING);
        request.setUserId(TEST_USER_ID);
        request.setPhoto(new byte[]{0});
        return request;
    }
}
