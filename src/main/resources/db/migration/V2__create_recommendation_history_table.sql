CREATE TABLE IF NOT EXISTS recommendation_history (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    title VARCHAR(100) NOT NULL,
    plant_summary_text VARCHAR(255) NOT NULL,
    request_snapshot TEXT NOT NULL,
    result_snapshot TEXT NOT NULL,
    created_at DATETIME(6) NOT NULL
);

CREATE INDEX idx_recommendation_history_user_id_id
    ON recommendation_history (user_id, id DESC);
