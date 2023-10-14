package com.msik404.karmaappusers.user.dto;

import com.msik404.karmaappusers.user.Role;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record IdAndHashedPasswordAndRoleOnlyDto(@NonNull ObjectId id, @NonNull String hashedPassword, @NonNull Role role) {
}
