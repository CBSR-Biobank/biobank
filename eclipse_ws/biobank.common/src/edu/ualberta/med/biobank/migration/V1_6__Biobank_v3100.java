package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

public class V1_6__Biobank_v3100 implements SpringJdbcMigration {

    @SuppressWarnings("nls")
    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // add another column to specimen advanced reports
        jdbcTemplate.execute(
            "INSERT INTO entity_property (id,property,property_type_id,entity_id,version)"
                + "VALUES (33,'topSpecimen.comments.message',1,1,0)");
        jdbcTemplate.execute(
            "INSERT INTO entity_column (id,name,entity_property_id,version)"
                + "VALUES (32,'Source Specimen Comment',33,0)");

        // add another column to specimen advanced reports
        jdbcTemplate.execute(
            "INSERT INTO entity_property (id,property,property_type_id,entity_id,version)"
                + "VALUES (34,'topSpecimen.specimenType.nameShort',1,1,0)");
        jdbcTemplate.execute(
            "INSERT INTO entity_column (id,name,entity_property_id,version)"
                + "VALUES (33,'Source Specimen Type',34,0)");
    }

}
