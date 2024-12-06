-- Update status values from lowercase to uppercase
UPDATE slots SET status = 'AVAILABLE' WHERE status = 'available';
UPDATE slots SET status = 'UNAVAILABLE' WHERE status = 'unavailable';

-- Update default value of the status column
ALTER TABLE slots MODIFY status ENUM('AVAILABLE', 'UNAVAILABLE') NOT NULL DEFAULT 'AVAILABLE';

