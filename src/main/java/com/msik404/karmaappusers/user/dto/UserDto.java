package com.msik404.karmaappusers.user.dto;

import com.msik404.karmaappusers.user.Role;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

public record UserDto(@NonNull ObjectId userId, @Nullable String firstName, @Nullable String lastName,
                      @NonNull String username, @NonNull String email, @NonNull String password, @NonNull Role role) {
}
