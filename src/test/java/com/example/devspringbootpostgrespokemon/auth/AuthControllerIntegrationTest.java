package com.example.devspringbootpostgrespokemon.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.emptyOrNullString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void loginWithValidCredentialsReturnsBearerToken() throws Exception {
        login("{\"username\":\"admin\",\"password\":\"admin-test-password\"}")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken", not(emptyOrNullString())))
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").value(900));
    }

    @Test
    void loginWithWrongPasswordReturnsUnauthorized() throws Exception {
        login("{\"username\":\"admin\",\"password\":\"wrong-password\"}")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void loginWithUnknownUserReturnsSameUnauthorizedMessage() throws Exception {
        login("{\"username\":\"misty\",\"password\":\"whatever\"}")
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value("Invalid username or password"));
    }

    @Test
    void loginWithBlankFieldsReturnsBadRequest() throws Exception {
        login("{\"username\":\"\",\"password\":\"\"}")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    @Test
    void loginWithMalformedBodyReturnsBadRequest() throws Exception {
        login("not-json")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Malformed request body"));
    }

    private ResultActions login(String body) throws Exception {
        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body));
    }
}
