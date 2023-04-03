DROP TABLE IF EXISTS users, categories, events, requests, compilations, comp_event;

CREATE TABLE IF NOT EXISTS users
(
    id    BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name  VARCHAR(255) NOT NULL,
    email varchar(255) NOT NULL,
    CONSTRAINT UQ_USER_EMAIL UNIQUE (email),
    CONSTRAINT email_at CHECK (email LIKE '%@%'),
    CONSTRAINT space_name_user CHECK (name NOT LIKE ' ' and name NOT LIKE '')
);

CREATE TABLE IF NOT EXISTS categories
(
    id   BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    CONSTRAINT UQ_CATEGORY_NAME UNIQUE (name),
    CONSTRAINT space_name_categories CHECK (name NOT LIKE ' ' and name NOT LIKE '')
);

CREATE TABLE IF NOT EXISTS events
(
    id                 BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    annotation         VARCHAR(1000)               NOT NULL,
    category_id        BIGINT                      NOT NULL REFERENCES categories (id) ON DELETE CASCADE,
    created_on         TIMESTAMP WITHOUT TIME ZONE,
    description        VARCHAR(1000),
    event_date         TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    initiator_id       BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    lat                float                       NOT NULL,
    lon                float                       NOT NULL,
    paid               boolean                     NOT NULL,
    participant_limit  INTEGER,
    published_on       TIMESTAMP WITHOUT TIME ZONE,
    request_moderation BOOL                        NOT NULL,
    state              VARCHAR,
    title              VARCHAR(100)                NOT NULL,
    comment_moderation BOOL,
    comment_close      BOOL
);

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id     BIGINT                      NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    requester_id BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    status       VARCHAR(100),
    UNIQUE (event_id, requester_id)
);

CREATE TABLE IF NOT EXISTS compilations
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    events_id BIGINT REFERENCES events (id) ON DELETE CASCADE,
    pinned    BOOL         NOT NULL,
    title     VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS comments
(
    id        BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    created   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    event_id  BIGINT                      NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    author_id BIGINT                      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    text      VARCHAR(500),
    status    VARCHAR
);

CREATE TABLE IF NOT EXISTS comp_event
(
    event_id BIGINT REFERENCES events (id) ON DELETE CASCADE,
    comp_id  BIGINT REFERENCES compilations (id) ON DELETE CASCADE,
    PRIMARY KEY (event_id, comp_id)
);
