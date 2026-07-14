package com.pedro.financeapi;

import com.pedro.financeapi.model.Transaction;
import com.pedro.financeapi.model.TransactionType;
import com.pedro.financeapi.model.User;
import com.pedro.financeapi.repository.TransactionRepository;
import com.pedro.financeapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

@SpringBootTest
@AutoConfigureMockMvc
abstract class IntegrationTestSupport {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected TransactionRepository transactionRepository;

    @BeforeEach
    void cleanDatabase() {
        transactionRepository.deleteAll();
        userRepository.deleteAll();
    }

    protected User createUser() {
        return createUser("Pedro", "pedro@email.com");
    }

    protected User createUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        return userRepository.save(user);
    }

    protected Long createTransaction(Long userId) {
        return createTransaction(
                userId,
                "Salary",
                BigDecimal.valueOf(5000.00),
                TransactionType.INCOME,
                "Salary"
        );
    }

    protected Long createTransaction(
            Long userId,
            String description,
            BigDecimal amount,
            TransactionType type,
            String category
    ) {
        User user = userRepository.findById(userId).orElseThrow();
        Transaction transaction = new Transaction();

        transaction.setDescription(description);
        transaction.setAmount(amount);
        transaction.setType(type);
        transaction.setCategory(category);
        transaction.setDate(LocalDate.of(2026, 6, 22));
        transaction.setUser(user);

        return transactionRepository.save(transaction).getId();
    }
}
