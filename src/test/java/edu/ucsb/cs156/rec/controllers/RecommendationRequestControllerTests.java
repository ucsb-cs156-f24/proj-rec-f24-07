package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RecommendationRequest;
import edu.ucsb.cs156.rec.repositories.RecommendationRequestRepository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RecommendationRequestController.class)
@Import(TestConfig.class)
public class RecommendationRequestControllerTests extends ControllerTestCase {
    @MockBean
    RecommendationRequestRepository recommendationRequestRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/ucsbdates/admin/all

    @Test
    public void logged_out_users_cannot_get_all() throws Exception {
            mockMvc.perform(get("/api/recommendationrequest/all"))
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

    @WithMockUser(roles = { "USER" })
    @Test
    public void logged_in_regular_users_cannot_post() throws Exception {
            mockMvc.perform(post("/api/recommendationrequest/post"))
                            .andExpect(status().is(403)); // only admins can post
    }

    // // // Tests with mocks for database actions

    @WithMockUser(roles = { "USER" })
    @Test
    public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

            // arrange
            LocalDateTime dateReq = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime dateNeed = LocalDateTime.parse("2022-05-01T00:00:00");

            RecommendationRequest recommendationRequest = RecommendationRequest.builder()
                            .requesterEmail("cgaucho@ucsb.edu")
                            .requesterName("Chris Gaucho")
                            .professorEmail("phtcon@ucsb.edu")
                            .professorName("Phill Conrad")
                            .requestType("PhD program")
                            .details("I want to apply to a PhD program")
                            .submissionDate(dateReq)
                            .submissionDate(dateNeed)
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

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterEmail("cgaucho@ucsb.edu")
                            .requesterName("Chris Gaucho")
                            .professorEmail("phtcon@ucsb.edu")
                            .professorName("Phill Conrad")
                            .requestType("PhD program")
                            .details("PhD CS UCSB")
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            LocalDateTime submissionDate2 = LocalDateTime.parse("2022-05-20T00:00:00");
            LocalDateTime completionDate2 = LocalDateTime.parse("2022-11-15T00:00:00");

            RecommendationRequest recommendationRequest2 = RecommendationRequest.builder()
                            .requesterEmail("ldelplaya@ucsb.edu")
                            .requesterName("Lana Del Playa")
                            .professorEmail("richert@ucsb.edu")
                            .professorName("Richert Wang")
                            .requestType("PhD program")
                            .details("PhD CS Stanford")
                            .submissionDate(submissionDate2)
                            .completionDate(completionDate2)
                            .status("Pending")
                            .build();

            ArrayList<RecommendationRequest> expectedRequests = new ArrayList<>();
            expectedRequests.addAll(Arrays.asList(recommendationRequest1, recommendationRequest2));

            when(recommendationRequestRepository.findAll()).thenReturn(expectedRequests);

            // act
            MvcResult response = mockMvc.perform(get("/api/recommendationrequest/all"))
                            .andExpect(status().isOk()).andReturn();

            // assert

            verify(recommendationRequestRepository, times(1)).findAll();
            String expectedJson = mapper.writeValueAsString(expectedRequests);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void an_admin_user_can_post_a_new_recommendationrequest() throws Exception {
            // arrange

            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterEmail("cgaucho@ucsb.edu")
                            .requesterName("Chris Gaucho")
                            .professorEmail("phtcon@ucsb.edu")
                            .professorName("Phill Conrad")
                            .requestType("PhD program")
                            .details("PhD CS UCSB")
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            when(recommendationRequestRepository.save(eq(recommendationRequest1))).thenReturn(recommendationRequest1);

            // act
            MvcResult response = mockMvc.perform(
                            post("/api/recommendationrequest/post?requesterEmail=cgaucho@ucsb.edu&requesterName=Chris Gaucho&professorEmail=phtcon@ucsb.edu&professorName=Phill Conrad&requestType=PhD program&details=PhD CS UCSB&submissionDate=2022-04-20T00:00:00&completionDate=2022-05-01T00:00:00&status=Pending")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).save(recommendationRequest1);
            String expectedJson = mapper.writeValueAsString(recommendationRequest1);
            String responseString = response.getResponse().getContentAsString();
            assertEquals(expectedJson, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_delete_a_request() throws Exception {
            // arrange

            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");

            RecommendationRequest recommendationRequest1 = RecommendationRequest.builder()
                            .requesterEmail("cgaucho@ucsb.edu")
                            .requesterName("Chris Gaucho")
                            .professorEmail("phtcon@ucsb.edu")
                            .professorName("Phill Conrad")
                            .requestType("PhD program")
                            .details("PhD CS UCSB")
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.of(recommendationRequest1));

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/recommendationrequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(15L);
            verify(recommendationRequestRepository, times(1)).delete(any());

            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 15 deleted", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_tries_to_delete_non_existant_recommendationrequest_and_gets_right_error_message()
                    throws Exception {
            // arrange

            when(recommendationRequestRepository.findById(eq(15L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            delete("/api/recommendationrequest?id=15")
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(15L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 15 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_can_edit_an_existing_recommendationrequest() throws Exception {
            // arrange

            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");

            LocalDateTime submissionDate2 = LocalDateTime.parse("2022-05-20T00:00:00");
            LocalDateTime completionDate2 = LocalDateTime.parse("2022-11-15T00:00:00");

            RecommendationRequest recommendationRequestOrig = RecommendationRequest.builder()
                            .requesterEmail("cgaucho@ucsb.edu")
                            .requesterName("Chris Gaucho")
                            .professorEmail("phtcon@ucsb.edu")
                            .professorName("Phill Conrad")
                            .requestType("PhD program")
                            .details("PhD CS UCSB")
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            RecommendationRequest recommendationRequestEdited = RecommendationRequest.builder()
                            .requesterEmail("ldelplaya@ucsb.edu")
                            .requesterName("Lana Del Playa")
                            .professorEmail("richert@ucsb.edu")
                            .professorName("Richert Wang")
                            .requestType("PhD program")
                            .details("PhD CS Stanford")
                            .submissionDate(submissionDate2)
                            .completionDate(completionDate2)
                            .status("Pending")
                            .build();

            String requestBody = mapper.writeValueAsString(recommendationRequestEdited);

            when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.of(recommendationRequestOrig));

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/recommendationrequest?id=67")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isOk()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(67L);
            verify(recommendationRequestRepository, times(1)).save(recommendationRequestEdited); // should be saved with correct user
            String responseString = response.getResponse().getContentAsString();
            assertEquals(requestBody, responseString);
    }

    @WithMockUser(roles = { "ADMIN", "USER" })
    @Test
    public void admin_cannot_edit_recommendationrequest_that_does_not_exist() throws Exception {
            // arrange

            LocalDateTime submissionDate = LocalDateTime.parse("2022-04-20T00:00:00");
            LocalDateTime completionDate = LocalDateTime.parse("2022-05-01T00:00:00");

            RecommendationRequest recommendationEditedRequest = RecommendationRequest.builder()
                            .requesterEmail("cgaucho@ucsb.edu")
                            .requesterName("Chris Gaucho")
                            .professorEmail("phtcon@ucsb.edu")
                            .professorName("Phill Conrad")
                            .requestType("PhD program")
                            .details("PhD CS UCSB")
                            .submissionDate(submissionDate)
                            .completionDate(completionDate)
                            .status("Pending")
                            .build();

            String requestBody = mapper.writeValueAsString(recommendationEditedRequest);

            when(recommendationRequestRepository.findById(eq(67L))).thenReturn(Optional.empty());

            // act
            MvcResult response = mockMvc.perform(
                            put("/api/recommendationrequest?id=67")
                                            .contentType(MediaType.APPLICATION_JSON)
                                            .characterEncoding("utf-8")
                                            .content(requestBody)
                                            .with(csrf()))
                            .andExpect(status().isNotFound()).andReturn();

            // assert
            verify(recommendationRequestRepository, times(1)).findById(67L);
            Map<String, Object> json = responseToJson(response);
            assertEquals("RecommendationRequest with id 67 not found", json.get("message"));
    }
}
