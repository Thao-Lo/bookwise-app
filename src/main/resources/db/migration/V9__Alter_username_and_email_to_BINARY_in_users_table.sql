-- Change value to Binary for case sensitive check, must include data type VARCHAR(50),..
ALTER TABLE users MODIFY username VARCHAR(50) COLLATE utf8_bin;
ALTER TABLE users MODIFY email VARCHAR(100) COLLATE utf8_bin;