package com.pedro.financeapi.repository;

import com.pedro.financeapi.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}