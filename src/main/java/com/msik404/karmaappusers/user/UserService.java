package com.msik404.karmaappusers.user;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.dto.UserDto;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserDocumentNotFoundException;
import com.msik404.karmaappusers.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void save(@NonNull UserDocument userDocument)
            throws DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        try {
            repository.save(userDocument);
        } catch (DuplicateKeyException ex) {
            DuplicateKeyExceptionHandler.handle(ex);
        }
    }

    public void update(@NonNull UserDto userDto)
            throws UserDocumentNotFoundException, DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        try {
            final UpdateResult result = repository.updateUser(userDto);
            if (result.getMatchedCount() == 0) {
                throw new UserDocumentNotFoundException();
            }
        } catch (DuplicateKeyException ex) {
            DuplicateKeyExceptionHandler.handle(ex);
        }
    }
}