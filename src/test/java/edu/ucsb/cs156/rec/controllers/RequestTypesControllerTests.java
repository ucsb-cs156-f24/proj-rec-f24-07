package edu.ucsb.cs156.rec.controllers;

import edu.ucsb.cs156.rec.repositories.UserRepository;
import edu.ucsb.cs156.rec.testconfig.TestConfig;
import edu.ucsb.cs156.rec.ControllerTestCase;
import edu.ucsb.cs156.rec.entities.RequestType;
import edu.ucsb.cs156.rec.repositories.RequestTypeRepository;
import edu.ucsb.cs156.rec.services.RequestTypeService;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = RequestTypesController.class)
@Import(TestConfig.class)
public class RequestTypesControllerTests extends ControllerTestCase {

        @MockBean
        RequestTypeRepository requestTypeRepository;

        @MockBean
        RequestTypeService requestTypeService;

        @MockBean
        UserRepository userRepository;

        // Authorization tests for /api/phones/admin/all

        @Test
        public void logged_out_users_cannot_get_all() throws Exception {
                mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().is(403)); // logged out users can't get all
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_users_can_get_all() throws Exception {
                mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().is(200)); // logged
        }

        @Test
        public void logged_out_users_cannot_get_by_id() throws Exception {
                mockMvc.perform(get("/api/requesttypes?id=7"))
                                .andExpect(status().is(403)); // logged out users can't get by id
        }

        // Authorization tests for /api/phones/post
        // (Perhaps should also have these for put and delete)

        @Test
        public void logged_out_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttypes/post"))
                                .andExpect(status().is(403));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_regular_users_cannot_post() throws Exception {
                mockMvc.perform(post("/api/requesttypes/post"))
                                .andExpect(status().is(403)); // only admins can post
        }

        // // Tests with mocks for database actions

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_exists() throws Exception {

                // arrange

                RequestType requestType = RequestType.builder()
                                .requestType("Colloquia")
                                .build();

                when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.of(requestType));  // Check not sure why id is 7

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes?id=7"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findById(eq(7L));
                String expectedJson = mapper.writeValueAsString(requestType);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void test_that_logged_in_user_can_get_by_id_when_the_id_does_not_exist() throws Exception {

                // arrange

                when(requestTypeRepository.findById(eq(7L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes?id=7"))
                                .andExpect(status().isNotFound()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findById(eq(7L));
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityNotFoundException", json.get("type"));
                assertEquals("RequestType with id 7 not found", json.get("message"));
        }

        @WithMockUser(roles = { "USER" })
        @Test
        public void logged_in_user_can_get_all_request_types() throws Exception {

                // arrange
                
                RequestType requestType1 = RequestType.builder()
                                .requestType("Colloquia")
                                .build();
                
                RequestType requestType2 = RequestType.builder()
                                .requestType("Office Hours")
                                .build();

                ArrayList<RequestType> expectedRequestTypes = new ArrayList<>();
                expectedRequestTypes.addAll(Arrays.asList(requestType1, requestType2));

                when(requestTypeRepository.findAll()).thenReturn(expectedRequestTypes);

                // act
                MvcResult response = mockMvc.perform(get("/api/requesttypes/all"))
                                .andExpect(status().isOk()).andReturn();

                // assert

                verify(requestTypeRepository, times(1)).findAll();
                String expectedJson = mapper.writeValueAsString(expectedRequestTypes);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void an_admin_user_can_post_a_new_request_type() throws Exception {
                // arrange

                RequestType requestType1 = RequestType.builder()
                                .requestType("Colloquia")
                                .build();

                when(requestTypeService.trySave(eq(requestType1))).thenReturn(Optional.of(requestType1));

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Colloquia")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeService, times(1)).trySave(requestType1);
                String expectedJson = mapper.writeValueAsString(requestType1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "INSTRUCTOR", "USER" })
        @Test
        public void an_instructor_can_post_a_new_request_type() throws Exception {
                // arrange

                RequestType requestType1 = RequestType.builder()
                                .requestType("Colloquia")
                                .build();

                when(requestTypeService.trySave(eq(requestType1))).thenReturn(Optional.of(requestType1));

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Colloquia")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeService, times(1)).trySave(requestType1);
                String expectedJson = mapper.writeValueAsString(requestType1);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(expectedJson, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_post_a_duplicate_request_type() throws Exception {
                // arrange
                RequestType requestType1 = RequestType.builder().id(0L)
                                .requestType("Colloquia")
                                .build();

                when(requestTypeService.trySave(requestType1)).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                post("/api/requesttypes/post?requestType=Colloquia")
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(requestTypeService, times(1)).trySave(requestType1);
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityAlreadyExistsException", json.get("type"));
                assertEquals("RequestType Colloquia already exists", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_delete_a_request_type() throws Exception {
                // arrange

                RequestType requestType1 = RequestType.builder()
                                .requestType("Colloquia")
                                .build();

                when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.of(requestType1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/requesttypes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(15L);
                verify(requestTypeRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("RequestType with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "INSTRUCTOR", "USER" })
        @Test
        public void instructor_can_delete_a_request_type() throws Exception {
                // arrange

                RequestType requestType1 = RequestType.builder()
                                .requestType("Colloquia")
                                .build();

                when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.of(requestType1));

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/requesttypes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(15L);
                verify(requestTypeRepository, times(1)).delete(any());

                Map<String, Object> json = responseToJson(response);
                assertEquals("RequestType with id 15 deleted", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_tries_to_delete_non_existant_request_type_and_gets_right_error_message()
                        throws Exception {
                // arrange

                when(requestTypeRepository.findById(eq(15L))).thenReturn(Optional.empty());

                // act
                MvcResult response = mockMvc.perform(
                                delete("/api/requesttypes?id=15")
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(15L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RequestType with id 15 not found", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_can_edit_an_existing_request_type() throws Exception {
                // arrange
                RequestType requestTypeOrig = RequestType.builder().id(67L)
                                .requestType("Colloquia")
                                .build();

                RequestType requestTypeEdited = RequestType.builder().id(67L)
                                .requestType("Colloquium")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                when(requestTypeService.trySave(eq(requestTypeEdited))).thenReturn(Optional.of(requestTypeEdited));
                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isOk()).andReturn();

                // assert
                
                verify(requestTypeService, times(1)).trySave(requestTypeEdited);
                verify(requestTypeRepository, times(1)).findById(67L);
                String responseString = response.getResponse().getContentAsString();
                assertEquals(requestBody, responseString);
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_to_duplicate_request_type() throws Exception {
                // arrange
                RequestType requestTypeOrig = RequestType.builder().id(67L)
                                .requestType("Colloquia")
                                .build();

                RequestType requestTypeEdited = RequestType.builder().id(67L)
                                .requestType("Colloquium")
                                .build();

                String requestBody = mapper.writeValueAsString(requestTypeEdited);

                when(requestTypeService.trySave(eq(requestTypeEdited))).thenReturn(Optional.empty());
                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.of(requestTypeOrig));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isBadRequest()).andReturn();

                // assert
                verify(requestTypeService, times(1)).trySave(requestTypeEdited);
                verify(requestTypeRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("EntityAlreadyExistsException", json.get("type"));
                assertEquals("RequestType Colloquium already exists", json.get("message"));
        }

        @WithMockUser(roles = { "ADMIN", "USER" })
        @Test
        public void admin_cannot_edit_request_type_that_does_not_exist() throws Exception {
                // arrange

                RequestType editedrequestType = RequestType.builder()
                                .requestType("Colloquium")
                                .build();

                String requestBody = mapper.writeValueAsString(editedrequestType);

                when(requestTypeRepository.findById(eq(67L))).thenReturn(Optional.empty());
                when(requestTypeService.trySave(eq(editedrequestType))).thenReturn(Optional.of(editedrequestType));

                // act
                MvcResult response = mockMvc.perform(
                                put("/api/requesttypes?id=67")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .characterEncoding("utf-8")
                                                .content(requestBody)
                                                .with(csrf()))
                                .andExpect(status().isNotFound()).andReturn();

                // assert
                verify(requestTypeRepository, times(1)).findById(67L);
                Map<String, Object> json = responseToJson(response);
                assertEquals("RequestType with id 67 not found", json.get("message"));

        }
}
