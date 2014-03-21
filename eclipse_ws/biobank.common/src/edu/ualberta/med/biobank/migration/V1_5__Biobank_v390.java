package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_5__Biobank_v390 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // fix for advanced reports
        jdbcTemplate.execute("ALTER TABLE report MODIFY COLUMN USER_ID INT(11) NOT NULL");
        jdbcTemplate.execute("ALTER TABLE report ADD INDEX FK8FDF4934B9634A05 (USER_ID)");
        jdbcTemplate.execute("ALTER TABLE report ADD CONSTRAINT FK8FDF4934B9634A05 "
            + "FOREIGN KEY FK8FDF4934B9634A05 (USER_ID) REFERENCES principal (ID) "
            + "ON UPDATE NO ACTION ON DELETE NO ACTION");
    }
}