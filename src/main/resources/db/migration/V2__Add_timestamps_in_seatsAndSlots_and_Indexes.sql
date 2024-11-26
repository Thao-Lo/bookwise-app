-- Ensure consistency of timestamps in `users` table (optional)
ALTER TABLE `reservation_management`.`users`
MODIFY COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add timestamps to the `seats` table
ALTER TABLE `reservation_management`.`seats`
ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add indexes to foreign key columns in `slots` and `guest_reservation`
CREATE INDEX idx_slots_date_id ON reservation_management.slots (date_id);
CREATE INDEX idx_guest_reservation_user_id ON reservation_management.guest_reservation (user_id);
CREATE INDEX idx_guest_reservation_slot_id ON reservation_management.guest_reservation (slot_id);

-- Add timestamps to the `slots` table
ALTER TABLE `reservation_management`.`slots`
ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

