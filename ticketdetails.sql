CREATE DATABASE ticketdetails;
USE ticketdetails;

CREATE TABLE reservations (
    pnr_number INT PRIMARY KEY,
    passenger_name VARCHAR(255),
    train_number VARCHAR(255),
    class_type VARCHAR(255),
    journey_date VARCHAR(255),
    departure_from VARCHAR(255),
    destination_to VARCHAR(255)
);
SELECT user();
SELECT current_user();
SELECT user, host,db, command FROM information_schema.processlist;
GRANT ALL PRIVILEGES ON database_name.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
select * from reservations;
CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE,
    password VARCHAR(255)
);
select * from users;
