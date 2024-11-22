package no.nav.veilarblest;

import org.jooq.codegen.GenerationTool;
import org.jooq.meta.extensions.ddl.DDLDatabase;
import org.jooq.meta.jaxb.*;
import org.jooq.meta.postgres.PostgresDatabase;
import org.postgresql.Driver;

import static no.nav.common.utils.EnvironmentUtils.getRequiredProperty;

public class JooqGenerator {
    public static final String VEILARBLEST_DB_URL_PROPERTY = "VEILARBLEST_DB_URL";
    public static final String VEILARBLEST_DB_USER_PROPERTY = "VEILARBLEST_DB_USER";
    public static final String VEILARBLEST_DB_PASSWORD_PROPERTY = "VEILARBLEST_DB_PASSWORD";

    public static void main(String[] args) throws Exception {
        String url = getRequiredProperty(VEILARBLEST_DB_URL_PROPERTY);
        String user = getRequiredProperty(VEILARBLEST_DB_USER_PROPERTY);
        String passowrd = getRequiredProperty(VEILARBLEST_DB_PASSWORD_PROPERTY);

        Configuration configuration = new Configuration()
                .withJdbc(new Jdbc()
                        .withDriver(Driver.class.getCanonicalName())
                        .withUrl(url)
                        .withUser(user)
                        .withPassword(passowrd))
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName(PostgresDatabase.class.getName())
                                .withIncludes(".*")
                                .withExcludes("SCHEMA_VERSION|FLYWAY_SCHEMA_HISTORY")
                                .withInputSchema("public"))
                        .withTarget(new Target()
                                .withPackageName("no.nav.veilarblest.domain")
                                .withDirectory("src/main/java/")
                                .withClean(true))
                        .withGenerate(new Generate()
                                .withJavaTimeTypes(true)));

        GenerationTool.generate(configuration);
    }

    /*
     * This is an alternate way of generating the jOOQ classes from the migration scripts.
     * My experience is that the generated classes are better when generated from a running
     * Postgres instance.
     */
    @SuppressWarnings("unused")
    private static Configuration scriptConfig() {
        return new Configuration()
                .withGenerator(new Generator()
                        .withDatabase(new Database()
                                .withName(DDLDatabase.class.getName())
                                .withProperties(
                                        new Property()
                                                .withKey("scripts")
                                                .withValue("src/main/resources/db/migration/**"),
                                        new Property()
                                                .withKey("sort")
                                                .withValue("sematic"))
                                .withInputSchema("public"))
                        .withTarget(new Target()
                                .withPackageName("no.nav.veilarblest.domain")
                                .withDirectory("src/main/java/")
                                .withClean(true))
                        .withGenerate(new Generate()
                                .withJavaTimeTypes(true)));
    }

}
