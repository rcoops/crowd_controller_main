INSERT INTO role (id, name) VALUES (1, 'ROLE_USER');
INSERT INTO role (id, name) VALUES (2, 'ROLE_ADMIN');
INSERT INTO role (id, name) VALUES (3, 'ROLE_GROUP_ADMIN');

INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (1, 'admin', '$2a$10$B6wXtY2CJjX32Z/I3CT.xOewUvObk8R4g92AuGX1m.BtrE/hRg7Ii', 'admin@admin.com', '01234567890', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (2, 'rick', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'r.p.cooper1@edu.salford.ac.uk', '07490958538', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (3, 'geoff', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'friend@email.com', '074909585382', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (4, 'tim', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'tim@email.com', '074909585322', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (5, 'steve', '$2a$10$myX7A77Ae3mrH3MktKxEM.FZ.RTvQoLGEhszmP4GVnJymRoNQip7q', 'steve@email.com', '074909585323', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (6, 'tom', '$2a$10$nQnCIYKc.g8x97kTZUOJhupT6kvi2TbAK.woHujEyK9bEEfqcSOR6', 'tom@email.com', '074909585324', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (7, 'dick', '$2a$10$AfCS75.ums0P1im9a1eULeni/hJdaYbOgs8Pcn8gUJFGvUnkDtNmu', 'dick@email.com', '074909585325', false);
INSERT INTO user (id, username, password, email, mobile_number, group_accepted) VALUES (8, 'harry', '$2a$10$JZGcdtfUMzZ//htHu.bYzOhyD8Ed9s6IPDKUeMc2IYIA6Z9FJc9FC', 'harry@email.com', '074909585326', false);

INSERT INTO user_role(user_id, role_id) VALUES(1, 1);
INSERT INTO user_role(user_id, role_id) VALUES(1, 2);
INSERT INTO user_role(user_id, role_id) VALUES(2, 1);
INSERT INTO user_role(user_id, role_id) VALUES(3, 1);
INSERT INTO user_role(user_id, role_id) VALUES(4, 1);
INSERT INTO user_role(user_id, role_id) VALUES(5, 1);
INSERT INTO user_role(user_id, role_id) VALUES(6, 1);
INSERT INTO user_role(user_id, role_id) VALUES(7, 1);
INSERT INTO user_role(user_id, role_id) VALUES(8, 1);

INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(1, 2, true);
INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(3, 1, false);
INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(4, 1, false);
INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(1, 5, false);
INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(2, 6, true);
INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(2, 7, true);
INSERT INTO friendship(inviter_id, invitee_id, activated) VALUES(2, 8, true);
