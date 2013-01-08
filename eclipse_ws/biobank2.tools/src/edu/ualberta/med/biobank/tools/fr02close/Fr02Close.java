package edu.ualberta.med.biobank.tools.fr02close;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.tools.GenericAppArgs;

/**
 * On January 3, 2013, CBSR requested that all specimens in Freezer 02 be closed.
 * 
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public class Fr02Close {

    private static final Logger log = LoggerFactory
        .getLogger(Fr02Close.class.getName());

    private static String USAGE =
        "Usage: fr02close [options]\n\n"
            + "Options\n"
            + "  -H, --host       hostname for BioBank server and MySQL server\n"
            + "  -p, --port       port number for BioBank server\n"
            + "  -u, --user       user name to log into BioBank server\n"
            + "  -w, --password   password to log into BioBank server\n"
            + "  -v, --verbose    shows verbose output\n"
            + "  -h, --help       shows this text\n";

    public static final String SPECIMEN_QRY =
        "SELECT spc FROM " + Specimen.class.getName() + " spc"
            + " INNER JOIN spc.specimenPosition spos"
            + " INNER JOIN spos.container ctr"
            + " INNER JOIN ctr.topContainer topctr"
            + " INNER JOIN topctr.containerType ct"
            + " WHERE topctr.label='02' AND ct.nameShort='F4x12'";

    private final SessionFactory sessionFactory;

    private final Session session;

    private final String globalAdminUserLogin = "peck";

    private final User globalAdminUser;

    public static void main(String[] argv) {
        try {
            GenericAppArgs args = new GenericAppArgs(argv);
            if (args.help) {
                System.out.println(USAGE);
                System.exit(0);
            } else if (args.error) {
                System.out.println(args.errorMsg + "\n" + USAGE);
                System.exit(-1);
            }
            new Fr02Close(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Fr02Close(GenericAppArgs args) throws Exception {
        // configure() configures settings from hibernate.cfg.xml found into the biobank-orm jar
        Configuration configuration = new Configuration().configure();
        configuration.setProperty("connection.url", "jdbc:mysql://localhost:3306/biobank");
        configuration.setProperty("connection.username", args.username);
        configuration.setProperty("connection.password", args.password);
        //configuration.setProperty("hibernate.show_sql", "false");
        //configuration.setProperty("hibernate.format_sql", "true");
        //configuration.setProperty("hibernate.use_sql_comments", "true");

        sessionFactory = configuration.buildSessionFactory();
        session = sessionFactory.openSession();

        globalAdminUser = (User) session.createCriteria(User.class)
            .add(Restrictions.eq("login", globalAdminUserLogin)).uniqueResult();

        if (globalAdminUser == null) {
            throw new RuntimeException(globalAdminUserLogin + " user not found");
        }

        Set<Specimen> specimensToClose = new HashSet<Specimen>();

        @SuppressWarnings("unchecked")
        List<Specimen> specimens = session.createQuery(SPECIMEN_QRY).list();

        if (specimens != null) {
            log.info("number of specimens in freezer: {}", specimens.size());
            for (Specimen specimen : specimens) {
                if (specimen.getActivityStatus() != ActivityStatus.CLOSED) {
                    specimensToClose.add(specimen);
                }
            }
        }

        closeSpecimens(specimensToClose);
    }

    private void closeSpecimens(Set<Specimen> specimens) {
        if (specimens.isEmpty()) {
            throw new RuntimeException("no specimens to close");
        }
        log.info("number of specimens to close: {}", specimens.size());

        session.beginTransaction();

        for (Specimen specimen : specimens) {
            log.info("closing specimen: inventoryId: {}, activityStatus: {}",
                specimen.getInventoryId(), specimen.getActivityStatus());

            Comment comment = new Comment();
            comment.setCreatedAt(new Date());
            comment.setUser(globalAdminUser);
            comment.setMessage("Aliquot pulled");

            session.save(comment);

            specimen.setActivityStatus(ActivityStatus.CLOSED);
            specimen.getComments().add(comment);
            session.update(specimen);
        }

        session.getTransaction().commit();
    }

}
