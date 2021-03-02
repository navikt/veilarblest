CREATE TABLE FEILET_KAFKA_MELDING (
              ID                      SERIAL,
              TOPIC_NAME              VARCHAR(200) NOT NULL,
              MESSAGE_KEY             VARCHAR(50) NOT NULL,
              JSON_PAYLOAD            CLOB NOT NULL
);
