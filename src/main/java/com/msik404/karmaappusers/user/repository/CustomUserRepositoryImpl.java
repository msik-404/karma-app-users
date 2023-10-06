package com.msik404.karmaappusers.user.repository;

import java.util.*;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.dto.UserDto;
import com.msik404.karmaappusers.user.dto.IdAndUsernameOnlyDto;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.lang.NonNull;

@RequiredArgsConstructor
public class CustomUserRepositoryImpl implements CustomUserRepository {

    private final MongoOperations ops;

    @NonNull
    @Override
    public UpdateResult updateUser(@NonNull UserDto userDto) {

        assert userDto.userId() != null;

        final var update = new Update();
        if (userDto.firstName() != null) {
            update.set("firstName", userDto.firstName());
        }
        if (userDto.lastName() != null) {
            update.set("lastName", userDto.lastName());
        }
        if (userDto.username() != null) {
            update.set("username", userDto.username());
        }
        if (userDto.email() != null) {
            update.set("email", userDto.email());
        }
        if (userDto.password() != null) {
            update.set("password", userDto.password());
        }
        if (userDto.role() != null) {
            update.set("role", userDto.role());
        }

        final var query = new Query(Criteria.where("id").is(userDto.userId()));
        return ops.updateFirst(query, update, UserDocument.class);
    }

    @NonNull
    @Override
    public List<Optional<String>> findUsernames(@NonNull List<ObjectId> userIds) {

        assert !userIds.isEmpty();

        final Map<ObjectId, Integer> userIdToIdx = new HashMap<>(userIds.size());
        for (int i = 0; i < userIds.size(); i++) {
            userIdToIdx.put(userIds.get(i), i);
        }

        final var query = new Query(Criteria.where("_id").in(userIds));
        query.fields().include("_id", "username");

        final List<IdAndUsernameOnlyDto> queryResults =  ops.find(
                query,
                IdAndUsernameOnlyDto.class,
                ops.getCollectionName(UserDocument.class)
        );

        List<Optional<String>> results = new ArrayList<>(Collections.nCopies(userIds.size(), Optional.empty()));
        for (IdAndUsernameOnlyDto dto : queryResults) {
            results.set(userIdToIdx.get(dto.id()), Optional.of(dto.username()));
        }

        return results;
    }

}
