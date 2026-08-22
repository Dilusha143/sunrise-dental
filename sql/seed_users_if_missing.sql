USE sunrise_dental;

INSERT INTO users (username, password_hash, full_name, role, dentist_id)
SELECT * FROM (SELECT
    'admin' AS username,
    '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9' AS password_hash,
    'System Admin' AS full_name,
    'ADMIN' AS role,
    NULL AS dentist_id
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'admin');

INSERT INTO users (username, password_hash, full_name, role, dentist_id)
SELECT * FROM (SELECT
    'reception1', '5145dba3b6bda2d610d2c5c435a1c2481eefd3146b6a7e004ad73f794386e031',
    'Receptionist One', 'RECEPTIONIST', NULL
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'reception1');

INSERT INTO users (username, password_hash, full_name, role, dentist_id)
SELECT * FROM (SELECT
    'reception2', '5145dba3b6bda2d610d2c5c435a1c2481eefd3146b6a7e004ad73f794386e031',
    'Receptionist Two', 'RECEPTIONIST', NULL
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'reception2');

-- Note: these two assume dentists.dentist_id 1 and 2 already exist
-- (they are created by the main schema.sql seed data).
INSERT INTO users (username, password_hash, full_name, role, dentist_id)
SELECT * FROM (SELECT
    'dr.perera', '22990c57fbef2aeac16a2bf5e0caeafc43717c99e2040b0e3ac8d468d42794f0',
    'Dr. Nimal Perera', 'DENTIST', 1
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'dr.perera');

INSERT INTO users (username, password_hash, full_name, role, dentist_id)
SELECT * FROM (SELECT
    'dr.silva', '22990c57fbef2aeac16a2bf5e0caeafc43717c99e2040b0e3ac8d468d42794f0',
    'Dr. Kavindi Silva', 'DENTIST', 2
) AS tmp
WHERE NOT EXISTS (SELECT 1 FROM users WHERE username = 'dr.silva');
