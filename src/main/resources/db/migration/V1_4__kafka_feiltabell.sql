CREATE TABLE FEILET_KAFKA_MELDING (
              ID                      SERIAL PRIMARY KEY NOT NULL,
              TOPIC_NAME              VARCHAR(200) NOT NULL,
              MESSAGE_KEY             VARCHAR(50) NOT NULL,
              JSON_PAYLOAD            text NOT NULL
);
