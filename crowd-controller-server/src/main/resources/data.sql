INSERT INTO roles (id, name) VALUES (1, 'STANDARD_USER');
INSERT INTO roles (id, name) VALUES (2, 'ADMIN_USER');

INSERT INTO users (id, username, password, email, mobile_number) VALUES (1, 'rick', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'r.p.cooper1@edu.salford.ac.uk', '07490958538');
INSERT INTO users (id, username, password, email, mobile_number) VALUES (2, 'admin', '$2a$10$B6wXtY2CJjX32Z/I3CT.xOewUvObk8R4g92AuGX1m.BtrE/hRg7Ii', 'admin@admin.com', '01234567890');

INSERT INTO user_role(user_id, role_id) VALUES(1,1);
INSERT INTO user_role(user_id, role_id) VALUES(2,1);
INSERT INTO user_role(user_id, role_id) VALUES(2,2);