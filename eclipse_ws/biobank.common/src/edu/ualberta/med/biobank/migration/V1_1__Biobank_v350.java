package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_1__Biobank_v350 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("ALTER TABLE origin_info DROP FOREIGN KEY FKE92E7A275598FA35");
        jdbcTemplate.execute("ALTER TABLE origin_info DROP INDEX FKE92E7A275598FA35");
        jdbcTemplate.execute("ALTER TABLE origin_info ADD INDEX FKE92E7A274D7A8883 (RECEIVER_SITE_ID)");
        jdbcTemplate.execute("ALTER TABLE origin_info "
            + "ADD CONSTRAINT FKE92E7A274D7A8883 FOREIGN KEY FKE92E7A274D7A8883 (RECEIVER_SITE_ID) "
            + "REFERENCES center (ID) ON UPDATE NO ACTION ON DELETE NO ACTION");
    }

}