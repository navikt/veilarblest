CREATE SEQUENCE FEILET_KAFKA_MELDING_SEQ START WITH 1;

CREATE TABLE FEILET_KAFKA_MELDING (
                                      ID                      NUMBER(10) DEFAULT FEILET_KAFKA_MELDING_SEQ.nextval NOT NULL,
                                      TOPIC_NAME              VARCHAR(200) NOT NULL,
                                      MESSAGE_KEY             VARCHAR(50) NOT NULL,
                                      JSON_PAYLOAD            CLOB NOT NULL
);