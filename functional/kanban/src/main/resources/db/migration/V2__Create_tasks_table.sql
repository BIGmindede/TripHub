CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS tasks (
    id UUID DEFAULT uuid_generate_v4(),
    kanban_id UUID REFERENCES kanban(id) ON DELETE CASCADE,
    author_id UUID NOT NULL,
    implementer_id UUID,
    status_id INT DEFAULT 0,
    created_at DATE DEFAULT CURRENT_TIMESTAMP,
    target_date DATE,
    description TEXT,
    name TEXT,
    PRIMARY KEY (id)
);

CREATE INDEX IF NOT EXISTS idx_tasks_kanban_id ON tasks(kanban_id);
CREATE INDEX IF NOT EXISTS idx_tasks_status ON tasks(status_id);
CREATE INDEX IF NOT EXISTS idx_tasks_implementer_id ON tasks(implementer_id);
CREATE INDEX IF NOT EXISTS idx_tasks_target_date ON tasks(target_date);