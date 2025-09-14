-- schema.sql
-- Create database and tables for Medicine Reminder & Doctor-Patient system

CREATE DATABASE IF NOT EXISTS medicine_system;
USE medicine_system;

-- users table: role = 'doctor' or 'patient'
CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    hospital_id VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    role ENUM('doctor','patient') NOT NULL,
    password VARCHAR(255) NOT NULL,
    assigned_doctor_id INT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (assigned_doctor_id) REFERENCES users(user_id) ON DELETE SET NULL
);

-- medicines: prescribed by doctor to patient
CREATE TABLE IF NOT EXISTS medicines (
    med_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    name VARCHAR(100) NOT NULL,
    dosage VARCHAR(50) NOT NULL,
    frequency VARCHAR(50) NOT NULL,
    time_of_day TIME NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- health_data: patient vitals
CREATE TABLE IF NOT EXISTS health_data (
    record_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    bp VARCHAR(20),
    sugar VARCHAR(20),
    weight VARCHAR(20),
    symptoms VARCHAR(255),
    record_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- lab_reports: path to uploaded file (we store path/filename)
CREATE TABLE IF NOT EXISTS lab_reports (
    report_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    file_path VARCHAR(255) NOT NULL,
    status ENUM('Pending','Reviewed') DEFAULT 'Pending',
    doctor_comments VARCHAR(255),
    uploaded_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- doctor_notes: notes from doctor to patient
CREATE TABLE IF NOT EXISTS doctor_notes (
    note_id INT AUTO_INCREMENT PRIMARY KEY,
    doctor_id INT NOT NULL,
    patient_id INT NOT NULL,
    message TEXT,
    note_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- appointments: patient requests confirmed by doctors
CREATE TABLE IF NOT EXISTS appointments (
    appt_id INT AUTO_INCREMENT PRIMARY KEY,
    patient_id INT NOT NULL,
    doctor_id INT NOT NULL,
    appt_date DATE NOT NULL,
    appt_time TIME NOT NULL,
    status ENUM('Pending','Confirmed','Rescheduled','Cancelled') DEFAULT 'Pending',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (patient_id) REFERENCES users(user_id) ON DELETE CASCADE,
    FOREIGN KEY (doctor_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- Sample seed data: one doctor and one patient
-- NOTE: password stored in plain text for simplicity in this demo. In real app hash passwords.
INSERT IGNORE INTO users (hospital_id, name, role, password) VALUES
('D100', 'Dr. Arun Kumar', 'doctor', 'docpass'),
('P101', 'John Doe', 'patient', 'patientpass');

-- Link patient to doctor (update assigned_doctor_id)
UPDATE users p
JOIN users d ON d.hospital_id = 'D100'
SET p.assigned_doctor_id = d.user_id
WHERE p.hospital_id = 'P101';

-- Insert a sample medicine assigned by the doctor to patient
INSERT INTO medicines (patient_id, doctor_id, name, dosage, frequency, time_of_day)
SELECT p.user_id, d.user_id, 'Paracetamol', '500mg', '2 times a day', '08:00:00'
FROM users p CROSS JOIN users d
WHERE p.hospital_id = 'P101' AND d.hospital_id = 'D100';

-- Insert sample health_data
INSERT INTO health_data (patient_id, bp, sugar, weight, symptoms)
SELECT user_id, '120/80', '95', '70', 'No major symptoms' FROM users
WHERE hospital_id='P101';
