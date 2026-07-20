package com.cs.rbac_api.service;

import com.cs.rbac_api.exception.UserNotFoundException;
import com.cs.rbac_api.model.Role;
import com.cs.rbac_api.model.User;
import com.cs.rbac_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void updateUserRole(Long userId, Role newRole) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found: " + userId));
        user.setRole(newRole);
        userRepository.save(user);
    }
}
