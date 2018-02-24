INSERT INTO role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_ADMIN');
INSERT INTO role (id, name) VALUES (3, 'ROLE_GROUP_ADMIN');

INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (1, 'admin', '$2a$10$B6wXtY2CJjX32Z/I3CT.xOewUvObk8R4g92AuGX1m.BtrE/hRg7Ii', 'admin@admin.com', '01234567890', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (2, 'rick', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'r.p.cooper1@edu.salford.ac.uk', '07490958538', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (3, 'friend', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'friend@email.com', '074909585382', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (4, 'addFriend', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'friend2@email.com', '074909585322', false);

INSERT INTO user_role(user_id, role_id) VALUES(1, 1);
INSERT INTO user_role(user_id, role_id) VALUES(1, 2);
INSERT INTO user_role(user_id, role_id) VALUES(2, 1);
INSERT INTO user_role(user_id, role_id) VALUES(3, 1);
INSERT INTO user_role(user_id, role_id) VALUES(4, 1);

INSERT INTO friendship(invitee_id, inviter_id, activated) VALUES(1, 2, true);
INSERT INTO friendship(invitee_id, inviter_id, activated) VALUES(1, 3, true);