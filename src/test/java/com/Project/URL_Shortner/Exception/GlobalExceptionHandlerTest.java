package com.Project.URL_Shortner.Exception;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class GlobalExceptionHandlerTest {

    private MockMvc mockMvc;

    @RestController
    static class StubController {

        @PostMapping("/stub/duplicate-email")
        public void duplicateEmail() {
            throw new EmailAlreadyExistsException("Email already exists");
        }

        @PostMapping("/stub/bad-credentials")
        public void badCredentials() {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new StubController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void duplicateEmail_returns409Conflict() throws Exception {
        mockMvc.perform(post("/stub/duplicate-email")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Email already exists"))
                .andExpect(jsonPath("$.path").value("/stub/duplicate-email"));
    }

    @Test
    void badCredentials_returns401Unauthorized() throws Exception {
        mockMvc.perform(post("/stub/bad-credentials")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"))
                .andExpect(jsonPath("$.message").value("Invalid email or password"))
                .andExpect(jsonPath("$.path").value("/stub/bad-credentials"));
    }
}
