INSERT INTO roles (id, role_name) VALUES (1, 'USER') ON CONFLICT (id) DO NOTHING;
INSERT INTO roles (id, role_name) VALUES (2, 'ADMIN') ON CONFLICT (id) DO NOTHING;

INSERT INTO users (email, first_name, last_name, password, role_id, is_deleted)
VALUES ('testuser@example.com', 'John', 'Doe', 'password', 1, false);

INSERT INTO accommodations (location, size, price, amenities, availability, daily_rate, is_deleted, type)
VALUES
  ('Kyiv', 'LARGE', 1000.00, '{"WiFi","TV"}', 10, 0.0, false, 'APARTMENT'),
  ('Lviv', 'MEDIUM', 750.00, '{"WiFi"}', 5, 0.0, false, 'APARTMENT');
