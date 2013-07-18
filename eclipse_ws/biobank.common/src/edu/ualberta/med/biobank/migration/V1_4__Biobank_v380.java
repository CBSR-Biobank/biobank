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

        // for nucleic acid specimen fields
        jdbcTemplate.execute("CREATE  TABLE `biobank`.`DNA` ("
		  + " `ID` INT(11) NOT NULL ,"
		  + " `VERSION` INT(11) NOT NULL ,"
		  + " `SPECIMEN_ID` INT(11) NOT NULL ,"
		  + " `CONCENTRATION_ABS` DECIMAL(20,10) NULL DEFAULT NULL ,"
		  + " `CONCENTRATION_FLUOR` DECIMAL(20,10) NULL DEFAULT NULL ,"
		  + " `OD_260_OVER_280` DECIMAL(20,10) NULL DEFAULT NULL ,"
		  + " `OD_260_OVER_230` DECIMAL(20,10) NULL DEFAULT NULL ,"
		  + " `ALIQUOT_YIELD` DECIMAL(20,10) NULL DEFAULT NULL ,"
		  + " PRIMARY KEY (`ID`) ,"
		  + " UNIQUE KEY `SPECIMEN_ID` (`SPECIMEN_ID`),"
		  + " KEY `FK_Dna_Specimen` (`SPECIMEN_ID`),"
		  + " CONSTRAINT `FK_Dna_Specimen` FOREIGN KEY (`SPECIMEN_ID`) REFERENCES `specimen` (`ID`)"
		  + ") ENGINE=InnoDB");
    }
}
