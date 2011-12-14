package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.EventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction.TaskList;
import edu.ualberta.med.biobank.common.wrappers.base.StudyEventAttrBaseWrapper;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyEventAttrWrapper extends StudyEventAttrBaseWrapper {

    public StudyEventAttrWrapper(WritableApplicationService appService,
        StudyEventAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudyEventAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @SuppressWarnings("nls")
    public static final String IS_USED_BY_COL_EVENTS_QRY =
        "select count(ea) from "
            + EventAttr.class.getName()
            + " as ea where ea."
            + EventAttrPeer.STUDY_EVENT_ATTR.getName() + "=?)";

    public boolean isUsedByCollectionEvents() throws ApplicationException,
        BiobankException {
        if (isNew()) {
            return false;
        }
        HQLCriteria c = new HQLCriteria(IS_USED_BY_COL_EVENTS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "" + getId() + ":\"" + getGlobalEventAttr().getLabel() + "\":\""
            + getPermissible() + "\":" + getActivityStatus() + ":"
            + getGlobalEventAttr().getEventAttrType().getName() + ":"
            + getStudy();
    }

    public static List<StudyEventAttrWrapper> getStudyEventAttrCollection(
        StudyWrapper study) {
        return study.getStudyEventAttrCollection(false);
    }

    @Override
    protected void addDeleteTasks(TaskList tasks) {

        tasks.add(check().notUsedBy(EventAttr.class,
            EventAttrPeer.STUDY_EVENT_ATTR));

        super.addDeleteTasks(tasks);
    }
}
