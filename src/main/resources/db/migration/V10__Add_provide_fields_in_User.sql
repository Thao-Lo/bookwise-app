-- Text must be compared and sorted: case sensitive and byte by byte: keep it unique
-- All current users will have LOCAL and NULL values
ALTER TABLE users
MODIFY username VARCHAR(100) COLLATE utf8_bin,
ADD COLUMN provider ENUM('LOCAL', 'GOOGLE') NOT NULL DEFAULT 'LOCAL',
ADD COLUMN provider_id VARCHAR(255) NULL;
