-- Update default value of the status column

ALTER TABLE slots MODIFY status ENUM('AVAILABLE', 'HOLDING', 'UNAVAILABLE') NOT NULL DEFAULT 'AVAILABLE';

