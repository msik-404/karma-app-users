package com.msik404.karmaappusers;

import java.util.ArrayList;
import java.util.List;

import com.msik404.karmaappusers.user.Role;
import com.msik404.karmaappusers.user.UserDocument;
import org.springframework.lang.NonNull;

public class TestingDataGenerator {

    public static final List<UserDocument> TEST_USER_DOCS = getTestUserDocs();

    public static String getUsername(int id) {
        return String.format("username_%d", id);
    }

    public static String getEmail(int id) {
        return String.format("%s@mail.com", getUsername(id));
    }

    public static UserDocument getTestUserDoc(int id, @NonNull Role role) {

        final String username = getUsername(id);
        final String email = getEmail(id);

        return new UserDocument(null, null, username, email, username, role);
    }

    private static List<UserDocument> getTestUserDocs() {

        final int userAmount = 6;

        List<UserDocument> users = new ArrayList<>(userAmount);

        users.add(getTestUserDoc(0, Role.USER));
        users.add(getTestUserDoc(1, Role.USER));
        users.add(getTestUserDoc(2, Role.USER));
        users.add(getTestUserDoc(3, Role.USER));

        users.add(getTestUserDoc(4, Role.MOD));

        users.add(getTestUserDoc(5, Role.ADMIN));

        return users;
    }

    public static UserDocument copy(int idx) {

        assert idx < TEST_USER_DOCS.size();

        final UserDocument doc = TEST_USER_DOCS.get(idx);

        final UserDocument copiedDoc = new UserDocument(
                doc.getFirstName(),
                doc.getLastName(),
                doc.getUsername(),
                doc.getEmail(),
                doc.getPassword(),
                doc.getRole()
        );
        copiedDoc.setId(doc.getId());

        return copiedDoc;
    }

}
