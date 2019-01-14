DELETE FROM lest;

CREATE TABLE mine_ressurser
(
  id        SERIAL PRIMARY KEY NOT NULL,
  eier      VARCHAR            NOT NULL,
  ressurs   ressurs            NOT NULL,
  tidspunkt TIMESTAMP          NOT NULL,
  verdi     VARCHAR
);

ALTER TABLE lest RENAME TO andres_ressurser;
ALTER TABLE andres_ressurser RENAME COLUMN av to lest_av;
ALTER TABLE andres_ressurser ADD CONSTRAINT andres_unike_ressurser_lest UNIQUE (lest_av, eier, ressurs);
ALTER TABLE mine_ressurser ADD CONSTRAINT mine_unike_ressurser_lest UNIQUE (eier, ressurs);

