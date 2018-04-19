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
                + "VALUES ((SELECT MAX(ID)+1 from entity_property as ent),'topSpecimen.comments.message',1,1,0)");
        jdbcTemplate.execute(
            "INSERT INTO entity_column (id,name,entity_property_id,version)"
                + "VALUES ((SELECT MAX(ID)+1 from entity_column as col),'Source Specimen Comment',33,0)");

        // add another column to specimen advanced reports
        jdbcTemplate.execute(
            "INSERT INTO entity_property (id,property,property_type_id,entity_id,version)"
                + "VALUES ((SELECT MAX(ID)+1 from entity_property as ent),'topSpecimen.specimenType.nameShort',1,1,0)");
        jdbcTemplate.execute(
            "INSERT INTO entity_column (id,name,entity_property_id,version)"
                + "VALUES ((SELECT MAX(ID)+1 from entity_column as col),'Source Specimen Type',34,0)");
    }

}