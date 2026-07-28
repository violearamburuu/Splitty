package com.violearamburuu.splitty;

import com.violearamburuu.splitty.model.User;
import com.violearamburuu.splitty.repository.UserRepository;
import com.violearamburuu.splitty.services.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;

    @InjectMocks private UserService userService;

    @Test
    void createUser_newEmail_savesWithHashedPassword() {
        when(userRepository.findByEmail("ana@test.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("rawpass")).thenReturn("hashed-value");
        when(userRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        userService.createUser("Ana", "ana@test.com", "rawpass");

        // capture what got saved and check the password was hashed, not raw
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        User saved = captor.getValue();

        assertEquals("hashed-value", saved.getPasswordHash());
        assertNotEquals("rawpass", saved.getPasswordHash());
    }

    @Test
    void createUser_duplicateEmail_throwsAndSavesNothing() {
        when(userRepository.findByEmail("ana@test.com"))
                .thenReturn(Optional.of(new User("Ana", "ana@test.com", "hash")));

        assertThrows(RuntimeException.class, () ->
                userService.createUser("Ana", "ana@test.com", "rawpass"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void findUserByEmail_notFound_throws() {
        when(userRepository.findByEmail("missing@test.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                userService.findUserByEmail("missing@test.com"));
    }
}