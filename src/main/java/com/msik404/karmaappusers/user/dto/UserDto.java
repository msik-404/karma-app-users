package com.msik404.karmaappusers.user.dto;

import com.msik404.karmaappusers.user.Role;
import lombok.Getter;
import org.bson.types.ObjectId;

@Getter
public class UserDto {

    private ObjectId userId;

    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String password;
    private Role role;

}
