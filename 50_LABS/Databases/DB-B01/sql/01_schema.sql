DROP TABLE IF EXISTS operation_event;
DROP TABLE IF EXISTS client;

CREATE TABLE client (
    id          bigint PRIMARY KEY,
    country     text NOT NULL,
    city        text NOT NULL,
    segment     text NOT NULL,
    created_at  timestamptz NOT NULL
);

CREATE TABLE operation_event (
    id          bigint PRIMARY KEY,
    tenant_id   integer NOT NULL,
    client_id   bigint NOT NULL REFERENCES client(id),
    status      text NOT NULL,
    category    text NOT NULL,
    amount      numeric(14,2) NOT NULL,
    created_at  timestamptz NOT NULL,
    processed_at timestamptz,
    payload     jsonb NOT NULL DEFAULT '{}'::jsonb
);

CREATE INDEX operation_event_client_idx
    ON operation_event(client_id);

CREATE INDEX operation_event_status_idx
    ON operation_event(status);

CREATE INDEX client_country_idx
    ON client(country);
