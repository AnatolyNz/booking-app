CREATE TABLE bookings (
    id BIGINT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    status VARCHAR(50),
    created_at TIMESTAMP
);
