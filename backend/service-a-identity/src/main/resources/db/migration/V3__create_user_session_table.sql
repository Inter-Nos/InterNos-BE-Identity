-- Create USER_SESSION table
CREATE TABLE user_session (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    session_fingerprint TEXT NOT NULL,
    ip_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_user_session_user FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE CASCADE
);

-- Create indexes
CREATE INDEX idx_user_session_user_id ON user_session(user_id);
CREATE INDEX idx_user_session_expires_at ON user_session(expires_at);
CREATE INDEX idx_user_session_fingerprint ON user_session(session_fingerprint);

-- Add comment
COMMENT ON TABLE user_session IS 'Active user sessions stored in Redis but tracked in DB';
COMMENT ON COLUMN user_session.session_fingerprint IS 'Session fingerprint for Redis lookup';
COMMENT ON COLUMN user_session.ip_hash IS 'Hashed IP address with pepper';

