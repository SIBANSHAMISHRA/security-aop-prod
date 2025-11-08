MERGE INTO users (email, password, name, role, active)
KEY(email)
VALUES 
('admin@example.com', 'adminpass', 'Admin User', 'ADMIN', TRUE),
('user@example.com', 'userpass', 'Normal User', 'USER', TRUE);
