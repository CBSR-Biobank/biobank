package edu.ualberta.med.biobank.auditor;

import java.util.Arrays;

import org.hibernate.Session;
import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;

import edu.ualberta.med.biobank.model.Activity;
import edu.ualberta.med.biobank.model.Activity.Arg;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Revision;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.type.ActivityType;

public class ActivityLogger {
    private final Session session;

    public ActivityLogger(Session session) {
        this.session = session;
    }

    public StudyActivityLogger onStudy(Study study) {
        return null;
    }

    public CenterActivityLogger onCenter(Center center) {
        return null;
    }

    public void log(ActivityType template, Arg... args) {
        Activity activity = new Activity();

        activity.setActivityType(template);
        activity.getArgs().addAll(Arrays.asList(args));

        activity.setStudy(null);
        activity.setCenter(null);
        activity.setUser(null);

        addToCurrentRevision(activity);

        session.save(activity);
    }

    /**
     * Add the {@link Activity} to the current {@link Revision} so that if the
     * latter is saved, it will have all the associated {@link Activity} -s
     * directly linked to it.
     * 
     * @param activity to add to the current {@link Revision}.
     */
    private void addToCurrentRevision(Activity activity) {
        AuditReader auditReader = AuditReaderFactory.get(session);
        Revision rev = auditReader.getCurrentRevision(Revision.class, false);
        rev.getActivities().add(activity);
    }
}
