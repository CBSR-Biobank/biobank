package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_4__Biobank_v380 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("ALTER TABLE container_type ADD COLUMN IS_MICROPLATE "
            + "TINYINT(1) NULL DEFAULT NULL COMMENT ''");

        jdbcTemplate.execute("UPDATE container_type SET IS_MICROPLATE=0");
    }
}
