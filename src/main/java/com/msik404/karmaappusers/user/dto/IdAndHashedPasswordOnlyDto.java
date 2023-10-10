package com.msik404.karmaappusers.user.dto;

import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public record IdAndHashedPasswordOnlyDto(@NonNull ObjectId id, @NonNull String password) {
}
