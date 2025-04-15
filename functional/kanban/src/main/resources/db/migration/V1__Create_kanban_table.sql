CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS kanban (
    id UUID DEFAULT uuid_generate_v4(),
    trip_id UUID NOT NULL, 
    statuses TEXT[] NOT NULL,
    PRIMARY KEY (id)
);