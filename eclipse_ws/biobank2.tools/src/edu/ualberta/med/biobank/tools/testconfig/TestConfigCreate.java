package edu.ualberta.med.biobank.tools.testconfig;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Used to create a configuration to easily test with. Creates the following
 *
 * - One clinic, named "Clinic1". Has one contact.
 *
 * - One study, named "Study1". Has contact from Clinic1.
 *
 * - One patient is created, "1100", which has one collection event and one parent specimen with
 *   inventory ID "A100".
 *
 * - Two site, names "Site1" and "Site2".
 *
 * @author Nelson Loyola
 *
 */
public class TestConfigCreate {

    private static String USAGE = "Usage: testconfigcreate\n\n"
                                  + "\tReads options from db.properties file.";

    private static final Logger log = LoggerFactory.getLogger(TestConfigCreate.class);

    public static void main(String[] argv) {
        try {
            AppArgs args = new AppArgs();
            args.parse(argv);
            if (args.help) {
                System.out.println(USAGE);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }

            if (args.grandchidSpecimenTypes) {
                new GrandchildSpecimenTypeTestConfig(args);
            } else {
                new TestConfig(args);
            }
            log.info("testing configuration created");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
