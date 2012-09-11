package edu.ualberta.med.biobank.test.action.tecan;

import org.hibernate.Transaction;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.test.action.TestAction;

public class TestTecanScriptPostProcess extends TestAction {

    private static Logger log = LoggerFactory
        .getLogger(TestTecanScriptPostProcess.class.getName());

    private static final String CSV_NAME = "tecan_output.csv";

    private CbsrTecanCsvRowHelper rowCsvHelper;

    private Transaction tx;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        rowCsvHelper = new CbsrTecanCsvRowHelper(factory.getNameGenerator());

        // add 2 shipments

        tx = session.beginTransaction();
        factory.createSite();
        factory.createClinic();
        factory.createStudy();
    }

}
