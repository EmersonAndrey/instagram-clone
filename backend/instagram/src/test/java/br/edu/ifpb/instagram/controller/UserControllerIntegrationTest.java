package br.edu.ifpb.instagram.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControllerIntegrationTest {

        @Autowired
        private MockMvc mockMvc;

        @Autowired
        private ObjectMapper objectMapper;

        private UserDetailsRequest newUser;
        private String token;
        private Long userId;

        @BeforeAll
        void signInUserBeforeUseEndPoints() throws Exception {

                newUser = new UserDetailsRequest(
                                null,
                                "teste@gmail.com",
                                "87654321",
                                "Teste Junior",
                                "testezin");

                MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUser)))
                                .andReturn();

                String jsonResponse = signupResult.getResponse().getContentAsString();
                userId = objectMapper.readTree(jsonResponse).get("id").asLong();

                LoginRequest loginRequest = new LoginRequest("testezin", "87654321");
                MvcResult signinResult = mockMvc.perform(post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andReturn();

                token = objectMapper.readTree(signinResult.getResponse().getContentAsString()).get("token").asText();
        }

        @Test
        @Order(-2)
        void whenGetUserById_shouldReturnUser() throws Exception {

                mockMvc.perform(get("/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().is(200))
                                .andExpect(jsonPath("$.email").value(newUser.email()))
                                .andExpect(jsonPath("$.username").value(newUser.username()));
        }
        @Test
        @Order(-1)
        void whenGetUserByIdWithoutPermission_shouldThrowException() throws Exception {

                mockMvc.perform(get("/users/" + userId)
                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().is(403));
        }

        @Test
        @Order(0)
        void whenGetUserList_shouldReturnList() throws Exception {

                mockMvc.perform(get("/users")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().is(200));
        }
        @Test
        @Order(1)
        void whenGetUserListUsingInvalidParameters_shouldThrowException() throws Exception {

                mockMvc.perform(get("/users/getList")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(2)
        void whenUpdateUser_shouldReturnUpdatedUser() throws Exception {

                UserDetailsRequest editedUser = new UserDetailsRequest(
                                userId,
                                "teste@gmail.com",
                                "11111111",
                                "Teste da Silva",
                                "testezin");

                mockMvc.perform(put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(editedUser))
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().is(200));
        }
        @Test
        @Order(3)
        void whenUpdateUserWhitoutData_shouldThrowException() throws Exception {

                mockMvc.perform(put("/users")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(null))
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @Order(4)
        void whenDeleteUser_shouldDeletedUser() throws Exception {

                mockMvc.perform(delete("/users/" + userId)
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().is(200));
        }

        @Test
        @Order(5)
        void whenDeleteUserWithNonNumericId_shouldDeletedUser() throws Exception {

                newUser = new UserDetailsRequest(
                                null,
                                "teste@gmail.com",
                                "87654321",
                                "Teste Junior",
                                "testezin");

                MvcResult signupResult = mockMvc.perform(post("/auth/signup")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(newUser)))
                                .andReturn();

                String jsonResponse = signupResult.getResponse().getContentAsString();
                userId = objectMapper.readTree(jsonResponse).get("id").asLong();

                LoginRequest loginRequest = new LoginRequest("testezin", "87654321");
                MvcResult signinResult = mockMvc.perform(post("/auth/signin")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(loginRequest)))
                                .andReturn();

                token = objectMapper.readTree(signinResult.getResponse().getContentAsString()).get("token").asText();

                mockMvc.perform(delete("/users/abc")
                                .header("Authorization", "Bearer " + token))
                                .andExpect(status().isBadRequest());
        }

}
