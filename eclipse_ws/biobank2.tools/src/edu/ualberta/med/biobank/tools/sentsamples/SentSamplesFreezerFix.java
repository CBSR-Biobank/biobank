package edu.ualberta.med.biobank.tools.sentsamples;

import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.action.LocalActionExecutor;
import edu.ualberta.med.biobank.common.action.container.ContainerMoveAction;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
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

	private static final SessionProvider SESSION_PROVIDER;
	private static final LocalActionExecutor EXECUTOR;

	static {
		SESSION_PROVIDER = new SessionProvider(Mode.RUN);
		EXECUTOR = new LocalActionExecutor(SESSION_PROVIDER);
	}

	private static final String SS_FREEZER_CONTAINER_TYPE_NAME_SHORT = "F4x6";

	private final Session session;

	private final String globalAdminUserLogin = "loyola";

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
		session = SESSION_PROVIDER.openSession();

		globalAdminUser = (User) session.createCriteria(User.class)
				.add(Restrictions.eq("login", globalAdminUserLogin))
				.uniqueResult();

		if (globalAdminUser == null) {
			throw new IllegalStateException(globalAdminUserLogin
					+ " user not found");
		}

		EXECUTOR.setUserId(globalAdminUser.getId());
		log.debug("username: {}", appArgs.username);

		session.beginTransaction();
		ContainerType newSsCtype = createSentSamplesContainerType();
		Container oldSsFreezer = getSsFreezer();
		renameSsFreezer(oldSsFreezer);

		Container newSsFreezer = createNewSentSamplesFreezer(newSsCtype,
				oldSsFreezer);

		session.getTransaction().commit();

		moveOldSsFreezerChildren(oldSsFreezer, newSsFreezer);

		log.debug("sent samples freezer size increased");
	}

	private ContainerType createSentSamplesContainerType() {
		ContainerType oldContainerType = (ContainerType) session
				.createCriteria(ContainerType.class)
				.add(Restrictions.eq("nameShort",
						SS_FREEZER_CONTAINER_TYPE_NAME_SHORT)).uniqueResult();
		if (oldContainerType == null) {
			throw new IllegalStateException("Container type F4x6 not found");
		}

		ContainerType newSsCtype = new ContainerType();
		newSsCtype.setName("Sent Samples Freezer Type");
		newSsCtype.setNameShort("SSFT");
		newSsCtype.setTopLevel(true);
		newSsCtype.setDefaultTemperature(oldContainerType
				.getDefaultTemperature());
		newSsCtype.setActivityStatus(ActivityStatus.ACTIVE);

		Capacity capacity = new Capacity();
		capacity.setRowCapacity(10);
		capacity.setColCapacity(10);

		newSsCtype.setCapacity(capacity);
		newSsCtype.setSite(oldContainerType.getSite());
		newSsCtype.setChildLabelingScheme(oldContainerType
				.getChildLabelingScheme());

		newSsCtype.getChildContainerTypes().addAll(
				oldContainerType.getChildContainerTypes());

		session.save(newSsCtype);

		return newSsCtype;
	}

	private Container getSsFreezer() {
		Container ssFreezer = (Container) session
				.createCriteria(Container.class)
				.add(Restrictions.eq("label", "SS")).uniqueResult();
		if (ssFreezer == null) {
			throw new IllegalStateException("SS freezer container not found");
		}
		return ssFreezer;
	}

	private void renameSsFreezer(Container ssFreezer) {
		ssFreezer.setLabel("OLD_SS");
		session.saveOrUpdate(ssFreezer);
	}

	private Container createNewSentSamplesFreezer(ContainerType newSsCtype,
			Container oldSsFreezer) {

		Container newSsFreezer = new Container();
		newSsFreezer.setLabel("SS");
		newSsFreezer.setTemperature(oldSsFreezer.getTemperature());
		newSsFreezer.setSite(oldSsFreezer.getSite());
		newSsFreezer.setContainerType(newSsCtype);
		newSsFreezer.setTopContainer(newSsFreezer);

		Comment comment = new Comment();
		comment.setUser(globalAdminUser);
		comment.setCreatedAt(DateFormatter.parseToDateTime("1970-01-01 00:00"));
		comment.setMessage("This freezer holds samples that have been sent out.");

		newSsFreezer.getComments().add(comment);

		session.save(comment);
		session.save(newSsFreezer);
		return newSsFreezer;
	}

	private void moveOldSsFreezerChildren(Container oldSsFreezer,
			Container newSsFreezer) {
		for (ContainerPosition childPos : oldSsFreezer.getChildPositions()) {
			Container childContainer = childPos.getContainer();
			ContainerMoveAction moveAction = new ContainerMoveAction(
					childContainer, newSsFreezer, childContainer.getLabel());
			EXECUTOR.exec(moveAction);
		}
	}
}
