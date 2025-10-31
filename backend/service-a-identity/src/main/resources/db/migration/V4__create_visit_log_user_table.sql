-- Create VISIT_LOG_USER table
CREATE TABLE visit_log_user (
    id BIGSERIAL PRIMARY KEY,
    owner_id BIGINT NOT NULL,
    visitor_anon_id TEXT,
    ip_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_visit_log_user_owner FOREIGN KEY (owner_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_visit_log_user_owner_id ON visit_log_user(owner_id);
CREATE INDEX idx_visit_log_user_created_at ON visit_log_user(owner_id, created_at DESC);
CREATE INDEX idx_visit_log_user_created_at_owner ON visit_log_user(owner_id, created_at DESC);

-- Add comment
COMMENT ON TABLE visit_log_user IS 'Profile visit logs for dashboard analytics';
COMMENT ON COLUMN visit_log_user.visitor_anon_id IS 'Anonymous visitor identifier';
COMMENT ON COLUMN visit_log_user.ip_hash IS 'Hashed IP address with pepper';

