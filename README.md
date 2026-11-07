# Hospital Management System

A Java Swing desktop application for managing hospital appointments and medical records. The application provides separate patient and doctor workflows and uses MySQL for persistent data.

## Features

- Patient and doctor registration and login
- Patient profiles and doctor specializations
- Doctor selection and appointment booking
- Appointment management for patients and doctors
- Medical history and diagnosis records
- Password hashing with BCrypt
- FlatLaf styling and bundled Inter font

## Requirements

- JDK 17 or newer
- Apache Maven
- MySQL 8

## Setup

1. Create the database by running the schema script in MySQL:

   ```sh
   mysql -u root -p < database/schema.sql
   ```

   **Warning:** The script drops and recreates the `hospital_db` database. Do not run it if you need to preserve data in an existing database.

2. Create `src/main/resources/config.properties` with your local MySQL connection settings:

   ```properties
   db.url=jdbc:mysql://localhost:3306/hospital_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
   db.user=root
   db.password=your_mysql_password
   ```

   This file is excluded from Git so local credentials are not published. Update the URL, username, and password to match your MySQL setup.

3. From the project root, compile and run the application:

   ```sh
   mvn clean compile
   mvn exec:java
   ```

## Project structure

```text
database/                 MySQL schema
src/main/java/com/hospital/
  dao/                    Database access objects
  db/                     Database connection
  model/                  Application data models
  ui/                     Swing interface and patient/doctor screens
  util/                   Validation, session, and password utilities
src/main/resources/       Local application configuration
pom.xml                   Maven build and dependencies
```

## Notes

- Maven downloads the dependencies declared in `pom.xml`.
- The MySQL server must be running and the schema must be initialized before using database-backed features.
- `target/` contains generated build output and is not tracked by Git.
