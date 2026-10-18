DROP DATABASE IF EXISTS sistemaReservas;

CREATE DATABASE sistemaReservas
	CHARACTER SET utf8mb4
	COLLATE utf8mb4_unicode_ci;

CREATE USER IF NOT EXISTS 'reservasApp'@'localhost' IDENTIFIED BY '1234';
GRANT ALL PRIVILEGES ON sistemaReservas.* TO 'reservasApp'@'localhost';