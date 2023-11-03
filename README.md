# karma-app-users
Users microservice for karma-app microservices version. karma-app-users is grpc server with access to mongodb database.

Check out other karma-app microservices:
- [karma-app-gateway](https://github.com/msik-404/karma-app-gateway)
- [karma-app-posts](https://github.com/msik-404/karma-app-posts)

# Technologies used
- Java 21
- MongoDB
- Docker
- gRPC
- Java spring
- [spring-boot-starter-data-mongodb](https://docs.spring.io/spring-data/mongodb/docs/current/reference/html/)
- spring-boot-starter-test
- [spring-boot-testcontainers](https://spring.io/blog/2023/06/23/improved-testcontainers-support-in-spring-boot-3-1)
- junit-jupiter
- [grpc-java](https://github.com/grpc/grpc-java)
- [protovalidate-java](https://github.com/bufbuild/protovalidate-java)
- lombok

# gRPC, Protobuf and protovalidate
[gRPC](https://grpc.io/) is a modern open source high performance Remote Procedure Call (RPC) framework that can run in 
any environment. gRPC simplifies microservices API implementation and later the usage of the API. gRPC is self-documenting,
all available service methods and message structures can be found inside [proto file](https://github.com/msik-404/karma-app-users/blob/main/src/main/proto/karma_app_users.proto).

In this project to help with message validation I use: [protovalidate-java](https://github.com/bufbuild/protovalidate-java).
This project significantly simplifies validation of messages and reduces the time required to build stable system.
Additionally potential user of this microservice can see which fields are required and what
constraints need to be met to build valid message.

## Using postman
To test this microservice in postman one must not only import .proto file in postman service definition but also import
path to .proto files of protovalidate-java. Usually this path looks something like this:
some_personal_path/karma-app-users/target/protoc-dependencies/some-long-code. Under some-long-code there should be the
following files buf/validate/priv/expression.proto and buf/validate/priv/validate.proto.

# Features

## Service methods
These are all the supported methods.

```
service Users {

  rpc createUser(CreateUserRequest) returns (google.protobuf.Empty) {}
  rpc updateUser(UpdateUserRequest) returns (google.protobuf.Empty) {}

  rpc findCredentials(CredentialsRequest) returns (CredentialsResponse) {}
  rpc findUserRole(UserRoleRequest) returns (UserRoleResponse) {}
  rpc findUsername(UsernameRequest) returns (UsernameResponse) {}
  rpc findUsernames(UsernamesRequest) returns (UsernamesResponse) {}
  rpc findUserId(UserIdRequest) returns (MongoObjectId) {}

}

```
Running from top to bottom:

### rpc createUser(CreateUserRequest) returns (google.protobuf.Empty) {}
- Create user. Password should be already hashed and salted for example with BCrypt.

### rpc updateUser(UpdateUserRequest) returns (google.protobuf.Empty) {}
- Update user. Only userId is required, all the other fields are optional. Only provided optional fields will update 
requested user.

### rpc findCredentials(CredentialsRequest) returns (CredentialsResponse) {}
- Find userId, hashed and salted password and role by user email.
### rpc findUserRole(UserRoleRequest) returns (UserRoleResponse) {}
- Find user role by userId.
### rpc findUsername(UsernameRequest) returns (UsernameResponse) {}
- Find user username by userId
### rpc findUsernames(UsernamesRequest) returns (UsernamesResponse) {}
- Find N usernames by N userIds. N usernames are returned in the same order as requested N userIds. If usernames was
not found empty string is returned.
### rpc findUserId(UserIdRequest) returns (MongoObjectId) {}
- Find userId by username.

To see message structure look inside [proto file](https://github.com/msik-404/karma-app-users/blob/main/src/main/proto/karma_app_users.proto).

## Exception encoding
When some exception which is not critical is thrown on the backend side, it is being encoded and passed with appropriate
gRPC code to the caller. Each exception has its unique identifier. With this it can be decoded on the caller side.
In this setup client side can use the same exception classes as backend.

Simple [encoding class](https://github.com/msik-404/karma-app-users/blob/main/src/main/java/com/msik404/karmaappusers/encoding/ExceptionEncoder.java)
which simply inserts "exceptionId EXCEPTION_ID" at the begging of error message. This EXCEPTION_ID can be parsed with
simple regex.

Each encodable exception must implement [EncodableException](https://github.com/msik-404/karma-app-users/blob/main/src/main/java/com/msik404/karmaappusers/encoding/EncodableException.java)
and [GrpcStatusException](https://github.com/msik-404/karma-app-users/blob/main/src/main/java/com/msik404/karmaappusers/grpc/impl/exception/GrpcStatusException.java).

# Environment variables

Backend requires four environment variables to be set:
- KARMA_APP_USERS_DB_HOST
- KARMA_APP_USERS_DB_USER
- KARMA_APP_USERS_DB_PASSWORD
- KARMA_APP_USERS_DB_NAME

for details see: [application.yaml](https://github.com/msik-404/karma-app-users/blob/main/src/main/resources/application.yaml).

Additionally, [docker-compose.yaml](https://github.com/msik-404/karma-app-users/blob/main/docker-compose.yaml) uses:
- KARMA_APP_USERS_HOST

Simply create .env and place it in the root of project.

For example:
```
KARMA_APP_USERS_DB_HOST=users-db
KARMA_APP_USERS_DB_USER=dev
KARMA_APP_USERS_DB_PASSWORD=dev
KARMA_APP_USERS_DB_NAME=users-db
KARMA_APP_USERS_HOST=karma-app-users
```

# Building the project
To get target folder and build the project with maven simply run:
```
./mvnw clean package -DskipTests
```

If one would like to build the project with running the tests, one must have docker installed on their machine and run:
```
./mvnw clean package
```

# Tests
Docker is required to run tests locally because I use [Testcontainers for Java](https://java.testcontainers.org/).

All the code which comes into contact with data persistence is tested in integration tests under
[src/test](https://github.com/msik-404/karma-app-users/tree/main/src/test).
The rest of the code is much simpler and easier to follow and was tested manually using postman.

# Starting the microservice | deployment for testing
In this repository one can find [docker-compose-yaml](https://github.com/msik-404/karma-app-users/blob/main/docker-compose.yaml).

To start the microservice one should use provided bash scripts but pure docker can also be used.

## Bash scripts
Bash scripts can be found under [scripts](https://github.com/msik-404/karma-app-users/tree/main/scripts) folder.

Starting microservice: [start.sh](https://github.com/msik-404/karma-app-users/blob/main/scripts/start.sh)

Stopping microservice: [stop.sh](https://github.com/msik-404/karma-app-users/blob/main/scripts/stop.sh)

Cleaning after microservice: [clean.sh](https://github.com/msik-404/karma-app-users/blob/main/scripts/clean.sh)

To run the scripts make them executable for example:
```
sudo chmod 744 *.sh
```
and then use:
```
./start.sh
```
```
./stop.sh
```
```
./clean.sh
```

## Pure docker method
```
docker compose up
```
