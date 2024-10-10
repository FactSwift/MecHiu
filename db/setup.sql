CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,             -- Unique user ID (auto-increment)
    username VARCHAR(50) NOT NULL,     -- Username for each player
    created_at TIMESTAMP DEFAULT NOW() -- When the user was added
);

CREATE TABLE IF NOT EXISTS scores (
    id SERIAL PRIMARY KEY,             -- Unique score ID (auto-increment)
    user_id INTEGER REFERENCES users(id), -- Link to user table (can be NULL if no users)
    score INTEGER NOT NULL,            -- The score the player achieved
    level INTEGER NOT NULL,            -- Level the player was on when the score was recorded
    recorded_at TIMESTAMP DEFAULT NOW() -- Time the score was recorded
);

CREATE INDEX IF NOT EXISTS idx_user_id ON scores(user_id);
CREATE INDEX IF NOT EXISTS idx_score ON scores(score);