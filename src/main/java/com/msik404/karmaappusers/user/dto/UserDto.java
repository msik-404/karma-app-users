package com.msik404.karmaappusers.user.dto;

import com.msik404.karmaappusers.user.Role;
import org.bson.types.ObjectId;

public record UserDto(ObjectId userId, String firstName, String lastName, String username, String email, String password, Role role) {
}
