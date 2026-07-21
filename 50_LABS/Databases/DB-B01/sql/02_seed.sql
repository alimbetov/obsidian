SELECT setseed(0.4242);

INSERT INTO client (id, country, city, segment, created_at)
SELECT
    gs,
    CASE
        WHEN gs % 10 < 7 THEN 'KZ'
        WHEN gs % 10 < 9 THEN 'DE'
        ELSE 'TR'
    END,
    CASE
        WHEN gs % 10 < 7 THEN
            CASE WHEN gs % 4 = 0 THEN 'Almaty' ELSE 'Astana' END
        WHEN gs % 10 < 9 THEN
            CASE WHEN gs % 2 = 0 THEN 'Berlin' ELSE 'Munich' END
        ELSE 'Istanbul'
    END,
    CASE
        WHEN gs % 20 = 0 THEN 'VIP'
        WHEN gs % 5 = 0 THEN 'SME'
        ELSE 'RETAIL'
    END,
    now() - ((gs % 1500) || ' days')::interval
FROM generate_series(1, 100000) AS gs;

INSERT INTO operation_event (
    id,
    tenant_id,
    client_id,
    status,
    category,
    amount,
    created_at,
    processed_at,
    payload
)
SELECT
    gs,
    1 + (gs % 100),
    1 + (gs % 100000),
    CASE
        WHEN gs % 100 < 80 THEN 'PROCESSED'
        WHEN gs % 100 < 90 THEN 'SUCCESS'
        WHEN gs % 100 < 95 THEN 'PENDING'
        ELSE 'FAILED'
    END,
    CASE
        WHEN gs % 8 = 0 THEN 'TRANSFER'
        WHEN gs % 8 = 1 THEN 'PAYMENT'
        WHEN gs % 8 = 2 THEN 'CASH'
        ELSE 'OTHER'
    END,
    round((10 + random() * 9990)::numeric, 2),
    now() - ((gs % 31536000) || ' seconds')::interval,
    CASE
        WHEN gs % 100 < 90 THEN now() - ((gs % 31536000) || ' seconds')::interval + interval '1 second'
        ELSE NULL
    END,
    jsonb_build_object(
        'source', CASE WHEN gs % 2 = 0 THEN 'MOBILE' ELSE 'WEB' END,
        'sequence', gs
    )
FROM generate_series(1, 1000000) AS gs;

VACUUM (ANALYZE) client;
VACUUM (ANALYZE) operation_event;

SELECT
    status,
    count(*) AS rows,
    round(count(*) * 100.0 / sum(count(*)) OVER (), 2) AS percentage
FROM operation_event
GROUP BY status
ORDER BY rows DESC;
