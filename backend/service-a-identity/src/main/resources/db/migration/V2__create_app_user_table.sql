-- Create APP_USER table
CREATE TABLE app_user (
    id BIGSERIAL PRIMARY KEY,
    username CITEXT NOT NULL UNIQUE,
    password_hash TEXT NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create index on username for faster lookups (UNIQUE constraint already creates index)
CREATE INDEX idx_app_user_username ON app_user(username);

-- Add comment
COMMENT ON TABLE app_user IS 'User accounts for Service A - Identity & Portal';
COMMENT ON COLUMN app_user.username IS 'Case-insensitive unique username';
COMMENT ON COLUMN app_user.password_hash IS 'Hashed password using Argon2id or BCrypt';

