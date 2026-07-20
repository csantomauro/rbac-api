package com.cs.rbac_api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import com.cs.rbac_api.model.Role;

@Getter
@Setter
public class UpdateRoleRequestDto {
    @NotNull(message = "Role is required")
    private Role role;
}
