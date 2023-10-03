package com.msik404.karmaappusers.user.repository;

import com.msik404.karmaappusers.user.UserDocument;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<UserDocument, ObjectId>, CustomUserRepository {
}
