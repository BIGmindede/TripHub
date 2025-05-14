CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists trips (
    id UUID DEFAULT uuid_generate_v4(),
    status_id INT,
    statuses TEXT[],
    author_id UUID NOT NULL,
    destination VARCHAR(255) NOT NULL,
    start_date DATE,
    end_date DATE,
    thumbnail_url TEXT,
    PRIMARY KEY (id)
);