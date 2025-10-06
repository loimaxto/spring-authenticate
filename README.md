# Spring Authenticate

A comprehensive Spring Boot application with OAuth 2.0 authentication and local user management.

## Features

- **Spring Boot 3.2.0** (latest stable version)
- **Thymeleaf** template engine for server-side rendering
- **MySQL** database integration
- **OAuth 2.0** authentication with:
  - Google
  - Facebook
  - GitHub
- **Local authentication** with username/password
- **BCrypt** password encryption
- **JPA/Hibernate** for database operations
- Complete database schema with user management

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 5.7 or higher

## Database Setup

1. Install and start MySQL server
2. Create a database:
```sql
CREATE DATABASE spring_auth;
```

3. The application will automatically create tables using the `schema.sql` file on startup.

Default test user credentials:
- Username: `testuser`
- Password: `password123`

## OAuth 2.0 Configuration

To enable OAuth 2.0 authentication, you need to register your application with each provider and update the configuration in `src/main/resources/application.properties`:

### Google OAuth 2.0
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project or select existing one
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Update `application.properties`:
```properties
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET
```

### Facebook OAuth 2.0
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app
3. Add Facebook Login product
4. Add OAuth redirect URI: `http://localhost:8080/login/oauth2/code/facebook`
5. Update `application.properties`:
```properties
spring.security.oauth2.client.registration.facebook.client-id=YOUR_FACEBOOK_CLIENT_ID
spring.security.oauth2.client.registration.facebook.client-secret=YOUR_FACEBOOK_CLIENT_SECRET
```

### GitHub OAuth 2.0
1. Go to [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App
3. Add Authorization callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Update `application.properties`:
```properties
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
```

## Database Configuration

Update MySQL connection settings in `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/spring_auth?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=root
```

## Build and Run

1. Clone the repository:
```bash
git clone https://github.com/loimaxto/spring-authenticate.git
cd spring-authenticate
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

4. Access the application at `http://localhost:8080`

## Project Structure

```
spring-authenticate/
├── src/
│   ├── main/
│   │   ├── java/com/loimaxto/springauth/
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java          # Security configuration
│   │   │   ├── controller/
│   │   │   │   ├── AuthController.java          # Authentication endpoints
│   │   │   │   └── HomeController.java          # Home page controller
│   │   │   ├── model/
│   │   │   │   └── User.java                    # User entity
│   │   │   ├── repository/
│   │   │   │   └── UserRepository.java          # User data access
│   │   │   ├── service/
│   │   │   │   ├── CustomOAuth2UserService.java # OAuth2 user handler
│   │   │   │   ├── CustomUserDetailsService.java# Local user authentication
│   │   │   │   └── UserService.java             # User management service
│   │   │   └── SpringAuthenticateApplication.java # Main application class
│   │   └── resources/
│   │       ├── templates/
│   │       │   ├── index.html                   # Landing page
│   │       │   ├── login.html                   # Login page
│   │       │   ├── register.html                # Registration page
│   │       │   └── home.html                    # Dashboard page
│   │       ├── application.properties           # Application configuration
│   │       └── schema.sql                       # Database schema
│   └── test/
│       └── java/com/loimaxto/springauth/
└── pom.xml                                      # Maven configuration
```

## Database Schema

The application uses two main tables:

### users
Stores local and OAuth user information:
- `id`: Primary key
- `username`: Unique username
- `password`: BCrypt encrypted password
- `email`: Unique email address
- `enabled`: Account status
- `auth_provider`: Authentication provider (LOCAL, GOOGLE, FACEBOOK, GITHUB)
- `created_at`, `updated_at`: Timestamps

### oauth_user_attributes
Stores OAuth 2.0 specific user attributes:
- `id`: Primary key
- `user_id`: Foreign key to users table
- `provider`: OAuth provider name
- `provider_user_id`: User ID from OAuth provider
- `name`, `email`, `picture_url`: User information
- `attributes`: JSON field for additional attributes
- `created_at`, `updated_at`: Timestamps

## Endpoints

- `/` - Landing page
- `/login` - Login page (local and OAuth)
- `/register` - User registration page
- `/home` - Dashboard (authenticated users only)
- `/logout` - Logout endpoint

## Technologies Used

- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Spring OAuth2 Client
- Thymeleaf
- MySQL
- Maven
- BCrypt for password encryption

## License

This project is open source and available under the MIT License.