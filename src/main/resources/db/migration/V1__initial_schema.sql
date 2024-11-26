CREATE TABLE `reservation_management`.`users` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(50) NOT NULL,
  `email` VARCHAR(100) NOT NULL,
  `password` VARCHAR(255) NOT NULL,
  `reset_token` VARCHAR(255) NULL,
  `reset_token_expiration` TIMESTAMP NULL,
  `role` ENUM('guest', 'admin') NOT NULL DEFAULT 'guest',
  `created_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC) VISIBLE,
  UNIQUE INDEX `email_UNIQUE` (`email` ASC) VISIBLE);

  CREATE TABLE `reservation_management`.`seats` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `seat_name` VARCHAR(45) NOT NULL,
  `capacity` INT NOT NULL,
  PRIMARY KEY (`id`));
  
  CREATE TABLE `reservation_management`.`dates` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `datetime` DATETIME NOT NULL,
  PRIMARY KEY (`id`));

  CREATE TABLE `reservation_management`.`slots` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `date_id` BIGINT NOT NULL,
  `seat_id` INT NOT NULL,
  `status` ENUM('available', 'unavailable') NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_date_id`
    FOREIGN KEY (`date_id`)
    REFERENCES `reservation_management`.`dates` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_seat_id`
    FOREIGN KEY (`seat_id`)
    REFERENCES `reservation_management`.`seats` (`id`)
    ON DELETE CASCADE
    ON UPDATE NO ACTION
);

CREATE TABLE `reservation_management`.`guest_reservation` (
  `id` BIGINT NOT NULL AUTO_INCREMENT,
  `user_id` BIGINT NOT NULL,
  `slot_id` BIGINT NOT NULL,
  `reservation_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `number_of_guest` INT NOT NULL,
  `status` ENUM('booked', 'cancelled') NOT NULL,
  PRIMARY KEY (`id`),
  CONSTRAINT `fk_user_id`
    FOREIGN KEY (`user_id`)
    REFERENCES `reservation_management`.`users` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE,
  CONSTRAINT `fk_slot_id`
    FOREIGN KEY (`slot_id`)
    REFERENCES `reservation_management`.`slots` (`id`)
    ON DELETE CASCADE
    ON UPDATE CASCADE);
