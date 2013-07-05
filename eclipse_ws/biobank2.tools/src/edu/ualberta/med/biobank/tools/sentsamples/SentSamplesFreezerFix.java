package edu.ualberta.med.biobank.tools.sentsamples;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.tools.GenericAppArgs;
import edu.ualberta.med.biobank.tools.SessionProvider;
import edu.ualberta.med.biobank.tools.SessionProvider.Mode;

public class SentSamplesFreezerFix {

    private static String USAGE = "Usage: sentsamplesmove\n\n"
        + "\tReads options from db.properties file.";

    private static final Logger log = LoggerFactory
        .getLogger(SentSamplesFreezerFix.class);

    private static final String SS_FREEZER_CONTAINER_TYPE_NAME_SHORT = "F4x6";

    private final SessionProvider sessionProvider;

    private final Session session;

    private final String globalAdminUserLogin = "testuser";

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
            new SentSamplesFreezerFix(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private SentSamplesFreezerFix(GenericAppArgs appArgs) {
        sessionProvider = new SessionProvider(Mode.RUN);
        session = sessionProvider.openSession();

        globalAdminUser = (User) session.createCriteria(User.class)
            .add(Restrictions.eq("login", globalAdminUserLogin))
            .uniqueResult();

        if (globalAdminUser == null) {
            throw new IllegalStateException(globalAdminUserLogin
                + " user not found");
        }

        log.debug("username: {}", appArgs.username);

        createSentSamplesContainerType();
    }

    private void createSentSamplesContainerType() {
        ContainerType oldContainerType =
            (ContainerType)
            session
                .createCriteria(ContainerType.class)
                .add(
                    Restrictions.eq("nameShort",
                        SS_FREEZER_CONTAINER_TYPE_NAME_SHORT)).uniqueResult();
        if (oldContainerType == null) {
            throw new IllegalStateException("Container type F4x6 not found");
        }

        log.debug("here");
        ContainerType newSsCtype = new ContainerType();
        newSsCtype.setName("Sent Samples Freezer Type");
        newSsCtype.setNameShort("SSFT");
    }
}
