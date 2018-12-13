CREATE TYPE ressurs AS enum (
  'aktivitetsplan'
  );

CREATE TABLE lest
(
  id        SERIAL PRIMARY KEY NOT NULL,
  eier      VARCHAR            NOT NULL,
  av        VARCHAR            NOT NULL,
  ressurs   ressurs            NOT NULL,
  tidspunkt TIMESTAMP          NOT NULL
);
