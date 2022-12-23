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
# For lokal test kjøring kjør MainTest.java
```

## Database

Database parameters med Vault:

```
vault read postgresql/preprod-fss/creds/veilarblest-q1-user
```

Man må sette følgenede environmentvariabler for tilkobling til postgres database:

```
VEILARBLEST_DB_URL=jdbc:postgresql://<HOST>:<PORT>/veilarblest
VEILARBLEST_DB_USER=<USERNAME>
VEILARBLEST_DB_PASSWORD=<PASSWORD>
```

For å sette opp en postgres database lokalt kan man bruke docker.

```sh
docker run --name veilarblest -e POSTGRES_PASSWORD=<PASSWORD> -d -p 5432:5432 postgres
docker ps (finn container id)
docker exec -it <container_id> bash
psql -U postgres
CREATE DATABASE veilarblest;
```

