package com.msik404.karmaappusers.user;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import org.springframework.dao.DuplicateKeyException;

public class DuplicateKeyExceptionHandler {

    public static void handle(DuplicateKeyException ex)
            throws DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        final String errorMessage = ex.getMostSpecificCause().getMessage();

        final Pattern pattern = Pattern.compile("index: (.*) dup");
        final Matcher matcher = pattern.matcher(errorMessage);

        if (matcher.find()) {
            final String field = matcher.group(1);
            if (field.equals("username")) {
                throw new DuplicateUsernameException();
            } else if (field.equals("email")) {
                throw new DuplicateEmailException();
            }
        }
        throw new DuplicateUnexpectedFieldException();
    }

}
