package org.example.information_security_lab1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.example.information_security_lab1.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
}
