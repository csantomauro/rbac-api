package com.cs.rbac_api.controller;

import com.cs.rbac_api.dto.UpdateRoleRequestDto;
import com.cs.rbac_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> updateRole(
            @PathVariable Long id,
            @Valid @RequestBody UpdateRoleRequestDto request) {
        userService.updateUserRole(id, request.getRole());
        return ResponseEntity.noContent().build();
    }
}
