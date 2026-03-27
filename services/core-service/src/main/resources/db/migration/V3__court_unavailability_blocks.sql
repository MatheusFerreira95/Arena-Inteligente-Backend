CREATE TABLE IF NOT EXISTS court_unavailability_blocks (
    id BIGSERIAL PRIMARY KEY,
    tenant_id VARCHAR(64) NOT NULL,
    court_id BIGINT NOT NULL REFERENCES courts (id),
    start_at TIMESTAMP NOT NULL,
    end_at TIMESTAMP NOT NULL,
    reason VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_unavailability_tenant_court_start_end
    ON court_unavailability_blocks (tenant_id, court_id, start_at, end_at);
