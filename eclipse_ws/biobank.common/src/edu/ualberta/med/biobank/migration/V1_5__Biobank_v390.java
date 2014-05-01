package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_5__Biobank_v390 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        // fix advanced reports where it is using user ids from the csm_user table
        jdbcTemplate.execute("UPDATE report rpt"
            + " LEFT JOIN principal user ON user.id=rpt.user_id"
            + " JOIN csm_user cu ON cu.user_id=rpt.user_id"
            + " JOIN principal user2 on CONVERT(user2.login USING utf8)=cu.login_name"
            + " SET rpt.user_id=user2.id"
            + " WHERE user.id IS NULL");

        // fix table so that NULL references are not allowed
        jdbcTemplate.execute("DELETE FROM report_column WHERE report_id IS NULL");

        jdbcTemplate.execute("DELETE FROM report_filter_value WHERE report_filter_id IN"
            + "(SELECT id FROM report_filter WHERE report_id IS NULL)");
        jdbcTemplate.execute("DELETE FROM report_filter WHERE report_id IS NULL");

        jdbcTemplate.execute("ALTER TABLE report_filter_value MODIFY COLUMN REPORT_FILTER_ID INT(11) NOT NULL");
        jdbcTemplate.execute("ALTER TABLE report_filter MODIFY COLUMN REPORT_ID INT(11) NOT NULL");

        jdbcTemplate.execute("ALTER TABLE report MODIFY COLUMN USER_ID INT(11) NOT NULL");
        jdbcTemplate.execute("ALTER TABLE report_column MODIFY COLUMN REPORT_ID INT(11) NOT NULL");
        jdbcTemplate.execute("ALTER TABLE report ADD INDEX FK8FDF4934B9634A05 (USER_ID)");
        jdbcTemplate.execute("ALTER TABLE report ADD CONSTRAINT FK8FDF4934B9634A05 "
            + "FOREIGN KEY FK8FDF4934B9634A05 (USER_ID) REFERENCES principal (ID) "
            + "ON UPDATE NO ACTION ON DELETE NO ACTION");

        // update the database so that it matches with the Hibernate config
        jdbcTemplate.execute("ALTER TABLE container_labeling_scheme CHANGE"
            + " has_multiple_layout HAS_MULTIPLE_LAYOUT TINYINT(1) NULL DEFAULT NULL COMMENT ''");
        jdbcTemplate.execute("ALTER TABLE dna COLLATE=latin1_general_cs");
        jdbcTemplate.execute("ALTER TABLE batch_operation_event_attr COLLATE=latin1_general_cs");

        // add new labeling scheme for OHS
        jdbcTemplate.execute("INSERT INTO container_labeling_scheme "
            + "(ID, NAME, MIN_CHARS, MAX_CHARS, MAX_ROWS, MAX_COLS, MAX_CAPACITY, HAS_MULTIPLE_LAYOUT, VERSION) VALUES "
            + "( 7, 'Box 85 by 2', 2, 3, 85, 2, 170, 0, 0)");
    }
}
