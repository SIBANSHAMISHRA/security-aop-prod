INSERT INTO users (email, password, name, role, active)
VALUES
('admin@example.com', 'adminpass', 'Admin User', 'ADMIN', true),
('user@example.com', 'userpass', 'Normal User', 'USER', true)
ON CONFLICT DO NOTHING;
