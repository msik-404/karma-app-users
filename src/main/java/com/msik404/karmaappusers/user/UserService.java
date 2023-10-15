package com.msik404.karmaappusers.user;

import java.util.List;
import java.util.Optional;

import com.mongodb.client.result.UpdateResult;
import com.msik404.karmaappusers.user.dto.*;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUnexpectedFieldException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserDocumentNotFoundException;
import com.msik404.karmaappusers.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;

    public void save(@NonNull final UserDocument userDocument)
            throws DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        try {
            repository.save(userDocument);
        } catch (DuplicateKeyException ex) {
            DuplicateKeyExceptionHandler.handle(ex);
        }
    }

    public void update(@NonNull final UserUpdateDto userUpdateDto)
            throws UserDocumentNotFoundException, DuplicateUsernameException, DuplicateEmailException, DuplicateUnexpectedFieldException {

        try {
            final UpdateResult result = repository.updateUser(userUpdateDto);
            if (result.getMatchedCount() == 0) {
                throw new UserDocumentNotFoundException();
            }
        } catch (DuplicateKeyException ex) {
            DuplicateKeyExceptionHandler.handle(ex);
        }
    }

    @NonNull
    public IdAndHashedPasswordAndRoleOnlyDto findCredentials(
            @NonNull final String email) throws UserDocumentNotFoundException {

        return repository.findByEmail(email).orElseThrow(UserDocumentNotFoundException::new);
    }

    @NonNull
    public Role findRole(@NonNull final ObjectId userId) throws UserDocumentNotFoundException {

        final Optional<RoleOnlyDto> optionalRoleDto = repository.findRoleByUserId(userId);
        if (optionalRoleDto.isEmpty()) {
            throw new UserDocumentNotFoundException();
        }
        return optionalRoleDto.get().role();
    }

    @NonNull
    public String findUsername(@NonNull final ObjectId userId) throws UserDocumentNotFoundException {

        final Optional<UsernameOnlyDto> optionalUsernameDto = repository.findUsernameByUserId(userId);
        if (optionalUsernameDto.isEmpty()) {
            throw new UserDocumentNotFoundException();
        }
        return optionalUsernameDto.get().username();
    }

    @NonNull
    public List<String> findUsernames(@NonNull final List<ObjectId> userIds) {

        final List<Optional<String>> optionalUsernames = repository.findUsernames(userIds);

        return optionalUsernames.stream().map(optional -> optional.orElse("")).toList();
    }

    @NonNull
    public ObjectId findUserId(@NonNull final String username) throws UserDocumentNotFoundException {

        final Optional<IdOnlyDto> optionalIdDto = repository.findUserIdByUsername(username);
        if (optionalIdDto.isEmpty()) {
            throw new UserDocumentNotFoundException();
        }
        return optionalIdDto.get().id();
    }

}
