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
    instant_audit_id    BIGINT NOT NULL,

    CONSTRAINT instant_audit_id_fk FOREIGN KEY(instant_audit_id) REFERENCES INSTANT_AUDIT(id) ON DELETE RESTRICT
);

---..
CREATE TABLE OPERATION (

    id                  SMALLSERIAL PRIMARY KEY,
    name                TEXT NOT NULL UNIQUE,
    instant_audit_id    BIGINT NOT NULL,

    CONSTRAINT instant_audit_id_fk FOREIGN KEY(instant_audit_id) REFERENCES INSTANT_AUDIT(id) ON DELETE RESTRICT
);

---..
CREATE TABLE USER_OPERATION (

    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT NOT NULL,
    operation_id        SMALLINT NOT NULL,

    CONSTRAINT user_id_fk FOREIGN KEY(user_id) REFERENCES GRAPHER_USER(id) ON DELETE CASCADE,
    CONSTRAINT operation_id_fk FOREIGN KEY(operation_id) REFERENCES OPERATION(id) ON DELETE CASCADE
);

---
INSERT INTO INSTANT_AUDIT (id, created_at, updated_at, created_by, updated_by)
VALUES (
    1,
    EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000,
    EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000,
    'TestingUser',
    'TestingUser'
);

-- password is: password123         with BCrypt 12 rounds
INSERT INTO GRAPHER_USER (id, username, password, email, flags, instant_audit_id)
VALUES (1, 'TestingUser', '$2a$12$rtM/MXw4mWhqQDjYsssNOuwNtTmdc28mvhsPHE7k6a/cPy93fHBte', 'testinguser@nonexistent.com', 0, 1);

---
COMMIT;

---