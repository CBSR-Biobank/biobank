package edu.ualberta.med.biobank.tools.upgradetest;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

@SuppressWarnings("nls")
public class TestUpgradeV131V140 {

    private static final String QUERY_PROPERTIES_FILE_NAME = "query.properties";

    private static final String v131filename = "biobank2_v131.csv";

    private static final String v140filename = "biobank2_v140.csv";

    private Connection connectionV131;

    private Connection connectionV140;

    private TestUpgradeV131V140() throws Exception {
        try {
            Properties queryProps = new Properties();
            InputStream in = Thread.currentThread().getContextClassLoader()
                .getResourceAsStream(QUERY_PROPERTIES_FILE_NAME);
            queryProps.load(in);

            connectionV131 = DriverManager
                .getConnection("jdbc:mysql://localhost:3306/biobank2_v131",
                    "dummy", "ozzy498");

            connectionV140 = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/biobank2_pre140", "dummy",
                "ozzy498");

            DataDumpV131 dataDumpV131 = new DataDumpV131(connectionV131,
                queryProps, v131filename);
            DataDumpV140 dataDumpV140 = new DataDumpV140(connectionV140,
                queryProps, v140filename);

            // dataDumpV131.getAliquots();
            // dataDumpV131.getPvSourceVessels();
            // dataDumpV131.getAliquotStorageSite();
            // dataDumpV131.getSiteStudies();
            // dataDumpV131.getStudyContacts();
            // dataDumpV131.getSiteContainers();
            // dataDumpV131.getClinicShipments();
            // dataDumpV131.getDispatchAliquots();
            dataDumpV131.getPatientVisits();

            // dataDumpV140.getAliquotedSpecimens();
            // dataDumpV140.getSourceSpecimens();
            // dataDumpV140.getSpecimenStorageSite();
            // dataDumpV140.getSiteStudies();
            // dataDumpV140.getStudyContacts();
            // dataDumpV140.getSiteContainers();
            // dataDumpV140.ClinicShipments();
            // dataDumpV140.getDispatchSpecimens();
            dataDumpV140.getCollectionEvents();

            dataDumpV131.dispose();
            dataDumpV140.dispose();

            System.out.println("Finished generating output files.");
        } catch (IOException ioe) {
            throw new Exception(ioe);
        }
    }

    public static void main(String[] args) throws Exception {
        new TestUpgradeV131V140();
    }

}
