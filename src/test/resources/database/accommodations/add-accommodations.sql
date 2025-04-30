INSERT INTO users (email, first_name, last_name, password, role, is_deleted)
VALUES ('testuser@example.com', 'John', 'Doe', 'password', 'USER', false);

INSERT INTO accommodations (location, size, price, amenities, availability, daily_rate, is_deleted, type)
VALUES
  ('Kyiv', 'LARGE', 1000.00, '{"WiFi","TV"}', 10, 0.0, false, 'APARTMENT'),
  ('Lviv', 'MEDIUM', 750.00, '{"WiFi"}', 5, 0.0, false, 'APARTMENT');
