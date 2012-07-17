package edu.ualberta.med.biobank.auditor;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Activity;
import edu.ualberta.med.biobank.model.Activity.Arg;
import edu.ualberta.med.biobank.model.Container;

public class ActivityLogger {
    ActivityLogger(Session session) {
    }

    public void log(ActivityTemplate template, Arg... args) {
        Activity activity = new Activity();
    }

    public interface IEvent {
        Activity getActivity();
    }

    public enum ActivityType {

    }

    public static class ContainerMoveEvent {
        public ContainerMoveEvent(Container movedContainer, Container newParent) {
            Activity activity = new Activity();
            activity.getArgs().set(0,
                new Arg(movedContainer.getLabel(), movedContainer));
        }
    }
}
