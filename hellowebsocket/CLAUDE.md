# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a Spring Boot 3.5.4 WebSocket demo application using Java 21. The project demonstrates basic WebSocket functionality with Spring Boot, using Maven for build management.

## Technology Stack

- **Java**: 21
- **Spring Boot**: 3.5.4
- **Build Tool**: Maven
- **Key Dependencies**:
  - spring-boot-starter-websocket (WebSocket support)
  - spring-boot-starter-web (Web MVC)
  - spring-boot-starter-thymeleaf (Template engine)
  - spring-boot-devtools (Development tools)
  - h2 (In-memory database)
  - lombok (Code generation)

## Development Commands

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run the application
./mvnw spring-boot:run

# Build and package
./mvnw clean package

# Run the packaged JAR
java -jar target/hellowebsocket-0.0.1-SNAPSHOT.jar
```

### Testing
```bash
# Run all tests
./mvnw test

# Run specific test class
./mvnw test -Dtest=HellowebsocketApplicationTests
```

### Development
```bash
# Clean and rebuild
./mvnw clean install

# Skip tests during build
./mvnw clean package -DskipTests
```

## Project Structure

- **Main Application**: `src/main/java/com/ezlevup/hellowebsocket/HellowebsocketApplication.java`
- **Configuration**: `src/main/resources/application.properties`
- **Static Resources**: `src/main/resources/static/` (currently empty)
- **Templates**: `src/main/resources/templates/` (currently empty, for Thymeleaf)
- **Tests**: `src/test/java/com/ezlevup/hellowebsocket/`

## Architecture Notes

This is a minimal Spring Boot application template. The main application class uses `@SpringBootApplication` annotation which provides:
- Auto-configuration
- Component scanning 
- Configuration properties

The project is set up for WebSocket development but currently contains only the basic Spring Boot structure. Future WebSocket implementations should:
- Create WebSocket configuration classes
- Implement message handlers
- Add frontend templates in the templates directory
- Utilize static resources for JavaScript/CSS

## Development Environment

- Uses Spring Boot DevTools for hot reloading during development
- H2 database available for data persistence (in-memory by default)
- Lombok configured for reducing boilerplate code