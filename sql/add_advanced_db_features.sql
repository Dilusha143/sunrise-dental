USE sunrise_dental;

DROP FUNCTION IF EXISTS fn_appointment_treatment_total;
DROP PROCEDURE IF EXISTS sp_generate_bill;
DROP TRIGGER IF EXISTS trg_prevent_double_booking;

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
