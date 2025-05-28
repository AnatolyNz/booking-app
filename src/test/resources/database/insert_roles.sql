INSERT INTO roles (id, role_name)
SELECT 1, 'USER'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 1);

INSERT INTO roles (id, role_name)
SELECT 2, 'ADMIN'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE id = 2);
