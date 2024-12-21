--ADDRESSES
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Main Street', 101, 'New York', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Elm Street', 202, 'Los Angeles', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Oak Avenue', 303, 'Chicago', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Pine Lane', 404, 'Houston', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Maple Drive', 505, 'Phoenix', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Cedar Boulevard', 606, 'Philadelphia', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Birch Road', 707, 'San Antonio', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Walnut Street', 808, 'San Diego', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Willow Way', 909, 'Dallas', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Poplar Court', 1010, 'San Jose', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Aspen Lane', 1111, 'Austin', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Spruce Circle', 1212, 'Jacksonville', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Cherry Street', 1313, 'Fort Worth', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Hickory Avenue', 1414, 'Columbus', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Sycamore Boulevard', 1515, 'Charlotte', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Chestnut Lane', 1616, 'San Francisco', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Dogwood Road', 1717, 'Indianapolis', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Fir Drive', 1818, 'Seattle', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Linden Way', 1919, 'Denver', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Magnolia Court', 2020, 'Washington', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Palm Street', 2121, 'Boston', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Redwood Avenue', 2222, 'El Paso', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Sequoia Boulevard', 2323, 'Nashville', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Cypress Lane', 2424, 'Detroit', 'USA');
INSERT INTO addresses (street, number_of_building, city, country) VALUES ('Juniper Road', 2525, 'Oklahoma City', 'USA');



-- USERS
INSERT INTO users (first_name, last_name, username, password, email, role, deleted, address_id) VALUES
('John', 'Doe', 'johndoe', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'johndoe@example.com', 'GUEST', false, 1),
('Jane', 'Smith', 'janesmith', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'janesmith@example.com', 'GUEST', false, 2),
('Robert', 'Johnson', 'robertjohnson', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'robertjohnson@example.com', 'GUEST', false, 3),
('Emily', 'Davis', 'emilydavis', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'emilydavis@example.com', 'GUEST', false, 4),
('Michael', 'Brown', 'michaelbrown', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'michaelbrown@example.com', 'GUEST', false, 5),
('Sarah', 'Miller', 'sarahmiller', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'sarahmiller@example.com', 'GUEST', false, 6),
('David', 'Wilson', 'davidwilson', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'davidwilson@example.com', 'GUEST', false, 7),
('Sophia', 'Moore', 'sophiamoore', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'sophiamoore@example.com', 'GUEST', false, 8),
('Daniel', 'Taylor', 'danieltaylor', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'danieltaylor@example.com', 'GUEST', false, 9),
('Olivia', 'Anderson', 'oliviaanderson', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'oliviaanderson@example.com', 'GUEST', false, 10),
('James', 'Thomas', 'jamesthomas', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'jamesthomas@example.com', 'GUEST', false, 11),
('Ella', 'Jackson', 'ellajackson', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'ellajackson@example.com', 'GUEST', false, 12),
('William', 'White', 'williamwhite', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'williamwhite@example.com', 'GUEST', false, 13),
('Ava', 'Harris', 'avaharris', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'avaharris@example.com', 'GUEST', false, 14),
('Christopher', 'Martin', 'christophermartin', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'christophermartin@example.com', 'GUEST', false, 15),
('Isabella', 'Thompson', 'isabellathompson', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'isabellathompson@example.com', 'HOST', false, 16),
('Joseph', 'Garcia', 'josephgarcia', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'josephgarcia@example.com', 'HOST', false, 17),
('Mia', 'Martinez', 'miamartinez', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'miamartinez@example.com', 'HOST', false, 18),
('Alexander', 'Robinson', 'alexanderrobinson', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'alexanderrobinson@example.com', 'HOST', false, 19),
('Amelia', 'Clark', 'ameliaclarck', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'ameliaclarck@example.com', 'HOST', false, 20),
('Ethan', 'Rodriguez', 'ethanrodriguez', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'ethanrodriguez@example.com', 'HOST', false, 21),
('Charlotte', 'Lewis', 'charlottelewis', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'charlottelewis@example.com', 'HOST', false, 22),
('Matthew', 'Lee', 'matthewlee', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'matthewlee@example.com', 'HOST', false, 23),
('Harper', 'Walker', 'harperwalker', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'harperwalker@example.com', 'HOST', false, 24),
('Andrew', 'Hall', 'andrewhall', '$2a$10$X7F3hUST4aTxWHikNRY7gek7xsjW352cF51QmbRehvE9rGJooAMhW', 'andrewhall@example.com', 'HOST', false, 25);


--RATES
INSERT INTO rates (rate, date, host_id, guest_id) VALUES
(4, '2024-12-19T10:00:00', 16, 1),
(5, '2024-12-19T11:00:00', 16, 2),
(1, '2024-12-19T12:00:00', 16, 3),
(2, '2024-12-19T13:00:00', 16, 4),
(1, '2024-12-19T14:00:00', 16, 5),
(5, '2024-12-19T15:00:00', 16, 6),
(5, '2024-12-19T16:00:00', 16, 7),
(3, '2024-12-19T17:00:00', 16, 8),
(2, '2024-12-19T18:00:00', 16, 9),
(1, '2024-12-19T19:00:00', 16, 10),
(2, '2024-12-19T20:00:00', 17, 11),
(4, '2024-12-19T21:00:00', 17, 12),
(3, '2024-12-19T22:00:00', 17, 13),
(2, '2024-12-19T23:00:00', 17, 14),
(1, '2024-12-20T00:00:00', 17, 15),
(4, '2024-12-20T01:00:00', 18, 1),
(5, '2024-12-20T02:00:00', 18, 2),
(3, '2024-12-20T03:00:00', 18, 3),
(2, '2024-12-20T04:00:00', 18, 4),
(1, '2024-12-20T05:00:00', 18, 5),
(4, '2024-12-20T06:00:00', 18, 6),
(1, '2024-12-20T07:00:00', 18, 7),
(2, '2024-12-20T08:00:00', 18, 8),
(2, '2024-12-20T09:00:00', 18, 9),
(1, '2024-12-20T10:00:00', 18, 10),
(5, '2024-12-20T11:00:00', 19, 11),
(5, '2024-12-20T12:00:00', 19, 12),
(4, '2024-12-20T13:00:00', 19, 13),
(2, '2024-12-20T14:00:00', 19, 14),
(1, '2024-12-20T15:00:00', 19, 15),
(2, '2024-12-20T16:00:00', 20, 1),
(4, '2024-12-20T17:00:00', 20, 2),
(4, '2024-12-20T18:00:00', 20, 3),
(2, '2024-12-20T19:00:00', 20, 4),
(4, '2024-12-20T20:00:00', 20, 5),
(4, '2024-12-20T21:00:00', 21, 6),
(1, '2024-12-20T22:00:00', 21, 7),
(1, '2024-12-20T23:00:00', 21, 8),
(2, '2024-12-21T00:00:00', 21, 9),
(2, '2024-12-21T01:00:00', 21, 10),
(4, '2024-12-21T02:00:00', 22, 11),
(4, '2024-12-21T03:00:00', 22, 12),
(3, '2024-12-21T04:00:00', 22, 13),
(2, '2024-12-21T05:00:00', 22, 14),
(1, '2024-12-21T06:00:00', 22, 15),
(5, '2024-12-21T07:00:00', 23, 1),
(2, '2024-12-21T08:00:00', 23, 2),
(3, '2024-12-21T09:00:00', 23, 3),
(2, '2024-12-21T10:00:00', 23, 4),
(1, '2024-12-21T11:00:00', 23, 5);
