# Spring Boot Backend Application

A robust Spring Boot backend application with REST API capabilities, security, validation, and testing infrastructure.

## 🚀 Features

- **Spring Web**: RESTful API endpoints
- **Spring Security**: Authentication and authorization framework
- **Spring Data JPA**: Database operations with Hibernate
- **Lombok**: Reduced boilerplate code
- **Validation**: Request validation using Bean Validation
- **H2 Database**: In-memory database for development
- **Spring Boot DevTools**: Hot reload during development
- **Comprehensive Testing**: Unit and integration testing support

## 📋 Prerequisites

- **Java 17** or higher
- **Maven 3.6+** (or use the Maven wrapper included)
- **IDE** (IntelliJ IDEA, Eclipse, or VS Code recommended)

## 🛠️ Setup Instructions

### 1. Clone or Navigate to the Project

```bash
cd backend
```

### 2. Configure Environment Variables

Copy the `.env.example` file to `.env` and update with your local database credentials:

```bash
cp .env.example .env
```

Edit `.env` and update:
```env
DB_URL=jdbc:postgresql://localhost:5432/aji_t9aser_db
DB_USERNAME=postgres
DB_PASSWORD=your_password
SERVER_PORT=8080
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000
```

**⚠️ Important**: The `.env` file is git-ignored and should **never** be committed to version control.

#### Option A: Using IDE Run Configuration (Recommended for Local Development)

**IntelliJ IDEA:**
1. Open Run → Edit Configurations
2. Select your Spring Boot run configuration
3. Under "Environment variables", add:
   ```
   DB_URL=jdbc:postgresql://localhost:5432/aji_t9aser_db;DB_USERNAME=postgres;DB_PASSWORD=your_password;SERVER_PORT=8080;JWT_SECRET=your-secret-key;JWT_EXPIRATION=86400000
   ```
4. Apply and run

**VS Code:**
1. Create/edit `.vscode/launch.json`
2. Add environment variables to the launch configuration

#### Option B: Using Docker Compose (Automatically loads .env)

```bash
docker compose up --build -d
```

### 3. Build the Project

Using Maven wrapper (recommended):
```bash
mvnw clean install
```

Or using system Maven:
```bash
mvn clean install
```

### 4. Run the Application

**Local (with IDE environment variables):**
```bash
mvnw spring-boot:run
```

**Docker:**
```bash
docker compose up --build -d
```

The application will start on **http://localhost:8080** (or **http://localhost:8081** with Docker)

## 📖 API Documentation

### Swagger UI (OpenAPI)

Once the application is running, visit the interactive API documentation:

```
http://localhost:8080/swagger-ui.html
```

This provides:
- ✅ All available endpoints
- ✅ Request/response schemas
- ✅ Test API calls directly from the browser
- ✅ Authentication details

### Alternative: OpenAPI JSON

Access the raw OpenAPI spec:
```
http://localhost:8080/v3/api-docs
```

## 🧪 Testing the Application

### Using Postman

1. **Health Check Endpoint**
   - **Method**: GET
   - **URL**: `http://localhost:8080/api/health`
   - **Expected Response**:
     ```json
     {
       "status": "UP",
       "timestamp": "2026-02-16T14:25:00",
       "message": "Backend application is running successfully"
     }
     ```

### Using cURL

```bash
curl http://localhost:8081/api/health
```

## 🐳 Docker

### Starting with Docker Compose

To run the backend with PostgreSQL using Docker:

```bash
cd backend
docker compose up --build -d
```

### Managing Containers

Check container status:
```bash
docker compose ps
```

View backend logs:
```bash
docker compose logs backend --tail=200
```

View database logs:
```bash
docker compose logs postgres --tail=100
```

Stop and remove containers:
```bash
docker compose down
```

Health check (when running on Docker):
```bash
curl http://localhost:8081/api/health
```

### Important: Environment Variables in Docker

Docker Compose automatically loads environment variables from the `.env` file in the `backend/` directory. Make sure your `.env` file is properly configured before running `docker compose up`.
- **Password**: (leave empty)

## 📁 Project Structure

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/backend/
│   │   │   ├── BackendApplication.java       # Main application entry point
│   │   │   ├── config/
│   │   │   │   └── SecurityConfig.java       # Security configuration
│   │   │   ├── controller/
│   │   │   │   └── HealthController.java     # REST controllers
│   │   │   ├── service/                      # Business logic layer
│   │   │   ├── repository/                   # Data access layer
│   │   │   ├── model/                        # Entity classes
│   │   │   ├── dto/                          # Data Transfer Objects
│   │   │   ├── exception/                    # Custom exceptions
│   │   │   └── security/                     # Security components
│   │   └── resources/
│   │       ├── application.properties        # Main configuration
│   │       └── application-dev.properties    # Development profile
│   └── test/
│       └── java/com/example/backend/
│           └── BackendApplicationTests.java  # Test classes
├── pom.xml                                   # Maven dependencies
├── .gitignore                                # Git ignore rules
└── README.md                                 # This file
```

## 🔧 Running Tests

Run all tests:
```bash
mvnw test
```

Run tests with coverage:
```bash
mvnw clean test jacoco:report
```

## 📝 Configuration

### Application Properties

Key configurations in `application.properties`:
- **Server Port**: 8080
- **Database**: H2 in-memory
- **Active Profile**: dev
- **JPA**: Auto DDL update, SQL logging enabled

### Profiles

- **dev**: Development profile with detailed logging and error messages
- **prod**: Production profile (to be configured)

## 🔐 Security

The application uses Spring Security with:
- **CORS**: Enabled for `localhost:4200` and `localhost:3000`
- **CSRF**: Disabled for stateless API
- **Session Management**: Stateless (JWT-ready)
- **Password Encoding**: BCrypt

**Note**: Current security is permissive for development. Implement proper authentication before production deployment.

## 📦 Dependencies

| Dependency | Version | Purpose |
|------------|---------|---------|
| Spring Boot | 3.2.2 | Framework |
| Java | 17 | Language |
| Spring Web | - | REST API |
| Spring Security | - | Security |
| Spring Data JPA | - | Database |
| H2 Database | - | In-memory DB |
| Lombok | - | Code generation |
| Validation | - | Bean validation |

## 🎯 Next Steps

1. **Add Authentication**: Implement JWT-based authentication
2. **Create Entities**: Define your domain models in the `model` package
3. **Build Services**: Implement business logic in the `service` package
4. **Create Repositories**: Add JPA repositories for data access
5. **Add DTOs**: Create request/response objects in the `dto` package
6. **Exception Handling**: Implement global exception handling
7. **API Documentation**: Add Swagger/OpenAPI documentation
8. **Database Migration**: Switch to PostgreSQL/MySQL for production
9. **Write Tests**: Add comprehensive unit and integration tests

## 🤝 Contributing

1. Create a feature branch
2. Make your changes
3. Write/update tests
4. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 📞 Support

For issues or questions, please create an issue in the repository.
