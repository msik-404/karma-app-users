package com.msik404.karmaappusers.user.repository;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.dto.UserDto;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoOperations ops;

    @Override
    public UpdateResult updateUser(@NonNull UserDto userDto) {

        final var update = new Update();
        if (userDto.getFirstName() != null) {
            update.set("firstName", userDto.getFirstName());
        }
        if (userDto.getLastName() != null) {
            update.set("lastName", userDto.getLastName());
        }
        if (userDto.getUsername() != null) {
            update.set("username", userDto.getUsername());
        }
        if (userDto.getEmail() != null) {
            update.set("email", userDto.getEmail());
        }
        if (userDto.getPassword() != null) {
            update.set("password", userDto.getPassword());
        }
        if (userDto.getRole() != null) {
            update.set("role", userDto.getRole());
        }

        final var query = new Query(Criteria.where("id").is(userDto.getUserId()));
        return ops.updateFirst(query, update, UserDocument.class);
    }

}
