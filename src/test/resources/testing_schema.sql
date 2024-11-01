BEGIN TRANSACTION;

---
DROP SCHEMA IF EXISTS "testing" CASCADE;
CREATE SCHEMA IF NOT EXISTS "testing";

---
CREATE TABLE AUDIT (

    id                          BIGSERIAL PRIMARY KEY,
    record_id                   BIGINT NOT NULL,
    table_name                  TEXT NOT NULL,
    columns                     TEXT NOT NULL,
    action                      TEXT NOT NULL,
    created_at                  BIGINT NOT NULL,
    created_by                  TEXT NOT NULL
);

---..
CREATE TABLE LOG (

    id                          BIGSERIAL PRIMARY KEY,
    timestamp                   BIGINT NOT NULL,
    level                       TEXT NOT NULL,
    thread                      TEXT NOT NULL,
    logger                      TEXT NOT NULL,
    message                     TEXT NOT NULL,
    created_at                  BIGINT NOT NULL
);

---..
CREATE TABLE SESSION (

    id                          TEXT PRIMARY KEY,
    user_id                     BIGINT NOT NULL,
    username                    TEXT NOT NULL,
    user_role                   TEXT NOT NULL,
    expires_at                  BIGINT NOT NULL
);

---..
CREATE TABLE GRAPHER_USER (

    id                          BIGSERIAL PRIMARY KEY,
    username                    TEXT NOT NULL UNIQUE,
    password                    TEXT NOT NULL,
    email                       TEXT NOT NULL,
    profile_picture             BYTEA NULL,
    about                       TEXT NOT NULL,
    preferences                 TEXT NOT NULL,
    role                        TEXT NOT NULL,
    failed_accesses             SMALLINT NOT NULL,
    locked_until                BIGINT NOT NULL,
    lock_reason                 TEXT NOT NULL,
    password_last_changed_at    BIGINT NOT NULL,
    created_at                  BIGINT NOT NULL,
    created_by                  TEXT NOT NULL,
    updated_at                  BIGINT NOT NULL,
    updated_by                  TEXT NOT NULL
);

---..
CREATE TABLE SUBSCRIPTION (

    id                          BIGSERIAL PRIMARY KEY,
    publisher                   BIGINT NOT NULL,
    subscriber                  BIGINT NOT NULL,
    notify                      BOOLEAN NOT NULL,
    created_at                  BIGINT NOT NULL,
    updated_at                  BIGINT NOT NULL,

    CONSTRAINT subscription_pub_sub_unique UNIQUE(publisher, subscriber),
    CONSTRAINT publisher_fk FOREIGN KEY(publisher) REFERENCES GRAPHER_USER(id) ON DELETE CASCADE,
    CONSTRAINT subscriber_fk FOREIGN KEY(subscriber) REFERENCES GRAPHER_USER(id) ON DELETE CASCADE
);

---
    INSERT INTO GRAPHER_USER (username,password,email,profile_picture,about,preferences,role,failed_accesses,locked_until,lock_reason,password_last_changed_at,created_at,created_by,updated_at,updated_by) VALUES ('TestAdminUser', '$2a$12$nMn/a2q4Q0RtaddC1/3.2e1rxEQDxvu79sbKF5BywyaCP8XhxiqKy', 'TestAdminUser@nonexistent.com', null, 'Im a testing admin user !', '', 'ADMINISTRATOR', 0, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, '', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestAdminUser', EXTRACT(EPOCH FROM CURRENT_TIMESTAMP) * 1000, 'TestAdminUser');

---
COMMIT;

---
