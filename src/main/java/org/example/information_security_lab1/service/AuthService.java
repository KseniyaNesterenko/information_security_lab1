package org.example.information_security_lab1.service;

import org.example.information_security_lab1.model.User;
import org.example.information_security_lab1.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
    }

    public void register(String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("User with this login already exists");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        userRepository.save(user);
    }

    public Optional<User> authenticate(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> bCryptPasswordEncoder.matches(password, user.getPassword()));
    }
}

