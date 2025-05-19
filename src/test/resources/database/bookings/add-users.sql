INSERT INTO roles (role_name) VALUES ('USER')
ON CONFLICT (role_name) DO NOTHING;

INSERT INTO roles (role_name) VALUES ('ADMIN')
ON CONFLICT (role_name) DO NOTHING;

-- Then insert users, assuming role_ids are correct
INSERT INTO users (id, email, first_name, last_name, password, role_id, is_deleted)
VALUES
  (1, 'user1@example.com', 'Alice', 'Anderson', 'password1', 1, FALSE),
  (2, 'user2@example.com', 'Bob', 'Brown', 'password2', 1, FALSE);
