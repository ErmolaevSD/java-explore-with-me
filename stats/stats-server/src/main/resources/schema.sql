DROP TABLE IF EXISTS stats;

CREATE TABLE IF NOT EXISTS stats (
    id BIGINT GENERATED ALWAYS AS identity PRIMARY KEY,
    app varchar(40) NOT NULL,
    uri varchar(40) NOT NULL,
    ip varchar(40) NOT NULL,
    createdAt varchar(40) NOT NULL
);