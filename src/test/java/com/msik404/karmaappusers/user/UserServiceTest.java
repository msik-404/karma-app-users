package com.msik404.karmaappusers.user;

import java.util.Optional;

import com.msik404.karmaappusers.MongoConfiguration;
import com.msik404.karmaappusers.TestingDataGenerator;
import com.msik404.karmaappusers.user.dto.UserDto;
import com.msik404.karmaappusers.user.exception.DuplicateEmailException;
import com.msik404.karmaappusers.user.exception.DuplicateUsernameException;
import com.msik404.karmaappusers.user.exception.UserDocumentNotFoundException;
import com.msik404.karmaappusers.user.repository.CustomUserRepository;
import com.msik404.karmaappusers.user.repository.CustomUserRepositoryImpl;
import com.msik404.karmaappusers.user.repository.UserRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest(classes = {
        UserService.class,
        MongoConfiguration.class,
        UserRepository.class,
        CustomUserRepository.class,
        CustomUserRepositoryImpl.class
})
@EnableMongoRepositories
@Testcontainers
class UserServiceTest {

    @Container
    public static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:latest")
            .withExposedPorts(27017);

    @DynamicPropertySource
    private static void registerRedisProperties(DynamicPropertyRegistry registry) {

        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.data.mongodb.database", () -> "test");
    }

    private final UserRepository repository;
    private final UserService service;

    @Autowired
    UserServiceTest(UserRepository repository, UserService service) {

        this.repository = repository;
        this.service = service;
    }

    @BeforeEach
    void setUp() {
        repository.saveAll(TestingDataGenerator.TEST_USER_DOCS);
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll();
    }

    @Test
    void save_UniqueUsernameAndEmail_NoExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument doc = TestingDataGenerator.getTestUserDoc(uniqueId, Role.USER);

        // then                  when
        assertDoesNotThrow(() -> service.save(doc));

        // then
        final Optional<UserDocument> optionalDoc = repository.findById(doc.getId());
        assertTrue(optionalDoc.isPresent());
        assertEquals(doc, optionalDoc.get());
    }

    @Test
    void save_NotUniqueUsername_DuplicateUsernameExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument doc = TestingDataGenerator.getTestUserDoc(uniqueId, Role.USER);
        doc.setUsername(TestingDataGenerator.TEST_USER_DOCS.get(0).getUsername());

        // then                                              when
        assertThrows(DuplicateUsernameException.class, () -> service.save(doc));
    }

    @Test
    void save_NotUniqueEmail_DuplicateEmailExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument doc = TestingDataGenerator.getTestUserDoc(uniqueId, Role.USER);
        doc.setEmail(TestingDataGenerator.TEST_USER_DOCS.get(0).getEmail());

        // then                                           when
        assertThrows(DuplicateEmailException.class, () -> service.save(doc));
    }

    @Test
    void save_NotUniqueUsernameAndEmail_DuplicateUsernameExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument doc = TestingDataGenerator.getTestUserDoc(uniqueId, Role.USER);
        doc.setUsername(TestingDataGenerator.TEST_USER_DOCS.get(0).getUsername());
        doc.setEmail(TestingDataGenerator.TEST_USER_DOCS.get(0).getEmail());

        // then                                              when
        assertThrows(DuplicateUsernameException.class, () -> service.save(doc));
    }

    @Test
    void update_DocumentExistsAndUniqueUsernameAndEmail_NoExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument groundTruthDoc = TestingDataGenerator.copy(0);
        final String newUsername = TestingDataGenerator.getUsername(uniqueId);
        final String newEmail = TestingDataGenerator.getEmail(uniqueId);
        groundTruthDoc.setUsername(newUsername);
        groundTruthDoc.setEmail(newEmail);

        final UserDto updateDto = new UserDto(
                groundTruthDoc.getId(),
                null,
                null,
                newUsername,
                newEmail,
                null,
                null
        );

        // then                  when
        assertDoesNotThrow(() -> service.update(updateDto));

        // then
        final Optional<UserDocument> optionalDoc = repository.findById(groundTruthDoc.getId());
        assertTrue(optionalDoc.isPresent());
        assertEquals(groundTruthDoc, optionalDoc.get());
    }

    @Test
    void update_DocumentDoesNotExist_UserDocumentNotFoundExceptionThrown() {

        // given
        final UserDto updateDto = new UserDto(
                ObjectId.get(),
                null,
                null,
                "some",
                "value",
                null,
                null
        );

        // then                                                 when
        assertThrows(UserDocumentNotFoundException.class, () -> service.update(updateDto));
    }

    @Test
    void update_DocumentExistsAndNotUniqueUsername_DuplicateUsernameExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument groundTruthDoc = TestingDataGenerator.copy(0);
        final String newEmail = TestingDataGenerator.getEmail(uniqueId);
        groundTruthDoc.setEmail(newEmail);

        final UserDto updateDto = new UserDto(
                groundTruthDoc.getId(),
                null,
                null,
                TestingDataGenerator.TEST_USER_DOCS.get(2).getUsername(),
                newEmail,
                null,
                null
        );

        // then                                              when
        assertThrows(DuplicateUsernameException.class, () -> service.update(updateDto));
    }

    @Test
    void update_DocumentExistsAndNotUniqueEmail_DuplicateEmailExceptionThrown() {

        // given
        final int uniqueId = 777;
        final UserDocument groundTruthDoc = TestingDataGenerator.TEST_USER_DOCS.get(0);
        final String newUsername = TestingDataGenerator.getUsername(uniqueId);
        groundTruthDoc.setUsername(newUsername);

        final UserDto updateDto = new UserDto(
                groundTruthDoc.getId(),
                null,
                null,
                newUsername,
                TestingDataGenerator.TEST_USER_DOCS.get(2).getEmail(),
                null,
                null
        );

        // then                                           when
        assertThrows(DuplicateEmailException.class, () -> service.update(updateDto));
    }

    @Test
    void update_DocumentExistsAndNotUniqueUsernameAndEmail_DuplicateUsernameExceptionThrown() {

        // given
        final UserDto updateDto = new UserDto(
                TestingDataGenerator.TEST_USER_DOCS.get(0).getId(),
                null,
                null,
                TestingDataGenerator.TEST_USER_DOCS.get(1).getUsername(),
                TestingDataGenerator.TEST_USER_DOCS.get(2).getEmail(),
                null,
                null
        );

        // then                                              when
        assertThrows(DuplicateUsernameException.class, () -> service.update(updateDto));
    }

}