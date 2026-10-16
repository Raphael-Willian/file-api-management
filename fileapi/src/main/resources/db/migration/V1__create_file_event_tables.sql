CREATE TABLE IF NOT EXISTS store_files (

    id UUID PRIMARY KEY,
    original_filename VARCHAR(255) NOT NULL,
    storage_key VARCHAR(512) NOT NULL UNIQUE,
    content_type VARCHAR(255) NOT NULL,
    size_bytes BIGINT NOT NULL CHECK (size_bytes > 0)
    sha256 CHAR(64) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP

    CONSTRAINT ck_stored_file_sha256
            CHECK (sha256 ~ '^[a-f0-9]{64}$')

);

CREATE TABLE IF NOT EXISTS file_delivery (
    id UUID PRIMARY KEY,
    file_id UUID NOT NULL,
    channel VARCHAR(20) NOT NULL,
    recipient VARCHAR(320) NOT NULL,
    subject VARCHAR(200),
    message TEXT NOT NULL DEFAULT '',
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    attempt_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_file_delivery_file
        FOREIGN KEY (file_id)
        REFERENCES stored_file(id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_file_delivery_channel
        CHECK (channel IN ('EMAIL', 'WHATSAPP')),

    CONSTRAINT ck_file_delivery_status
        CHECK (
            status IN (
                'PENDING',
                'PROCESSING',
                'SENT',
                'FAILED'
            )
        ),

    CONSTRAINT ck_file_delivery_attempt_count
        CHECK (attempt_count >= 0),

    CONSTRAINT ck_file_delivery_email_subject
        CHECK (
            channel <> 'EMAIL'
            OR (
                subject IS NOT NULL
                AND BTRIM(subject) <> ''
            )
        )
);

CREATE INDEX idx_file_delivery_file_id
    ON file_delivery(file_id);

CREATE INDEX idx_file_delivery_status_created_at
    ON file_delivery(status, created_at);

CREATE TABLE outbox_event (
    id UUID PRIMARY KEY,
    event_type VARCHAR(150) NOT NULL,
    aggregate_id UUID NOT NULL,
    payload JSONB NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    published_at TIMESTAMPTZ,
    attempt_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,

    CONSTRAINT fk_outbox_event_delivery
        FOREIGN KEY (aggregate_id)
        REFERENCES file_delivery(id)
        ON DELETE RESTRICT,

    CONSTRAINT ck_outbox_event_attempt_count
        CHECK (attempt_count >= 0)
);

CREATE INDEX idx_outbox_event_pending
    ON outbox_event(created_at)
    WHERE published_at IS NULL;