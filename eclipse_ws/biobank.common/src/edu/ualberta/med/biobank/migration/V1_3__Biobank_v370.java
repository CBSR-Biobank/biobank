package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_3__Biobank_v370 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // for collection event attributes batchOp
        jdbcTemplate.execute("CREATE TABLE `batch_operation_event_attr` ("
            + " `EVENT_ATTR_ID` int(11) NOT NULL,"
            + " `BATCH_OPERATION_ID` int(11) NOT NULL,"
            + " PRIMARY KEY (EVENT_ATTR_ID, BATCH_OPERATION_ID),"
            + " KEY `FKF1184A93D3BA0590` (`BATCH_OPERATION_ID`),"
            + " CONSTRAINT `FKF1184A93D3BA0590` FOREIGN KEY (`BATCH_OPERATION_ID`) "
            + " REFERENCES `batch_operation` (`ID`)"
            + ") ENGINE=InnoDB");
    }
}