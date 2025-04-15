CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE IF NOT EXISTS reports (
    id UUID DEFAULT uuid_generate_v4(),
    trip_id UUID NOT NULL,
    author_id UUID NOT NULL,
    departure_from VARCHAR(255),
    arrival_to VARCHAR(255),
    start_date TIMESTAMP WITH TIME ZONE,
    end_date TIMESTAMP WITH TIME ZONE,
    participants_amount INTEGER NOT NULL,
    sum_expenses DECIMAL(12, 2),
    avg_expenses DECIMAL(10, 2),
    forward_vehicle VARCHAR(100),
    back_vehicle VARCHAR(100),
    planned_budget TEXT,
    total_budget TEXT,
    equipment_taken TEXT,
    notes TEXT,
    PRIMARY KEY (id)
);