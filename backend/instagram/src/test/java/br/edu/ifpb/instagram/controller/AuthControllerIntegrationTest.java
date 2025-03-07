package br.edu.ifpb.instagram.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.edu.ifpb.instagram.model.request.LoginRequest;
import br.edu.ifpb.instagram.model.request.UserDetailsRequest;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void whenSignUpCorrectly_shouldReturnUserSignedUp() throws Exception {

        UserDetailsRequest newUser = new UserDetailsRequest(
                null,
                "testeSignUp@gmail.com",
                "12345678",
                "Teste da silva",
                "testin");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("testeSignUp@gmail.com"))
                .andExpect(jsonPath("$.username").value("testin"));

    }

    @Test
    void whenSignUpInvalidJson_shouldReturnBadRequest() throws Exception {

        String invalidJson = "{ \"email\": \"teste@gmail.com\", \"password\": }";

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(invalidJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void whenSignIn_shouldEnter() throws Exception {

        UserDetailsRequest newUser = new UserDetailsRequest(
                null,
                "testeSignIn@gmail.com",
                "87654321",
                "Teste Junior",
                "testezin");

        mockMvc.perform(post("/auth/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(newUser)));

        LoginRequest loginRequest = new LoginRequest("testezin", "87654321");

        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());

    }

    @Test
    void whenSignInWrong_shouldReturnInvalidPermission() throws Exception {

        LoginRequest loginRequest = new LoginRequest("teste", "11111111");

        mockMvc.perform(post("/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().is(403));

    }

}
