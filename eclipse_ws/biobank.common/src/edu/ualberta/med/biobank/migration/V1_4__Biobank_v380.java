package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_4__Biobank_v380 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // fix for container table
        jdbcTemplate.execute(
            "ALTER TABLE container change top_container_id `TOP_CONTAINER_ID` int(11) NOT NULL");
    }
}
