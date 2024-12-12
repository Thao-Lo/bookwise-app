-- Update status values from lowercase to uppercase
UPDATE guest_reservation SET status = 'BOOKED' WHERE status = 'booked';
UPDATE guest_reservation SET status = 'CANCELLED' WHERE status = 'cancelled';

-- Update default value of the status column
ALTER TABLE guest_reservation MODIFY status ENUM('BOOKED', 'CANCELLED') NOT NULL DEFAULT 'BOOKED';