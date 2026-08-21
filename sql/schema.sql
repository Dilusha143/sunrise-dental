CREATE DATABASE IF NOT EXISTS sunrise_dental;
USE sunrise_dental;


CREATE TABLE IF NOT EXISTS dentists (
    dentist_id    INT AUTO_INCREMENT PRIMARY KEY,
    full_name     VARCHAR(100) NOT NULL,
    specialization VARCHAR(100)
);


CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name     VARCHAR(100) NOT NULL,
    role          ENUM('RECEPTIONIST', 'DENTIST', 'ADMIN') NOT NULL,
    dentist_id    INT NULL UNIQUE,
    created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id)
);


CREATE TABLE IF NOT EXISTS treatment_types (
    treatment_id   INT AUTO_INCREMENT PRIMARY KEY,
    treatment_name VARCHAR(100) NOT NULL,
    fee            DECIMAL(10,2) NOT NULL
);


CREATE TABLE IF NOT EXISTS appointments (
    appointment_number VARCHAR(20) PRIMARY KEY,
    patient_name        VARCHAR(100) NOT NULL,
    address              VARCHAR(255) NOT NULL,
    contact_number       VARCHAR(20)  NOT NULL,
    dentist_id           INT NOT NULL,
    appointment_date     DATE NOT NULL,
    appointment_time     TIME NOT NULL,
    status               ENUM('SCHEDULED','COMPLETED','CANCELLED') DEFAULT 'SCHEDULED',
    created_at           TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (dentist_id) REFERENCES dentists(dentist_id)
);


CREATE TABLE IF NOT EXISTS appointment_treatments (
    appointment_number VARCHAR(20) NOT NULL,
    treatment_id        INT NOT NULL,
    PRIMARY KEY (appointment_number, treatment_id),
    FOREIGN KEY (appointment_number) REFERENCES appointments(appointment_number),
    FOREIGN KEY (treatment_id) REFERENCES treatment_types(treatment_id)
);


CREATE TABLE IF NOT EXISTS bills (
    bill_id             INT AUTO_INCREMENT PRIMARY KEY,
    appointment_number  VARCHAR(20) NOT NULL,
    treatment_fee       DECIMAL(10,2) NOT NULL,
    consultation_fee    DECIMAL(10,2) NOT NULL,
    total_amount        DECIMAL(10,2) NOT NULL,
    generated_at        TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (appointment_number) REFERENCES appointments(appointment_number)
);


INSERT INTO dentists (full_name, specialization) VALUES
('Dr. Nimal Perera', 'General Dentistry'),
('Dr. Kavindi Silva', 'Orthodontics'),
('Dr. Ruwan Fernando', 'Oral Surgery');

INSERT INTO treatment_types (treatment_name, fee) VALUES
('Consultation', 1000.00),
('Tooth Filling', 3500.00),
('Tooth Extraction', 4000.00),
('Root Canal', 12000.00),
('Teeth Cleaning', 2500.00);

INSERT INTO users (username, password_hash, full_name, role, dentist_id) VALUES
('admin',      '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'System Admin',      'ADMIN',        NULL),
('reception1', '5145dba3b6bda2d610d2c5c435a1c2481eefd3146b6a7e004ad73f794386e031', 'Receptionist One',  'RECEPTIONIST', NULL),
('reception2', '5145dba3b6bda2d610d2c5c435a1c2481eefd3146b6a7e004ad73f794386e031', 'Receptionist Two',  'RECEPTIONIST', NULL),
('dr.perera',  '22990c57fbef2aeac16a2bf5e0caeafc43717c99e2040b0e3ac8d468d42794f0', 'Dr. Nimal Perera',   'DENTIST',      1),
('dr.silva',   '22990c57fbef2aeac16a2bf5e0caeafc43717c99e2040b0e3ac8d468d42794f0', 'Dr. Kavindi Silva',  'DENTIST',      2);


DELIMITER $$

CREATE FUNCTION fn_appointment_treatment_total(p_appointment_number VARCHAR(20))
RETURNS DECIMAL(10,2)
DETERMINISTIC
READS SQL DATA
BEGIN
    DECLARE v_total DECIMAL(10,2);
    SELECT COALESCE(SUM(t.fee), 0.00) INTO v_total
    FROM appointment_treatments at
    JOIN treatment_types t ON t.treatment_id = at.treatment_id
    WHERE at.appointment_number = p_appointment_number;
    RETURN v_total;
END$$

CREATE PROCEDURE sp_generate_bill(
    IN  p_appointment_number   VARCHAR(20),
    OUT p_treatment_fee        DECIMAL(10,2),
    OUT p_consultation_fee     DECIMAL(10,2),
    OUT p_total_amount         DECIMAL(10,2)
)
BEGIN
    DECLARE v_status VARCHAR(20);

    SELECT status INTO v_status
    FROM appointments
    WHERE appointment_number = p_appointment_number;

    IF v_status IS NULL OR v_status = 'CANCELLED' THEN
        SET p_treatment_fee = NULL;
        SET p_consultation_fee = NULL;
        SET p_total_amount = NULL;
    ELSE
        SET p_consultation_fee = 1000.00;
        IF v_status = 'COMPLETED' THEN
            SET p_treatment_fee = fn_appointment_treatment_total(p_appointment_number);
        ELSE
            SET p_treatment_fee = 0.00;
        END IF;
        SET p_total_amount = p_treatment_fee + p_consultation_fee;
    END IF;
END$$


CREATE TRIGGER trg_prevent_double_booking
BEFORE INSERT ON appointments
FOR EACH ROW
BEGIN
    DECLARE v_clash_count INT;

    SELECT COUNT(*) INTO v_clash_count
    FROM appointments
    WHERE dentist_id = NEW.dentist_id
      AND appointment_date = NEW.appointment_date
      AND appointment_time = NEW.appointment_time
      AND status IN ('SCHEDULED', 'COMPLETED');

    IF v_clash_count > 0 THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Double booking: this dentist already has an appointment at that date and time.';
    END IF;
END$$

DELIMITER ;
