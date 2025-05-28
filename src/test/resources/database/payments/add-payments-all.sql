INSERT INTO users (id, email, password, first_name, last_name, role, is_deleted)
VALUES
  (1, 'user@example.com', 'lokloked', 'Fan', 'Zhen', 'USER', false),
  (2, 'admin@example.com', 'lokloked', 'Admin', 'User', 'ADMIN', false);

INSERT INTO bookings (id, user_id, status, created_at) VALUES
(101, 1, 'PENDING', NOW()),
(102, 1, 'CONFIRMED', NOW());

INSERT INTO payments (id, session_id, session_url, amount_to_pay, status, booking_id, user_id) VALUES
(1, 'session1', 'https://session1.com', 100.00, 'PENDING', 101, 1),
(2, 'session2', 'https://session2.com', 150.00, 'COMPLETED', 102, 1);
