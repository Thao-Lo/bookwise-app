
ALTER TABLE users 
ADD COLUMN verification_code VARCHAR(64);

ALTER TABLE users 
ADD COLUMN email_verified BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE users 
ADD COLUMN code_expiration_time TIMESTAMP;