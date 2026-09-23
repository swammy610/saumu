-- Optional: Hibernate creates these automatically (ddl-auto=update).
-- Run manually only if you prefer explicit control.

CREATE DATABASE askbot;
\c askbot;

CREATE TABLE IF NOT EXISTS conversations (
    id          BIGSERIAL PRIMARY KEY,
    title       VARCHAR(200) NOT NULL,
    subject     VARCHAR(40)  NOT NULL,
    language    VARCHAR(20)  NOT NULL,
    created_at  TIMESTAMP    NOT NULL
);

CREATE TABLE IF NOT EXISTS messages (
    id              BIGSERIAL PRIMARY KEY,
    role            VARCHAR(10) NOT NULL,
    content         TEXT        NOT NULL,
    conversation_id BIGINT      NOT NULL REFERENCES conversations(id) ON DELETE CASCADE,
    created_at      TIMESTAMP   NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_messages_conversation ON messages(conversation_id);
