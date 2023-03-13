# Veilarblest

Backendapplikasjon for aktivitetsplan. Tilbyr REST tjenester for å markere ressurser som lest, samt logg over når
ressurser sist ble lest.

## Kjøre appen

```console
# bygge
mvn clean install 

# test
mvn test

# starte
# Kjør main-metoden i no.nav.veilarblest.VeilarblestApp.java
```

## Database

Database parameters med Vault:

```
vault read postgresql/preprod-fss/creds/veilarblest-q1-user
```

Postgres database:

```
Postgres database startes opp i docker (jdbc:tc:postgresql:15.2:///veilarblest) via testcontainerts
```

