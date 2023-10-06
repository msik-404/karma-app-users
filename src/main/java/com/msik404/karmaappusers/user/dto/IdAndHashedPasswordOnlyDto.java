package com.msik404.karmaappusers.user.dto;

import org.bson.types.ObjectId;

public record IdAndHashedPasswordOnlyDto(ObjectId id, String password) {
}
