package com.violearamburuu.splitty.services;

import com.violearamburuu.splitty.model.*;
import com.violearamburuu.splitty.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder){
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User createUser(String name, String email, String password){
        if (userRepository.findByEmail(email).isPresent()){
            throw new RuntimeException("Email already in use: " + email);
        }
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(name, email, hashedPassword);
        userRepository.save(user);
        return user;
    }

    public void deleteUser(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent()){
            userRepository.delete(user);
        }
    }

    public User findUser(String email){
        return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
