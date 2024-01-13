CREATE SEQUENCE anomalies_id_seq START 1;

CREATE TABLE detected_anomalies
(
    id            INTEGER       NOT NULL DEFAULT nextval('anomalies_id_seq'),
    temperature   DECIMAL(5, 2) NOT NULL,
    roomId        VARCHAR(50)   NOT NULL,
    thermometerId VARCHAR(50)   NOT NULL,
    timestamp     TIMESTAMP
);

/* I manually create a PK with a name, so that we can refer to a primary key by name on different environments
   (test, accept, prod), in case we need to alter the primary key. Otherwise, the constraint name is randomly generated
   and cannot be used in migration scripts such as this one, because it will be different on each environment.

   For this simple schema that's probably overkill, but I thought it's nice thing to include.
   */
ALTER TABLE detected_anomalies
    ADD CONSTRAINT
        detected_anomalies_pk PRIMARY KEY (id);