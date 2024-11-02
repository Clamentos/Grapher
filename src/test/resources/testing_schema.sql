
---
USE grapher_auth_test;
SET FOREIGN_KEY_CHECKS = 0;

---
DROP SEQUENCE IF EXISTS audit_id_seq;
CREATE SEQUENCE IF NOT EXISTS audit_id_seq START WITH 1 INCREMENT BY 1;

DROP TABLE IF EXISTS audit;
CREATE TABLE IF NOT EXISTS audit (

    id                          BIGINT            PRIMARY KEY DEFAULT (NEXT VALUE FOR audit_id_seq),
    record_id                   BIGINT            NOT NULL,
    table_name                  VARCHAR(32)       NOT NULL,
    columns                     VARCHAR(256)      NOT NULL,
    action                      VARCHAR(16)       NOT NULL,
    created_at                  BIGINT            NOT NULL,
    created_by                  VARCHAR(32)       NOT NULL
);

---..
DROP SEQUENCE IF EXISTS log_id_seq;
CREATE SEQUENCE IF NOT EXISTS log_id_seq START WITH 1 INCREMENT BY 64;

DROP TABLE IF EXISTS log;
CREATE TABLE IF NOT EXISTS log (

    id                          BIGINT            PRIMARY KEY DEFAULT (NEXT VALUE FOR log_id_seq),
    timestamp                   BIGINT            NOT NULL,
    level                       VARCHAR(8)        NOT NULL,
    thread                      VARCHAR(128)      NOT NULL,
    logger                      VARCHAR(128)      NOT NULL,
    message                     TEXT              NOT NULL,
    created_at                  BIGINT            NOT NULL
);

---..
DROP TABLE IF EXISTS session;
CREATE TABLE IF NOT EXISTS session (

    id                          VARCHAR(128)      PRIMARY KEY,
    user_id                     BIGINT            NOT NULL,
    username                    VARCHAR(32)       NOT NULL,
    user_role                   VARCHAR(32)       NOT NULL,
    expires_at                  BIGINT            NOT NULL
);

---..
DROP SEQUENCE IF EXISTS grapher_user_id_seq;
CREATE SEQUENCE grapher_user_id_seq START WITH 1 INCREMENT BY 1;

DROP TABLE IF EXISTS grapher_user;
CREATE TABLE IF NOT EXISTS grapher_user (

    id                          BIGINT            PRIMARY KEY DEFAULT (NEXT VALUE FOR grapher_user_id_seq),
    username                    VARCHAR(32)       NOT NULL UNIQUE,
    password                    VARCHAR(128)      NOT NULL,
    email                       VARCHAR(64)       NOT NULL,
    profile_picture             MEDIUMBLOB            NULL,
    about                       VARCHAR(1024)     NOT NULL,
    preferences                 TEXT              NOT NULL,
    role                        VARCHAR(32)       NOT NULL,
    failed_accesses             SMALLINT          NOT NULL,
    locked_until                BIGINT            NOT NULL,
    lock_reason                 VARCHAR(256)      NOT NULL,
    password_last_changed_at    BIGINT            NOT NULL,
    created_at                  BIGINT            NOT NULL,
    created_by                  VARCHAR(32)       NOT NULL,
    updated_at                  BIGINT            NOT NULL,
    updated_by                  VARCHAR(32)       NOT NULL
);

---..
DROP SEQUENCE IF EXISTS subscription_id_seq;
CREATE SEQUENCE subscription_id_seq START WITH 1 INCREMENT BY 1;

DROP TABLE IF EXISTS subscription;
CREATE TABLE IF NOT EXISTS subscription (

    id                          BIGINT            PRIMARY KEY DEFAULT (NEXT VALUE FOR subscription_id_seq),
    publisher                   BIGINT            NOT NULL,
    subscriber                  BIGINT            NOT NULL,
    notify                      BOOLEAN           NOT NULL,
    created_at                  BIGINT            NOT NULL,
    updated_at                  BIGINT            NOT NULL,

    CONSTRAINT subscription_pub_sub_unique UNIQUE(publisher, subscriber),
    CONSTRAINT publisher_fk FOREIGN KEY(publisher) REFERENCES grapher_user(id) ON DELETE CASCADE,
    CONSTRAINT subscriber_fk FOREIGN KEY(subscriber) REFERENCES grapher_user(id) ON DELETE CASCADE
);

---
SET FOREIGN_KEY_CHECKS = 1;

---
INSERT INTO grapher_user (username,password,email,profile_picture,about,preferences,role,failed_accesses,locked_until,lock_reason,password_last_changed_at,created_at,created_by,updated_at,updated_by) VALUES ('TestAdminUser', '$2a$12$nMn/a2q4Q0RtaddC1/3.2e1rxEQDxvu79sbKF5BywyaCP8XhxiqKy', 'TestAdminUser@nonexistent.com', null, 'Im a testing admin user !', '', 'ADMINISTRATOR', 0, UNIX_TIMESTAMP() * 1000, '', UNIX_TIMESTAMP() * 1000, UNIX_TIMESTAMP() * 1000, 'TestAdminUser', UNIX_TIMESTAMP() * 1000, 'TestAdminUser');

---
