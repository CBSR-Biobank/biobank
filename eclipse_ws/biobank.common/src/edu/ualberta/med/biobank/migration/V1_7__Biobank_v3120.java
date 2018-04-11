package edu.ualberta.med.biobank.migration;

import org.springframework.jdbc.core.JdbcTemplate;

import com.googlecode.flyway.core.api.migration.spring.SpringJdbcMigration;

@SuppressWarnings("nls")
public class V1_7__Biobank_v3120 implements SpringJdbcMigration {

    @Override
    public void migrate(JdbcTemplate jdbcTemplate) throws Exception {

        // to make many to many relation between researchgroup and study

	jdbcTemplate.execute(" CREATE TABLE `researchgroup_study` ("
		  +" `STUDY_ID` int(11) NOT NULL,"
		  +" `SITE_ID` int(11) NOT NULL, "
		  +" PRIMARY KEY (`SITE_ID`,`STUDY_ID`),"
		  +" KEY `FK_Study_Research` (`STUDY_ID`),"
		  +" KEY `FK_Site_Research` (`SITE_ID`),"
		  +" CONSTRAINT `FK_Site_Research` FOREIGN KEY (`SITE_ID`) REFERENCES `center` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION,"
		  +" CONSTRAINT `FK_Study_Research` FOREIGN KEY (`STUDY_ID`) REFERENCES `study` (`ID`) ON DELETE NO ACTION ON UPDATE NO ACTION"
		  +") ENGINE=InnoDB;");
    }
}
