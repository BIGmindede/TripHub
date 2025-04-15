CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists profiles (
    id UUID DEFAULT uuid_generate_v4(),
    email VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    country VARCHAR(100),
    birth_date DATE,
    tag_name VARCHAR(36),
    role VARCHAR NOT NULL,
    enabled BOOLEAN NOT NULL,
    PRIMARY KEY (id)
);