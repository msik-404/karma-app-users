package com.msik404.karmaappusers.grpc.impl.mapper;

import com.msik404.karmaappusers.grpc.UserRole;
import com.msik404.karmaappusers.grpc.impl.exception.UnsupportedRoleException;
import com.msik404.karmaappusers.user.Role;
import org.springframework.lang.NonNull;

public class RoleMapper {

    @NonNull
    public static Role map(@NonNull UserRole role) throws UnsupportedRoleException {

        switch (role) {
            case ROLE_USER -> {
                return Role.USER;
            }
            case ROLE_MOD -> {
                return Role.MOD;
            }
            case ROLE_ADMIN -> {
                return Role.ADMIN;
            }
            default -> throw new UnsupportedRoleException();
        }
    }

    @NonNull
    public static UserRole map(@NonNull Role role) {

        switch (role) {
            case USER -> {
                return UserRole.ROLE_USER;
            }
            case MOD -> {
                return UserRole.ROLE_MOD;
            }
            case ADMIN -> {
                return UserRole.ROLE_ADMIN;

            }
            default -> {
                return UserRole.UNRECOGNIZED;
            }
        }
    }

}
