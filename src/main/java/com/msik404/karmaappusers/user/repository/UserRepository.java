package com.msik404.karmaappusers.user.repository;

import java.util.Optional;

import com.msik404.karmaappusers.user.UserDocument;
import com.msik404.karmaappusers.user.dto.PasswordOnlyDto;
import com.msik404.karmaappusers.user.dto.RoleOnlyDto;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.lang.NonNull;

public interface UserRepository extends MongoRepository<UserDocument, ObjectId>, CustomUserRepository {

    Optional<PasswordOnlyDto> findByEmail(@NonNull String email);

    @Query("{ '_id' :  ?0 }")
    Optional<RoleOnlyDto> findByUserId(@NonNull ObjectId userId);

}
