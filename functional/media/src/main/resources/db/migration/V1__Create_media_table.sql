CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS media_metadata (
    id UUID DEFAULT uuid_generate_v4(),
    author_id UUID NOT NULL,
    media_url VARCHAR(255) NOT NULL,
    trip_id UUID,
    geodata VARCHAR(255),
    is_opened_for_publish BOOLEAN DEFAULT false,
    created_at TIMESTAMP NOT NULL,
    content_type VARCHAR(100),
    file_size BIGINT
);