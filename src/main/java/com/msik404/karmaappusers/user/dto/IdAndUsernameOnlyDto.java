package com.msik404.karmaappusers.user.dto;

import org.bson.types.ObjectId;

public record IdAndUsernameOnlyDto(ObjectId id, String username) {
}
