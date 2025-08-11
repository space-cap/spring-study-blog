# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview
This is "Smile Dental Clinic" - a Spring Boot web application for managing dental clinic operations including appointments, patients, billing, medical records, and a chatbot inquiry system. Built with Spring Boot 3.5.4, Spring Security, JPA/Hibernate, and Thymeleaf templating.

## Development Commands

### Build and Run
```bash
# Build the project
./mvnw clean compile

# Run the application (development mode with DevTools)
./mvnw spring-boot:run

# Package the application
./mvnw clean package

# Run tests
./mvnw test
```

### Windows
```cmd
# Use mvnw.cmd on Windows
mvnw.cmd clean compile
mvnw.cmd spring-boot:run
mvnw.cmd test
```

## Architecture Overview

### Core Structure
- **Main Application**: `SmileApplication.java` with `@EnableScheduling` for automated tasks
- **Package Structure**: `com.develead.smile`
  - `config/` - Security and data initialization
  - `controller/` - Web controllers (Admin, Auth, Home, Appointment)
  - `domain/` - JPA entities with change log tracking
  - `dto/` - Data transfer objects
  - `repository/` - JPA repositories
  - `service/` - Business logic layer

### Key Features
- **Multi-role Authentication**: Admin and regular user roles with Spring Security
- **Audit Trail**: Change log entities track modifications to core business objects
- **Email Notifications**: Configured with Gmail SMTP
- **PDF Generation**: Uses OpenHTMLToPDF for report generation
- **Scheduled Tasks**: Notification scheduler for automated operations
- **Chatbot Integration**: Inquiry system with logging

### Database Configuration
- **MySQL Database**: `dentist_chatbot_db` on localhost:3306
- **JPA/Hibernate**: DDL auto-update enabled, SQL logging active
- **Credentials**: Check `application.properties` (username: steve, password: doolman)

### Frontend
- **Templates**: Thymeleaf with layout dialect in `src/main/resources/templates/`
  - Admin interface in `admin/` subfolder
  - Common fragments in `fragments/`
  - Layout templates in `layouts/`
- **Static Resources**: CSS and JavaScript in `src/main/resources/static/`
- **Korean Fonts**: NanumGothic fonts included for Korean text support

### Key Services
- **NotificationScheduler**: Handles automated notifications
- **EmailService**: Gmail integration for email communications  
- **ReportService**: PDF report generation
- **Multiple Change Log Services**: Track entity modifications
- **CustomUserDetailsService**: Spring Security user management

### Development Notes
- **DevTools**: Enabled for automatic restart and live reload
- **Thymeleaf Cache**: Disabled in development for hot reloading
- **Server Port**: 8080 (default)
- **Timezone**: Asia/Seoul