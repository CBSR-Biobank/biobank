package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_2__Biobank_v360 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("ALTER TABLE container_labeling_scheme ADD COLUMN has_multiple_layout "
            + "TINYINT(1) NULL DEFAULT NULL COMMENT ''");

        jdbcTemplate.execute("UPDATE container_labeling_scheme SET has_multiple_layout=0"
            + " WHERE name IN ('SBS Standard', 'Dewar', 'CBSR SBS')");
        jdbcTemplate.execute("UPDATE container_labeling_scheme SET has_multiple_layout=1"
            + " WHERE name IN ('CBSR 2 char alphabetic', '2 char numeric', '2 char alphabetic')");

        jdbcTemplate.execute("ALTER TABLE container_type ADD COLUMN LABELING_LAYOUT INT(11) "
            + "NOT NULL COMMENT ''");
        jdbcTemplate.execute("UPDATE container_type SET labeling_layout=0");

        // for patient batchOp
        jdbcTemplate.execute("CREATE TABLE `batch_operation_patient` ("
            + "`PROCESSING_EVENT_ID` int(11) NOT NULL,"
            + "  `BATCH_OPERATION_ID` int(11) NOT NULL,"
            + "  PRIMARY KEY (`PROCESSING_EVENT_ID`,`BATCH_OPERATION_ID`),"
            + "  KEY `FK69FFC208D3BA0590` (`BATCH_OPERATION_ID`),"
            + "  CONSTRAINT `FK69FFC208D3BA0590` FOREIGN KEY (`BATCH_OPERATION_ID`) REFERENCES `batch_operation` (`ID`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs");
    }

}
