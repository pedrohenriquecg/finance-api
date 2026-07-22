package com.pedro.financeapi;

import com.pedro.financeapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void shouldReturnAuthenticatedUserProfile() throws Exception {
        User user = createUser();

        mockMvc.perform(get("/users/me")
                        .header("Authorization", bearerToken(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Pedro"))
                .andExpect(jsonPath("$.email").value("pedro@email.com"));
    }

    @Test
    void shouldUpdateAuthenticatedUserProfile() throws Exception {
        User user = createUser();

        mockMvc.perform(put("/users/me")
                        .header("Authorization", bearerToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Pedro Atualizado",
                                  "email": "novo@email.com"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId()))
                .andExpect(jsonPath("$.name").value("Pedro Atualizado"))
                .andExpect(jsonPath("$.email").value("novo@email.com"));
    }

    @Test
    void shouldRejectDuplicateEmailWhenUpdatingProfile() throws Exception {
        User user = createUser();
        createUser("Ana", "ana@email.com");

        mockMvc.perform(put("/users/me")
                        .header("Authorization", bearerToken(user))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Pedro",
                                  "email": "ana@email.com"
                                }
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Email is already in use"));
    }

    @Test
    void shouldDeleteAuthenticatedUserWithoutTransactions() throws Exception {
        User user = createUser();

        mockMvc.perform(delete("/users/me")
                        .header("Authorization", bearerToken(user)))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldRejectDeletingUserWithTransactions() throws Exception {
        User user = createUser();
        createTransaction(user.getId());

        mockMvc.perform(delete("/users/me")
                        .header("Authorization", bearerToken(user)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("User has transactions and cannot be deleted"));
    }

    @Test
    void shouldRejectUnauthenticatedProfileRequest() throws Exception {
        mockMvc.perform(get("/users/me"))
                .andExpect(status().isUnauthorized());
    }
}
