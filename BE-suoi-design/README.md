# SuoiDesign Backend

Spring Boot backend for the AI Content & Automation Platform.

## Tech Stack

- Java 17
- Spring Boot 3.2.0
- Spring Security
- Spring Data JPA
- Thymeleaf
- OAuth2 Client (Facebook, Instagram)
- JWT Authentication
- H2 Database (development)
- MySQL (production)
- Lombok

## Project Structure

```
BE-suoi-design/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── suoidesign/
│   │   │           ├── SuoiDesignApplication.java
│   │   │           ├── config/          # Security, OAuth configuration
│   │   │           ├── controller/      # REST API controllers
│   │   │           ├── dto/             # Data Transfer Objects
│   │   │           ├── entity/          # JPA entities
│   │   │           ├── exception/       # Exception handlers
│   │   │           ├── repository/      # JPA repositories
│   │   │           ├── service/         # Business logic
│   │   │           └── util/            # Utility classes (JWT)
│   │   └── resources/
│   │       ├── application.yml
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/
│       └── java/
│           └── com/
│               └── suoidesign/
│                   └── SuoiDesignApplicationTests.java
├── pom.xml
└── .gitignore
```

## Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL (for production)

### Installation

1. Clone the repository
2. Navigate to the backend directory:
   ```bash
   cd BE-suoi-design
   ```

3. Configure environment variables:
   ```bash
   # Create .env file or set environment variables
   FACEBOOK_CLIENT_ID=your_facebook_app_id
   FACEBOOK_CLIENT_SECRET=your_facebook_app_secret
   INSTAGRAM_CLIENT_ID=your_instagram_app_id
   INSTAGRAM_CLIENT_SECRET=your_instagram_app_secret
   TIKTOK_CLIENT_ID=your_tiktok_client_key
   TIKTOK_CLIENT_SECRET=your_tiktok_client_secret
   JWT_SECRET=your-jwt-secret-key
   DB_HOST=localhost
   DB_PORT=3306
   DB_NAME=suoidesign
   DB_USERNAME=root
   DB_PASSWORD=your_password
   ```

4. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The application will start on `http://localhost:8080`

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login and get JWT token

### OAuth

- `GET /api/oauth/accounts` - Get user's connected social accounts
- `DELETE /api/oauth/accounts/{id}` - Remove a social account

### Social Posting

- `POST /api/social/batch-post` - Batch post to multiple social accounts

### Frontend Pages

The backend serves the frontend HTML files from the parent directory:
- `/user/*` - User pages
- `/admin/*` - Admin pages
- `/assets/*` - Static assets
- `/layouts/*` - Thymeleaf layouts

## Database Schema

### Users Table
```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar VARCHAR(255),
    role VARCHAR(20) NOT NULL,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### Social Accounts Table
```sql
CREATE TABLE social_accounts (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    platform VARCHAR(20) NOT NULL,
    platform_account_id VARCHAR(255) NOT NULL,
    account_name VARCHAR(255),
    profile_picture VARCHAR(255),
    access_token TEXT NOT NULL,
    refresh_token TEXT,
    token_expires_at TIMESTAMP,
    followers_count INT,
    is_active BOOLEAN DEFAULT TRUE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### Campaigns Table
```sql
CREATE TABLE campaigns (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    status VARCHAR(20) NOT NULL,
    media_url VARCHAR(255),
    content TEXT,
    scheduled_time TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

## OAuth Integration

### Facebook/Instagram

1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create a new app (Business type)
3. Add "Facebook Login" and "Instagram Basic Display" products
4. Configure OAuth redirect URIs
5. Get App ID and App Secret

### TikTok

1. Go to [TikTok for Developers](https://developers.tiktok.com/)
2. Create a new app
3. Configure OAuth redirect URI
4. Get Client Key and Client Secret

## Development

### Running with H2 Database (Development)

The application uses H2 database by default. Access the H2 console at:
```
http://localhost:8080/h2-console
```
- JDBC URL: `jdbc:h2:mem:suoidesign`
- Username: `sa`
- Password: (leave empty)

### Running with MySQL (Production)

1. Update `application-prod.yml` with your MySQL credentials
2. Run with production profile:
   ```bash
   mvn spring-boot:run -Dspring-boot.run.profiles=prod
   ```

## Testing

Run tests:
```bash
mvn test
```

## Building

Build the application:
```bash
mvn clean package
```

Run the JAR file:
```bash
java -jar target/suoi-design-backend-1.0.0.jar
```

## Security

- JWT-based authentication
- BCrypt password encryption
- OAuth 2.0 for social media integration
- CORS enabled for frontend communication

## Configuration

### Application Properties

- `application.yml` - Main configuration
- `application-dev.yml` - Development profile
- `application-prod.yml` - Production profile

### Environment Variables

- `FACEBOOK_CLIENT_ID` - Facebook App ID
- `FACEBOOK_CLIENT_SECRET` - Facebook App Secret
- `INSTAGRAM_CLIENT_ID` - Instagram App ID
- `INSTAGRAM_CLIENT_SECRET` - Instagram App Secret
- `TIKTOK_CLIENT_ID` - TikTok Client Key
- `TIKTOK_CLIENT_SECRET` - TikTok Client Secret
- `JWT_SECRET` - JWT secret key
- `DB_HOST` - Database host
- `DB_PORT` - Database port
- `DB_NAME` - Database name
- `DB_USERNAME` - Database username
- `DB_PASSWORD` - Database password

## Frontend Integration

The backend serves static files from the parent directory structure:
- Frontend HTML files are in `../suoi-design/user/` and `../suoi-design/admin/`
- Static assets are in `../suoi-design/assets/`
- Thymeleaf layouts are in `../suoi-design/layouts/`

The `SuoiDesignApplication.java` configures resource handlers to serve these files.

## License

This project is part of SuoiDesign AI Workspace.
