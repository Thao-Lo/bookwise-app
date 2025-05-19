-- Ensure consistency of timestamps in `users` table (optional)
ALTER TABLE `bookwise`.`users`
MODIFY COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add timestamps to the `seats` table
ALTER TABLE `bookwise`.`seats`
ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

-- Add indexes to foreign key columns in `slots` and `guest_reservation`
CREATE INDEX idx_slots_date_id ON bookwise.slots (date_id);
CREATE INDEX idx_guest_reservation_user_id ON bookwise.guest_reservation (user_id);
CREATE INDEX idx_guest_reservation_slot_id ON bookwise.guest_reservation (slot_id);

-- Add timestamps to the `slots` table
ALTER TABLE `bookwise`.`slots`
ADD COLUMN `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
ADD COLUMN `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

