# Quick Start Guide

## 1. Prerequisites

Ensure you have installed:
- Java 17 or higher
- Maven 3.6+
- MySQL (for production deployment)

## 2. Setup Environment Variables

Copy the example environment file:
```bash
cp .env.example .env
```

Edit `.env` with your actual credentials:
- Facebook App ID and Secret
- Instagram App ID and Secret  
- TikTok Client Key and Secret
- JWT secret key
- Database credentials

## 3. Run Application

### Development Mode (H2 Database)
```bash
mvn spring-boot:run
```

### Production Mode (MySQL)
```bash
mvn spring-boot:run -Dspring-boot.run.profiles=prod
```

## 4. Access Application

- **Backend API**: http://localhost:8080
- **H2 Console**: http://localhost:8080/h2-console
  - JDBC URL: `jdbc:h2:mem:suoidesign`
  - Username: `sa`
  - Password: (empty)

## 5. Test API Endpoints

### Register User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "fullName": "Test User",
    "email": "test@example.com",
    "password": "password123"
  }'
```

### Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "test@example.com",
    "password": "password123"
  }'
```

## 6. Frontend Integration

The backend serves frontend files from the parent directory:
- User pages: `/user/*`
- Admin pages: `/admin/*`
- Static assets: `/assets/*`

## 7. OAuth Setup

### Facebook/Instagram
1. Go to [Facebook Developers](https://developers.facebook.com/)
2. Create app with Business type
3. Add Facebook Login and Instagram Basic Display products
4. Set redirect URI: `http://localhost:8080/oauth2/callback/facebook` (or `/instagram`)
5. Get App ID and Secret from dashboard

### TikTok
1. Go to [TikTok for Developers](https://developers.tiktok.com/)
2. Create new app
3. Set redirect URI: `http://localhost:8080/oauth2/callback/tiktok`
4. Get Client Key and Secret from dashboard

## 8. Common Issues

### Port Already in Use
```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9
```

### Database Connection Issues
- Verify MySQL is running
- Check database credentials in `.env`
- Ensure database exists

### OAuth Callback Issues
- Verify redirect URIs match in developer consoles
- Check app is in development mode
- Ensure proper scopes are configured

## 9. Development Tips

- Use hot reload: Spring Boot DevTools is included
- Check logs for detailed error messages
- Use H2 console for database inspection during development
- Test OAuth flows with test accounts, not personal accounts

## 10. Production Deployment

1. Update `application-prod.yml` with production settings
2. Set environment variables in production environment
3. Build JAR: `mvn clean package`
4. Run: `java -jar target/suoi-design-backend-1.0.0.jar --spring.profiles.active=prod`
