package com.msik404.karmaappusers.user;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

@Data
@Document(collection = "users")
public class UserDocument {

    private ObjectId id;

    private String firstName;
    private String lastName;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;
    private String password;

    private Role role;

    public UserDocument(
            @Nullable final String firstName,
            @Nullable final String lastName,
            @NonNull final String username,
            @NonNull final String email,
            @NonNull final String password,
            @NonNull final Role role) {

        this.id = ObjectId.get();
        this.firstName = firstName;
        this.lastName = lastName;
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }

}
