# Quick Start Guide

This guide will help you quickly set up and run the Spring Boot OAuth 2.0 Authentication application.

## Prerequisites

- Java 17 or higher
- Maven 3.6 or higher
- MySQL 5.7 or higher
- (Optional) OAuth 2.0 credentials from Google, Facebook, and/or GitHub

## Step 1: Database Setup

1. Install MySQL and start the MySQL server
2. Create the database:
```bash
mysql -u root -p
CREATE DATABASE spring_auth;
exit;
```

3. Update database credentials in `src/main/resources/application.properties`:
```properties
spring.datasource.username=YOUR_MYSQL_USERNAME
spring.datasource.password=YOUR_MYSQL_PASSWORD
```

## Step 2: OAuth 2.0 Setup (Optional)

If you want to use OAuth 2.0 providers, you need to obtain client credentials:

### Google OAuth 2.0
1. Visit [Google Cloud Console](https://console.cloud.google.com/)
2. Create a new project
3. Enable the Google+ API
4. Create OAuth 2.0 credentials
5. Add redirect URI: `http://localhost:8080/login/oauth2/code/google`
6. Copy Client ID and Client Secret

### Facebook OAuth 2.0
1. Visit [Facebook Developers](https://developers.facebook.com/)
2. Create a new app
3. Add Facebook Login product
4. Add redirect URI: `http://localhost:8080/login/oauth2/code/facebook`
5. Copy App ID and App Secret

### GitHub OAuth 2.0
1. Visit [GitHub Developer Settings](https://github.com/settings/developers)
2. Create a new OAuth App
3. Add callback URL: `http://localhost:8080/login/oauth2/code/github`
4. Copy Client ID and Client Secret

### Update Configuration
Edit `src/main/resources/application.properties` and replace the placeholders:
```properties
# Google
spring.security.oauth2.client.registration.google.client-id=YOUR_GOOGLE_CLIENT_ID
spring.security.oauth2.client.registration.google.client-secret=YOUR_GOOGLE_CLIENT_SECRET

# Facebook
spring.security.oauth2.client.registration.facebook.client-id=YOUR_FACEBOOK_CLIENT_ID
spring.security.oauth2.client.registration.facebook.client-secret=YOUR_FACEBOOK_CLIENT_SECRET

# GitHub
spring.security.oauth2.client.registration.github.client-id=YOUR_GITHUB_CLIENT_ID
spring.security.oauth2.client.registration.github.client-secret=YOUR_GITHUB_CLIENT_SECRET
```

**Note:** If you skip OAuth setup, you can still use local authentication with username/password.

## Step 3: Build and Run

1. Navigate to the project directory:
```bash
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

Or run the JAR file directly:
```bash
java -jar target/spring-authenticate-1.0.0.jar
```

## Step 4: Access the Application

Open your browser and navigate to:
```
http://localhost:8080
```

## Default Test User

A default user is automatically created:
- **Username:** testuser
- **Password:** password123

You can log in with these credentials to test local authentication.

## Application Features

### Local Authentication
1. Click "Register" on the home page
2. Fill in username, email, and password
3. Submit the form
4. Log in with your new credentials

### OAuth 2.0 Authentication
1. Click "Login" on the home page
2. Choose one of the OAuth providers (Google, Facebook, or GitHub)
3. Authorize the application
4. You'll be redirected to the home page

## Troubleshooting

### MySQL Connection Error
- Verify MySQL is running: `systemctl status mysql` (Linux) or check Services (Windows)
- Check database credentials in `application.properties`
- Ensure the database `spring_auth` exists

### OAuth 2.0 Error
- Verify client credentials are correct
- Ensure redirect URIs match exactly in provider settings
- Check that your application is accessible at `http://localhost:8080`

### Build Errors
- Ensure Java 17 or higher is installed: `java -version`
- Ensure Maven is installed: `mvn -version`
- Clear Maven cache: `mvn clean`

## Next Steps

- Customize the templates in `src/main/resources/templates/`
- Add more user fields to the `User` entity
- Implement role-based access control
- Add password reset functionality
- Customize OAuth 2.0 user mapping

## Support

For issues and questions, please check the [README.md](README.md) file or open an issue on GitHub.
