CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists participations (
    id UUID DEFAULT uuid_generate_v4(),
    trip_id UUID NOT NULL,
    profile_id UUID NOT NULL,
    status VARCHAR(10) NOT NULL,
    PRIMARY KEY (id)
    );