package edu.ualberta.med.biobank;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.EnversSchemaGenerator;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class OutputSchema {
    public static void main(String[] args) {
        Configuration config = new Configuration().configure();
        SchemaExport schemaExport = new EnversSchemaGenerator(config).export();
        schemaExport.drop(true, false);
        schemaExport.create(true, false);
    }
}
