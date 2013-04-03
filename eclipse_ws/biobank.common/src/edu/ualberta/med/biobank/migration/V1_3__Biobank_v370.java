package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_3__Biobank_v370 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
        jdbcTemplate.execute("CREATE TABLE `batch_operation_patient` ("
            + "`PROCESSING_EVENT_ID` int(11) NOT NULL,"
            + "  `BATCH_OPERATION_ID` int(11) NOT NULL,"
            + "  PRIMARY KEY (`PROCESSING_EVENT_ID`,`BATCH_OPERATION_ID`),"
            + "  KEY `FK69FFC208D3BA0590` (`BATCH_OPERATION_ID`),"
            + "  CONSTRAINT `FK69FFC208D3BA0590` FOREIGN KEY (`BATCH_OPERATION_ID`) REFERENCES `batch_operation` (`ID`)"
            + ") ENGINE=InnoDB DEFAULT CHARSET=latin1 COLLATE=latin1_general_cs");
    }
}
