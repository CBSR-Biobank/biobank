package edu.ualberta.med.biobank.tools.fr02close;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;

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
        "Usage: fr02close\n\n"
            + "\tReads options from db.properties file.";

    public static final String SPECIMEN_QRY =
        "SELECT spc FROM " + Specimen.class.getName() + " spc"
            + " INNER JOIN spc.specimenPosition spos"
            + " INNER JOIN spos.container ctr"
            + " INNER JOIN ctr.topContainer topctr"
            + " INNER JOIN topctr.containerType ct"
            + " WHERE topctr.label='02' AND ct.nameShort='F4x12'";

    private static final SessionProvider SESSION_PROVIDER;

    private final Session session;

    private final String globalAdminUserLogin = "peck";

    private final User globalAdminUser;

    static {
        SESSION_PROVIDER = new SessionProvider(Mode.RUN);
    }

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
            new Fr02Close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected static SessionProvider getSessionProvider() {
        return SESSION_PROVIDER;
    }

    public Fr02Close() throws Exception {
        session = getSessionProvider().openSession();

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
