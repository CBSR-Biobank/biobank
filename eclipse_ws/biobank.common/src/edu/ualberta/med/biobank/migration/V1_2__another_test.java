package edu.ualberta.med.biobank.migration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.LinkedHashSet;
import java.util.Set;

import com.googlecode.flyway.core.api.migration.jdbc.JdbcMigration;

//@SuppressWarnings("nls")
//public class V1_2__another_test implements SpringJdbcMigration, MigrationChecksumProvider {
//
//    @Override
//    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
//        //jdbcTemplate.execute("ALTER TABLE origin_info DROP FOREIGN KEY FKE92E7A275598FA35");
//        //jdbcTemplate.execute("ALTER TABLE origin_info DROP INDEX FKE92E7A275598FA35");
//        //jdbcTemplate.execute("ALTER TABLE origin_info ADD INDEX FKE92E7A274D7A8883 (RECEIVER_SITE_ID)");
//        //jdbcTemplate.execute("ALTER TABLE origin_info ADD CONSTRAINT FKE92E7A274D7A8883 FOREIGN KEY FKE92E7A274D7A8883 (RECEIVER_SITE_ID) REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION");
//
//        jdbcTemplate.execute("ALTER TABLE contact MODIFY COLUMN ID INT(11) NOT NULL auto_increment");
//        jdbcTemplate.execute("INSERT INTO contact (name,clinic_id,version) VALUES ('test',1,0)");
//        jdbcTemplate.execute("ALTER TABLE contact MODIFY COLUMN ID INT(11) NOT NULL");
//    }
//
//    @Override
//    public Integer getChecksum() {  
//        return 1234;
//    }
//
//}


@SuppressWarnings("nls")
public class V1_2__another_test implements JdbcMigration {

    @Override
    public void migrate(Connection connection) throws Exception {
        Set<PreparedStatement> statements = new LinkedHashSet<PreparedStatement>();
        statements.add(connection.prepareStatement("ALTER TABLE contact MODIFY COLUMN ID INT(11) NOT NULL auto_increment"));
        statements.add(connection.prepareStatement("INSERT INTO contact (name,clinic_id,version) VALUES ('test',1,0)"));
        statements.add(connection.prepareStatement("ALTER TABLE contact MODIFY COLUMN ID INT(11) NOT NULL"));

        for (PreparedStatement statement : statements) {
            try {
                statement.execute();
            } finally {
                statement.close();
            }
        }
        
    }
    
}