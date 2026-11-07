-- ============================================================
-- Hospital Management System - Database Schema
-- Yeh file database ki poori structure define karti hai
-- MySQL 8 ke liye likhi gayi hai
-- ============================================================

-- Pehle purana database hata do (agar hai)
DROP DATABASE IF EXISTS hospital_db;
CREATE DATABASE hospital_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE hospital_db;

-- ============================================================
-- users table: login ke liye (email + hashed password + role)
-- ============================================================
CREATE TABLE users (
    user_id    INT          AUTO_INCREMENT PRIMARY KEY,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,       -- BCrypt hashed
    role       ENUM('Patient','Doctor') NOT NULL,
    created_at TIMESTAMP    DEFAULT CURRENT_TIMESTAMP
);

-- ============================================================
-- patients table: patient ki personal info
-- ============================================================
CREATE TABLE patients (
    patient_id     INT          AUTO_INCREMENT PRIMARY KEY,
    user_id        INT          NOT NULL UNIQUE,
    full_name      VARCHAR(100) NOT NULL,
    contact_number VARCHAR(15),
    dob            DATE,
    gender         ENUM('Male','Female','Other'),
    blood_group    VARCHAR(5),
    address        TEXT,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- doctors table: doctor ki professional info
-- ============================================================
CREATE TABLE doctors (
    doc_id          INT          AUTO_INCREMENT PRIMARY KEY,
    user_id         INT          NOT NULL UNIQUE,
    full_name       VARCHAR(100) NOT NULL,
    contact_number  VARCHAR(15),
    specialization  VARCHAR(100) NOT NULL,
    experience_years INT         DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- ============================================================
-- appointments table: patient aur doctor ke beech meeting
-- ============================================================
CREATE TABLE appointments (
    appointment_id INT       AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT       NOT NULL,
    doc_id         INT       NOT NULL,
    appointment_date DATE    NOT NULL,
    appointment_time TIME    NOT NULL,
    status         ENUM('Scheduled','Completed','Cancelled') DEFAULT 'Scheduled',
    reason         TEXT,
    FOREIGN KEY (patient_id) REFERENCES patients(patient_id) ON DELETE CASCADE,
    FOREIGN KEY (doc_id)     REFERENCES doctors(doc_id)      ON DELETE CASCADE
);

-- ============================================================
-- medical_records table: completed appointment ke baad doctor
--                        jo notes likhta hai
-- ============================================================
CREATE TABLE medical_records (
    record_id      INT       AUTO_INCREMENT PRIMARY KEY,
    patient_id     INT       NOT NULL,
    doc_id         INT       NOT NULL,
    appointment_id INT,
    diagnosis_log  TEXT      NOT NULL,
    prescription   TEXT,
    record_date    DATE      NOT NULL,
    FOREIGN KEY (patient_id)     REFERENCES patients(patient_id)         ON DELETE CASCADE,
    FOREIGN KEY (doc_id)         REFERENCES doctors(doc_id)              ON DELETE CASCADE,
    FOREIGN KEY (appointment_id) REFERENCES appointments(appointment_id) ON DELETE SET NULL
);

