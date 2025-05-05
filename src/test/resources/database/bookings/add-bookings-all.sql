INSERT INTO users (id, email, first_name, last_name, password, role, is_deleted)
VALUES (1, 'user@example.com', 'John', 'Doe', 'encoded_password', 'USER', FALSE);

INSERT INTO accommodations (
    id, location, size, price, daily_rate, availability, type, is_deleted, amenities
) VALUES (
    1, 'Kyiv', 'MEDIUM', 1000, 150, 5, 'HOUSE', FALSE, ARRAY['WiFi', 'TV', 'Heating']
);

INSERT INTO booking (check_in_date, check_out_date, status, user_id, accommodation_id)
VALUES (CURRENT_DATE + 1, CURRENT_DATE + 3, 'PENDING', 1, 1);
