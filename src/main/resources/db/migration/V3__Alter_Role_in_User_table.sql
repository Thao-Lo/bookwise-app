-- Update role values from lowercase to uppercase
UPDATE users SET role = 'GUEST' WHERE role = 'guest';
UPDATE users SET role = 'ADMIN' WHERE role = 'admin';

-- Update default value of the role column
ALTER TABLE users MODIFY role ENUM('GUEST', 'ADMIN') NOT NULL DEFAULT 'GUEST';