package edu.ualberta.med.biobank.model.type;

import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

public enum RevisionAction {
    CONTAINER_TYPE_CREATE(
        1,
        Loader.i18n.tr("Container type added"),
        Loader.i18n.tr("Added a new container type {0}")),
    CONTAINER_TYPE_UPDATE(
        2,
        Loader.i18n.tr("Container type changed"),
        // what if nameShort changed, show which one? last revision value?
        Loader.i18n.tr("Changed container type {0}")),
    PATIENT_MERGE(
        3,
        Loader.i18n.tr("Patient merge"),
        // could wrap the parameters in links if only {0} not
        // {patient1.pnumber}, but would need entity id, type, rev number, or,
        // pass it through an ognl formatter, that would make the with a hook.
        Loader.i18n.tr("Patient {0} was merged into patient {1}"));

    // Note: arguments should be from entities in the given revision to really
    // make sense. They could open a form that shows that object at the given
    // revision?

    private final int id;
    private final String type;
    private final String description;

    private RevisionAction(int id, String type, String description) {
        this.id = id;
        this.type = type;
        this.description = description;
    }

    private RevisionAction(int id, String type) {
        this(id, type, type);
    }

    public static class Loader {
        private static final I18n i18n = I18nFactory.getI18n(Loader.class);
    }
}
