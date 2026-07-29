/************************
 * Made by [MR Ferry™]  *
 * on Juli 2026         *
 ************************/

INSERT INTO user_session_types (id, name) VALUES (1, 'STAFF');
INSERT INTO user_session_types (id, name) VALUES (2, 'CUSTOMER');

INSERT INTO staff_roles (id, name) VALUES (1, 'SUPER_STAFF');
INSERT INTO staff_roles (id, name) VALUES (2, 'STAFF');

INSERT INTO email_trigger_types (id, name) VALUES (1, 'TENANT_REGISTRATION');
INSERT INTO email_trigger_types (id, name) VALUES (2, 'FORGOTTEN_PASSWORD');

INSERT INTO email_trigger_statuses (id, name) VALUES (1, 'CREATED');
INSERT INTO email_trigger_statuses (id, name) VALUES (2, 'PUBLISHED');
