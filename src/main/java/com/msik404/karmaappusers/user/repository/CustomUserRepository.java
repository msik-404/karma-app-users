package com.msik404.karmaappusers.user.repository;

import java.util.List;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.dto.UserDto;
import org.bson.types.ObjectId;
import org.springframework.lang.NonNull;

public interface CustomUserRepository {

    UpdateResult updateUser(@NonNull UserDto userDto);

    List<String> findUsernames(@NonNull List<ObjectId> userIds);

}
