ALTER TABLE reservations
    ADD COLUMN IF NOT EXISTS cancel_reason VARCHAR(255) NULL;

CREATE TABLE IF NOT EXISTS reservation_audit_events (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    reservation_id BIGINT NOT NULL REFERENCES reservations (id),
    event_type VARCHAR(50) NOT NULL,
    message VARCHAR(255) NOT NULL,
    actor_user_id VARCHAR(64) NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_reservation_audit_tenant_reservation
    ON reservation_audit_events (tenant_id, reservation_id);
