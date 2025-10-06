# Deployment Guide

This guide covers deploying the Spring Boot OAuth 2.0 Authentication application to production.

## Production Configuration

### 1. Security Considerations

#### Update Default Credentials
The application includes a default test user. **Remove or change this in production!**

Edit `src/main/resources/schema.sql` and either:
- Remove the INSERT statement for the test user
- Change the password to a secure one

#### Environment Variables
Never commit sensitive credentials to version control. Use environment variables:

```bash
export DB_URL="jdbc:mysql://production-db-server:3306/spring_auth"
export DB_USERNAME="prod_user"
export DB_PASSWORD="secure_password"
export GOOGLE_CLIENT_ID="your_production_google_client_id"
export GOOGLE_CLIENT_SECRET="your_production_google_client_secret"
export FACEBOOK_CLIENT_ID="your_production_facebook_client_id"
export FACEBOOK_CLIENT_SECRET="your_production_facebook_client_secret"
export GITHUB_CLIENT_ID="your_production_github_client_id"
export GITHUB_CLIENT_SECRET="your_production_github_client_secret"
```

Update `application.properties` to use environment variables:
```properties
spring.datasource.url=${DB_URL}
spring.datasource.username=${DB_USERNAME}
spring.datasource.password=${DB_PASSWORD}

spring.security.oauth2.client.registration.google.client-id=${GOOGLE_CLIENT_ID}
spring.security.oauth2.client.registration.google.client-secret=${GOOGLE_CLIENT_SECRET}
spring.security.oauth2.client.registration.facebook.client-id=${FACEBOOK_CLIENT_ID}
spring.security.oauth2.client.registration.facebook.client-secret=${FACEBOOK_CLIENT_SECRET}
spring.security.oauth2.client.registration.github.client-id=${GITHUB_CLIENT_ID}
spring.security.oauth2.client.registration.github.client-secret=${GITHUB_CLIENT_SECRET}
```

### 2. Database Configuration

#### Production Schema Management
Change the schema initialization mode:
```properties
# Don't automatically recreate schema in production
spring.sql.init.mode=never
spring.jpa.hibernate.ddl-auto=validate
```

Run the schema.sql manually on your production database:
```bash
mysql -u prod_user -p spring_auth < src/main/resources/schema.sql
```

#### Connection Pool
Configure connection pooling for production:
```properties
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.idle-timeout=600000
spring.datasource.hikari.max-lifetime=1800000
```

### 3. Logging Configuration

Create `application-prod.properties`:
```properties
# Production logging
logging.level.org.springframework.security=INFO
logging.level.org.springframework.web=INFO
logging.level.com.loimaxto.springauth=INFO

# Log to file
logging.file.name=/var/log/spring-auth/application.log
logging.file.max-size=10MB
logging.file.max-history=10
```

### 4. HTTPS Configuration

For production, always use HTTPS. Configure SSL/TLS:

```properties
server.port=8443
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=spring-auth
```

Generate a keystore:
```bash
keytool -genkeypair -alias spring-auth -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 3650
```

### 5. OAuth Redirect URIs

Update OAuth provider settings with production URLs:
- Google: `https://yourdomain.com/login/oauth2/code/google`
- Facebook: `https://yourdomain.com/login/oauth2/code/facebook`
- GitHub: `https://yourdomain.com/login/oauth2/code/github`

Update `application.properties`:
```properties
spring.security.oauth2.client.registration.google.redirect-uri=https://yourdomain.com/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.facebook.redirect-uri=https://yourdomain.com/login/oauth2/code/{registrationId}
spring.security.oauth2.client.registration.github.redirect-uri=https://yourdomain.com/login/oauth2/code/{registrationId}
```

## Deployment Options

### Option 1: JAR Deployment

1. Build the production JAR:
```bash
mvn clean package -DskipTests -Pprod
```

2. Run with production profile:
```bash
java -jar -Dspring.profiles.active=prod target/spring-authenticate-1.0.0.jar
```

3. Create a systemd service (Linux):
```ini
[Unit]
Description=Spring Authenticate Application
After=syslog.target network.target

[Service]
User=springauth
ExecStart=/usr/bin/java -jar -Dspring.profiles.active=prod /opt/spring-auth/spring-authenticate-1.0.0.jar
SuccessExitStatus=143
Environment="JAVA_OPTS=-Xmx512m -Xms256m"
EnvironmentFile=/etc/spring-auth/application.env

[Install]
WantedBy=multi-user.target
```

### Option 2: Docker Deployment

Create `Dockerfile`:
```dockerfile
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
COPY target/spring-authenticate-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

Build and run:
```bash
docker build -t spring-authenticate:latest .
docker run -d -p 8080:8080 \
  -e DB_URL="jdbc:mysql://db:3306/spring_auth" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="password" \
  spring-authenticate:latest
```

With Docker Compose (`docker-compose.yml`):
```yaml
version: '3.8'
services:
  app:
    build: .
    ports:
      - "8080:8080"
    environment:
      - DB_URL=jdbc:mysql://db:3306/spring_auth
      - DB_USERNAME=root
      - DB_PASSWORD=password
    depends_on:
      - db
  db:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=spring_auth
    volumes:
      - mysql-data:/var/lib/mysql

volumes:
  mysql-data:
```

### Option 3: Cloud Deployment

#### AWS Elastic Beanstalk
1. Package the application:
```bash
mvn clean package
```

2. Create `Procfile`:
```
web: java -jar target/spring-authenticate-1.0.0.jar --server.port=5000
```

3. Deploy using EB CLI:
```bash
eb init -p java-17 spring-authenticate
eb create spring-auth-prod
eb deploy
```

#### Google Cloud Platform (App Engine)
Create `app.yaml`:
```yaml
runtime: java17
instance_class: F2
env_variables:
  DB_URL: "jdbc:mysql://google/spring_auth?cloudSqlInstance=PROJECT:REGION:INSTANCE&socketFactory=com.google.cloud.sql.mysql.SocketFactory"
```

Deploy:
```bash
gcloud app deploy
```

#### Heroku
1. Create `Procfile`:
```
web: java -jar target/spring-authenticate-1.0.0.jar --server.port=$PORT
```

2. Deploy:
```bash
heroku create spring-auth-app
heroku addons:create cleardb:ignite
heroku config:set SPRING_PROFILES_ACTIVE=prod
git push heroku main
```

## Monitoring and Maintenance

### Health Checks
Add Spring Boot Actuator for health monitoring:
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-actuator</artifactId>
</dependency>
```

Configure endpoints:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized
```

### Database Backups
Set up regular MySQL backups:
```bash
#!/bin/bash
DATE=$(date +%Y%m%d_%H%M%S)
mysqldump -u root -p spring_auth > backup_$DATE.sql
```

### Log Rotation
Use logrotate for log management:
```
/var/log/spring-auth/*.log {
    daily
    rotate 14
    compress
    delaycompress
    notifempty
    create 0644 springauth springauth
}
```

## Performance Optimization

### Enable Caching
Add Redis for session management:
```properties
spring.session.store-type=redis
spring.redis.host=localhost
spring.redis.port=6379
```

### Enable Compression
```properties
server.compression.enabled=true
server.compression.mime-types=text/html,text/xml,text/plain,text/css,application/javascript,application/json
server.compression.min-response-size=1024
```

### Database Indexing
Ensure proper indexes are created (already included in schema.sql):
- Index on `username` for quick user lookups
- Index on `email` for duplicate checks
- Unique constraints to prevent duplicates

## Security Checklist

- [ ] Remove or change default test user credentials
- [ ] Use environment variables for sensitive data
- [ ] Enable HTTPS with valid SSL certificates
- [ ] Update OAuth redirect URIs to production URLs
- [ ] Configure CSRF protection (enabled by default)
- [ ] Set up rate limiting for login endpoints
- [ ] Enable security headers (XSS, Content-Type, etc.)
- [ ] Regular security updates for dependencies
- [ ] Database connection encryption
- [ ] Regular backups of user data
- [ ] Monitor application logs for suspicious activity
- [ ] Implement password complexity requirements

## Troubleshooting

### Application Won't Start
- Check environment variables are set correctly
- Verify database connectivity
- Review application logs

### OAuth Not Working
- Verify redirect URIs match exactly
- Check client credentials are for production environment
- Ensure HTTPS is properly configured

### Performance Issues
- Check database connection pool settings
- Monitor database query performance
- Enable caching where appropriate
- Consider scaling horizontally

## Additional Resources

- [Spring Boot Documentation](https://docs.spring.io/spring-boot/docs/current/reference/html/)
- [Spring Security OAuth 2.0](https://docs.spring.io/spring-security/reference/servlet/oauth2/index.html)
- [MySQL Production Best Practices](https://dev.mysql.com/doc/)
