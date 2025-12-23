package com.example.tremor_test_be.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User create(UserCreateRequest request) {
        Optional<User> existing = userRepository.findByEmail(request.getEmail());
        if (existing.isPresent()) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "User with this email already exists");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .loginProvider(request.getLoginProvider())
                .build();
        return userRepository.save(user);
    }
}


