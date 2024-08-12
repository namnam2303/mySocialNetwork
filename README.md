# MySocialNetwork

MySocialNetwork is a social networking application built with Spring Boot, providing basic features such as posting, friending, commenting, and interactions.

## Key Features

- User registration and login
- Create and manage posts
- Friend requests and friend list management
- Comments and reactions on posts
- User profile management
- View timeline

## Project Structure

The project is organized following the MVC pattern with main packages:

- `controller`: Handles HTTP requests
- `service`: Contains business logic
- `repository`: Interacts with the database
- `entity`: Defines data objects
- `dto`: Data Transfer Objects
- `security`: Security configuration and authentication

## API Endpoints

- `/api/auth`: Authentication and registration
- `/api/user`: User management
- `/api/post`: Post management
- `/api/comment`: Comment management
- `/api/friends`: Friend management
- `/api/reaction`: Reaction management
- `/api/timeline`: View timeline
- `/api/reports`: Generate reports (admin only)

## Installation and Running

1. Clone the repository
2. Configure the database in `application.properties`
3. Run the command: `mvn spring-boot:run`

## Unit Testing

The project uses JUnit and Mockito for unit testing. Test cases are organized in the `test` package and include:

- Tests for services
- Tests for controllers
- Mocking dependencies to isolate unit tests

To run tests:

```
mvn test
```

## Technologies Used

- Spring Boot
- Spring Security
- Spring Data JPA
- PostgreSQL
- JUnit
- Mockito
- Maven

## Contributing

Contributions are welcome. Please open an issue or submit a pull request to contribute.
