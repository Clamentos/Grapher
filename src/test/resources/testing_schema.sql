BEGIN TRANSACTION;

---
DROP SCHEMA IF EXISTS "testing" CASCADE;
CREATE SCHEMA IF NOT EXISTS "testing";

---
CREATE TABLE INSTANT_AUDIT (

    id                  BIGSERIAL PRIMARY KEY,
    created_at          BIGINT NOT NULL,
    updated_at          BIGINT NOT NULL,
    created_by          TEXT NOT NULL,
    updated_by          TEXT NOT NULL
);

---..
CREATE TABLE AUDIT (

    id                  BIGSERIAL PRIMARY KEY,
    record_id           BIGINT NOT NULL,
    table_name          TEXT NOT NULL,
    columns             TEXT NOT NULL,
    action              CHAR(1) NOT NULL,
    created_at          BIGINT NOT NULL,
    created_by          TEXT NOT NULL
);

---..
CREATE TABLE BLACKLISTED_TOKEN (

    hash                TEXT PRIMARY KEY,
    expires_at          BIGINT NOT NULL
);

---..
CREATE TABLE GRAPHER_USER (

    id                  BIGINT PRIMARY KEY,
    username            TEXT NOT NULL UNIQUE,
    password            TEXT NOT NULL,
    email               TEXT NOT NULL,
    flags               SMALLINT NOT NULL,
    instant_audit_id    BIGINT NOT NULL UNIQUE,

    CONSTRAINT instant_audit_id_fk FOREIGN KEY(instant_audit_id) REFERENCES INSTANT_AUDIT(id) ON DELETE RESTRICT
);

---..
CREATE TABLE OPERATION (

    id                  SMALLSERIAL PRIMARY KEY,
    name                TEXT NOT NULL UNIQUE,
    instant_audit_id    BIGINT NOT NULL UNIQUE,

    CONSTRAINT instant_audit_id_fk FOREIGN KEY(instant_audit_id) REFERENCES INSTANT_AUDIT(id) ON DELETE RESTRICT
);

---..
CREATE TABLE USER_OPERATION (

    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    operation_id        SMALLINT NOT NULL,

    CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES GRAPHER_USER(id) ON DELETE CASCADE,
    CONSTRAINT operation_id_fk FOREIGN KEY(operation_id) REFERENCES OPERATION(id) ON DELETE CASCADE,
    UNIQUE(user_id, operation_id)
);

---..
CREATE TABLE API_PERMISSION (

    id                  BIGSERIAL PRIMARY KEY,
    path                TEXT NOT NULL,
    is_optional         BOOLEAN NOT NULL,
    instant_audit_id    BIGINT NOT NULL UNIQUE,
    operation_id        SMALLINT NOT NULL,

    CONSTRAINT operation_id_fk FOREIGN KEY(operation_id) REFERENCES OPERATION(id) ON DELETE CASCADE,
    UNIQUE(path, operation_id)
);

---
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (1, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (2, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (3, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (4, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (5, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (6, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (7, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (8, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (9, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (10, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (11, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (12, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (13, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (14, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (15, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (16, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (17, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (18, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (19, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (20, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (21, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (22, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (23, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (24, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (25, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (26, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (27, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (28, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (29, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (30, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (31, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (32, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by) VALUES (33, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestingUser', 'TestingUser');

-- password is: password123         with BCrypt 12 rounds
INSERT INTO GRAPHER_USER (id, username, password, email, flags, instant_audit_id) VALUES (1, 'TestingUser', '$2a$12$rtM/MXw4mWhqQDjYsssNOuwNtTmdc28mvhsPHE7k6a/cPy93fHBte', 'testinguser@nonexistent.com', 0, 1);

INSERT INTO OPERATION (name, instant_audit_id) VALUES ('C-PERMISSION', 1);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('R-PERMISSION', 2);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('U-PERMISSION', 3);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('D-PERMISSION', 4);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('R-STATUS', 5);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('C-OPERATION', 6);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('R-OPERATION', 7);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('U-OPERATION', 8);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('D-OPERATION', 9);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('R-USER-SELF', 10);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('R-USER-OTHER', 11);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('U-USER-SELF', 12);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('U-USER-OTHER', 13);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('D-USER-SELF', 14);
INSERT INTO OPERATION (name, instant_audit_id) VALUES ('D-USER-OTHER', 15);

INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (1, 'POST/v1/grapher/permissions', false, 16, 1);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (2, 'GET/v1/grapher/permissions', false, 17, 2);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (3, 'PATCH/v1/grapher/permissions', false, 18, 3);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (4, 'DELETE/v1/grapher/permissions', false, 19, 4);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (5, 'GET/v1/grapher/auth/observability/status', false, 20, 5);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (6, 'POST/v1/grapher/operation', false, 21, 6);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (7, 'GET/v1/grapher/operation', false, 22, 7);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (8, 'PATCH/v1/grapher/operation', false, 23, 8);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (9, 'DELETE/v1/grapher/operation', false, 24, 9);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (10, 'GET/v1/grapher/user', false, 25, 10);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (11, 'GET/v1/grapher/user', false, 26, 11);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (12, 'GET/v1/grapher/user/{id}', false, 27, 10);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (13, 'GET/v1/grapher/user/{id}', true, 28, 11);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (14, 'PATCH/v1/grapher/user', false, 29, 12);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (15, 'PATCH/v1/grapher/user', true, 30, 13);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (16, 'DELETE/v1/grapher/user/{id}', false, 31, 14);
INSERT INTO API_PERMISSION (id, path, is_optional, instant_audit_id, operation_id) VALUES (17, 'DELETE/v1/grapher/user/{id}', true, 32, 15);

---
COMMIT;

---