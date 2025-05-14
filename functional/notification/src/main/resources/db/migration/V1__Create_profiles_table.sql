CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE if not exists notifications (
    id UUID DEFAULT uuid_generate_v4(),
    profile_id UUID NOT NULL,
    sender_tag VARCHAR,
    sent_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    content TEXT NOT NULL,
    notification_status VARCHAR NOT NULL,
    actions TEXT,
    notification_type TEXT NOT NULL,
    PRIMARY KEY (id)
);

CREATE INDEX if not exists idx_notifications_profile_id ON notifications (profile_id);