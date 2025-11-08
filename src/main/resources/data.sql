DELETE FROM users;

INSERT INTO users (email, password, name, role, active)
VALUES
('admin@example.com',
 '$2a$10$9l7ZMYWvMqU9xj0lfIxhQOW.YGpxHcb0hEw2FvSHuK4aGyB61UhPq',
 'Admin User', 'ADMIN', TRUE),
('user@example.com',
 '$2a$10$U2Z5pYFfJ7vFZ3.oy8EVh.bE1NfYfJ9DQdmj2x0db4Y/YtbM5xROe',
 'Normal User', 'USER', TRUE);
