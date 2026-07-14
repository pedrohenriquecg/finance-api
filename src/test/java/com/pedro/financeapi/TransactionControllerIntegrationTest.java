package com.pedro.financeapi;

import com.pedro.financeapi.model.TransactionType;
import com.pedro.financeapi.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class TransactionControllerIntegrationTest extends IntegrationTestSupport {

    @Test
    void shouldCreateTransaction() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Salary",
                                  "amount": 5000.00,
                                  "type": "INCOME",
                                  "category": "Salary",
                                  "date": "2026-06-22",
                                  "userId": %d
                                }
                                """.formatted(user.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.description").value("Salary"))
                .andExpect(jsonPath("$.amount").value(5000.00))
                .andExpect(jsonPath("$.type").value("INCOME"))
                .andExpect(jsonPath("$.category").value("Salary"))
                .andExpect(jsonPath("$.date").value("2026-06-22"))
                .andExpect(jsonPath("$.user.id").value(user.getId()));
    }

    @Test
    void shouldListTransactionsByUser() throws Exception {
        User user = createUser();
        User otherUser = createUser("Ana", "ana@email.com");

        createTransaction(user.getId(), "Salary", BigDecimal.valueOf(5000.00), TransactionType.INCOME, "Salary");
        createTransaction(user.getId(), "Groceries", BigDecimal.valueOf(250.75), TransactionType.EXPENSE, "Food");
        createTransaction(otherUser.getId(), "Freelance", BigDecimal.valueOf(800.00), TransactionType.INCOME, "Work");

        mockMvc.perform(get("/transactions/user/" + user.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[*].description", containsInAnyOrder("Salary", "Groceries")))
                .andExpect(jsonPath("$[*].user.id", containsInAnyOrder(
                        user.getId().intValue(),
                        user.getId().intValue()
                )));
    }

    @Test
    void shouldReturnFinancialSummaryByUser() throws Exception {
        User user = createUser();
        User otherUser = createUser("Ana", "ana@email.com");

        createTransaction(user.getId(), "Salary", BigDecimal.valueOf(5000.00), TransactionType.INCOME, "Salary");
        createTransaction(user.getId(), "Bonus", BigDecimal.valueOf(750.00), TransactionType.INCOME, "Salary");
        createTransaction(user.getId(), "Rent", BigDecimal.valueOf(1200.50), TransactionType.EXPENSE, "Housing");
        createTransaction(otherUser.getId(), "Other salary", BigDecimal.valueOf(9000.00), TransactionType.INCOME, "Salary");

        mockMvc.perform(get("/transactions/user/" + user.getId() + "/summary"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.totalIncome").value(5750.0))
                .andExpect(jsonPath("$.totalExpense").value(1200.5))
                .andExpect(jsonPath("$.balance").value(4549.5))
                .andExpect(jsonPath("$.transactionCount").value(3));
    }

    @Test
    void shouldRejectInvalidTransaction() throws Exception {
        User user = createUser();

        mockMvc.perform(post("/transactions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "",
                                  "amount": 0,
                                  "type": "EXPENSE",
                                  "category": "",
                                  "date": "2026-06-22",
                                  "userId": %d
                                }
                                """.formatted(user.getId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.description").value("Description is required"))
                .andExpect(jsonPath("$.amount").value("Amount must be greater than zero"))
                .andExpect(jsonPath("$.category").value("Category is required"));
    }

    @Test
    void shouldReturnNotFoundWhenTransactionDoesNotExist() throws Exception {
        mockMvc.perform(get("/transactions/999"))
                .andExpect(status().isNotFound())
                .andExpect(content().string(containsString("Transaction not found with id: 999")));
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        User user = createUser();
        Long transactionId = createTransaction(user.getId());

        mockMvc.perform(put("/transactions/" + transactionId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "description": "Groceries",
                                  "amount": 250.75,
                                  "type": "EXPENSE",
                                  "category": "Food",
                                  "date": "2026-06-22",
                                  "userId": %d
                                }
                                """.formatted(user.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(transactionId))
                .andExpect(jsonPath("$.description").value("Groceries"))
                .andExpect(jsonPath("$.amount").value(250.75))
                .andExpect(jsonPath("$.type").value("EXPENSE"))
                .andExpect(jsonPath("$.category").value("Food"));
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        User user = createUser();
        Long transactionId = createTransaction(user.getId());

        mockMvc.perform(delete("/transactions/" + transactionId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/transactions/" + transactionId))
                .andExpect(status().isNotFound());
    }
}
