package edu.ualberta.med.biobank;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class BiobankSchemaExport {

    public static void main(String[] args) {
        Configuration config = new Configuration().configure();

        config.setProperty("hibernate.dialect",
            "org.hibernate.dialect.MySQL5InnoDBDialect");
        config.setProperty("hibernate.connection.driver_class",
            "com.mysql.jdbc.Driver");
        // config.setProperty("hibernate.connection.url",
        // "jdbc:mysql://localhost:3306/biobank");
        // config.setProperty("hibernate.connection.username", "dummy");
        // config.setProperty("hibernate.connection.password", "ozzy498");
        config.setProperty("hibernate.show_sql", "true");

        SchemaExport schemaExport = new EnversSchemaGenerator(config).export();

        /** Just dump the schema SQLs to the console , but not execute them ***/
        schemaExport.setOutputFile("schema.sql");
        schemaExport.create(true, false);
    }
}
