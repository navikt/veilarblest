FROM docker.pkg.github.com/navikt/pus-nais-java-app/pus-nais-java-app:java11
COPY /target/veilarblest.jar app.jar
