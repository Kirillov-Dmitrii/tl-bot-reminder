--liquibase formatted sql

-- changeSet dkirillov:1
CREATE TABLE task (
    id SERIAL PRIMARY KEY,
    chat_id BIGINT,
    text VARCHAR,
    data_time TIMESTAMP
)





