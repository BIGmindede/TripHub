ALTER TABLE participations
    DROP CONSTRAINT IF EXISTS participations_trip_id_fkey,
    ADD CONSTRAINT participations_trip_id_fkey
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE;