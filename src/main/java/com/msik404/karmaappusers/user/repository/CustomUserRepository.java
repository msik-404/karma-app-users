package com.msik404.karmaappusers.user.repository;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.dto.UserDto;
import org.springframework.lang.NonNull;

public interface CustomUserRepository {

    UpdateResult updateUser(@NonNull UserDto userDto);

}
