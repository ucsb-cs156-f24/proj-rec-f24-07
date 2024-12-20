package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.services.CurrentUserService;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import jakarta.servlet.ServletException;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.entities.User;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @Autowired
    CurrentUserService currentUserService;

    @MockBean
    UserRepository userRepository;

    @MockBean
    RequestTypeRepository requestTypeRepository;

    // Authorization tests for /api/recommendationrequest/admin/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @Test
    public void logged_out_users_cannot_get_all_admin() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest/alladmin"))
                            .andExpect(status().is(403)); // logged out users can't get all
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_users_can_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().is(200)); // logged
    }

    @Test
    public void logged_out_users_cannot_get_by_id() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest?id=7"))
                            .andExpect(status().is(403)); // logged out users can't get by id
    }

    // Authorization tests for /api/recommendationrequest/post
    // (Perhaps should also have these for put and delete)

    @Test
    public void logged_out_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendationrequest/post"))
                            .andExpect(status().is(403));
    }

    // // // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {
            // arrange
            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");
            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                            .requesterId(1)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest));

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(7L));
            String expectedJson = mapper.writeValueAsString(recommendationRequest);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void user_cannot_get_by_id_if_not_their_own_request() throws Exception {
            // arrange
            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");
            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                            .requesterId(2)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.of(recommendationRequest));

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(7L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {
            // arrange
            when(recommendationRequestRepository.findById(eq(7L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest?id=7"))
                            .andExpect(status().isNotFound()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findById(eq(7L));
            Map<String, Object> json = responseToJson(response);
            assertEquals("EntityNotFoundException", json.get("type"));
            assertEquals("RecommendationRequest with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_user_can_get_all_recommendationrequests() throws Exception {
            // arrange
            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");
            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterId(1)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            LocalDateTime submissionDate2 = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate2 = LocalDateTime.parse("2022-05-01T00:00:00");
            LocalDateTime neededByDate2 = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                            .requesterId(1)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate2)
                            .submissionDate(submissionDate2)
                            .completionDate(completionDate2)
                            .status("Pending")
                            .build();
                        
            ArrayList<RecommendationRequest> expectedRequests = new ArrayList<>();
            expectedRequests.addAll(Arrays.asList(recommendationRequest1, recommendationRequest2));

            when(recommendationRequestRepository.findAllByRequesterId(1L)).thenReturn(expectedRequests);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findAllByRequesterId(1L);
            String expectedJson = mapper.writeValueAsString(expectedRequests);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER", "ADMIN" })
    @Test
    public void admin_can_get_all_recommendationrequests() throws Exception {
            // arrange
            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");
            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterId(1)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            LocalDateTime submissionDate2 = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate2 = LocalDateTime.parse("2022-05-01T00:00:00");
            LocalDateTime neededByDate2 = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                            .requesterId(1)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate2)
                            .submissionDate(submissionDate2)
                            .completionDate(completionDate2)
                            .status("Pending")
                            .build();
                        
            ArrayList<RecommendationRequest> expectedRequests = new ArrayList<>();
            expectedRequests.addAll(Arrays.asList(recommendationRequest1, recommendationRequest2));

            when(recommendationRequestRepository.findAll()).thenReturn(expectedRequests);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/alladmin"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedRequests);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void a_user_can_post_a_new_recommendationrequest() throws Exception {
            // arrange
            LocalDateTime submissionDate = LocalDateTime.now();
            submissionDate = submissionDate.minusNanos(submissionDate.getNano());

            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterId(1)
                            .professorId(1)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(null)
                            .status("Pending")
                            .build();

            User professor = User.builder().admin(true).build();

            RequestType requestType = RequestType.builder().requestType("PhD program").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(professor));

            when(requestTypeRepository.findByRequestType("PhD program")).thenReturn(Optional.of(requestType));

            when(recommendationRequestRepository.save(eq(recommendationRequest1))).thenReturn(recommendationRequest1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/recommendationrequest/post?requesterId=1&professorId=1&requestType=PhD program&details=I want to apply to a PhD program&neededByDate=2022-06-01T00:00:00&submissionDate="+submissionDate.toString()+"&completionDate=null&status=Pending")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).save(recommendationRequest1);
            String expectedJson = mapper.writeValueAsString(recommendationRequest1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void a_user_cannot_post_a_new_recommendationrequest_professor_does_not_exist() throws Exception {
            // arrange
            LocalDateTime currentDate = LocalDateTime.now();
            final LocalDateTime submissionDate = currentDate.minusNanos(currentDate.getNano());

            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterId(1L)
                            .professorId(1L)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(null)
                            .status("Pending")
                            .build();

            RequestType requestType = RequestType.builder().requestType("PhD program").build();

            when(userRepository.findById(1L)).thenReturn(Optional.empty());

            when(requestTypeRepository.findByRequestType("PhD program")).thenReturn(Optional.of(requestType));

            when(recommendationRequestRepository.save(eq(recommendationRequest1))).thenReturn(recommendationRequest1);

            // act
            MvcResult response = mockMvc.perform(
                post("/api/recommendationrequest/post?requesterId=1&professorId=1&requestType=PhD program&details=I want to apply to a PhD program&neededByDate=2022-06-01T00:00:00&submissionDate="+submissionDate.toString()+"&completionDate=null&status=Pending")
                .with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

            // assert
            assertEquals("User with id 1 not found", response.getResolvedException().getMessage());
        
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void a_user_cannot_post_a_new_recommendationrequest_professor_is_not_admin() throws Exception {
            // arrange
            LocalDateTime currentDate = LocalDateTime.now();
            final LocalDateTime submissionDate = currentDate.minusNanos(currentDate.getNano());

            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterId(1L)
                            .professorId(1L)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(null)
                            .status("Pending")
                            .build();

            User professor = User.builder().admin(false).build();

            RequestType requestType = RequestType.builder().requestType("PhD program").build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(professor));

            when(requestTypeRepository.findByRequestType("PhD program")).thenReturn(Optional.of(requestType));

            when(recommendationRequestRepository.save(eq(recommendationRequest1))).thenReturn(recommendationRequest1);

            // act
            ServletException exception = assertThrows(ServletException.class, () -> {
                mockMvc.perform(
                                post("/api/recommendationrequest/post?requesterId=1&professorId=1&requestType=PhD program&details=I want to apply to a PhD program&neededByDate=2022-06-01T00:00:00&submissionDate="+submissionDate.toString()+"&completionDate=null&status=Pending")
                                                .with(csrf()))
                        .andExpect(status().is4xxClientError()).andReturn();
            });
        
            // assert
            assertEquals("Request processing failed: java.lang.IllegalArgumentException: Requested professor is not an admin.", exception.getMessage());
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void a_user_cannot_post_a_new_recommendationrequest_request_type_does_not_exist() throws Exception {
            // arrange
            LocalDateTime currentDate = LocalDateTime.now();
            final LocalDateTime submissionDate = currentDate.minusNanos(currentDate.getNano());

            LocalDateTime neededByDate = LocalDateTime.parse("2022-06-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterId(1L)
                            .professorId(1L)
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .neededByDate(neededByDate)
                            .submissionDate(submissionDate)
                            .completionDate(null)
                            .status("Pending")
                            .build();

            User professor = User.builder().admin(true).build();

            when(userRepository.findById(1L)).thenReturn(Optional.of(professor));

            when(requestTypeRepository.findByRequestType("PhD program")).thenReturn(Optional.empty());

            when(recommendationRequestRepository.save(eq(recommendationRequest1))).thenReturn(recommendationRequest1);

            // act
            MvcResult response = mockMvc.perform(
                post("/api/recommendationrequest/post?requesterId=1&professorId=1&requestType=PhD program&details=I want to apply to a PhD program&neededByDate=2022-06-01T00:00:00&submissionDate="+submissionDate.toString()+"&completionDate=null&status=Pending")
                .with(csrf()))
                .andExpect(status().is4xxClientError()).andReturn();

            // assert
            assertEquals("RequestType with id PhD program not found", response.getResolvedException().getMessage());
    }
}
