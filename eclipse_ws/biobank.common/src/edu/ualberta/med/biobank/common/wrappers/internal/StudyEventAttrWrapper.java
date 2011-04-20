package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.EventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
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

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsedByCollectionEvents()) {
            throw new BiobankDeleteException(
                "Unable to delete EventAttr with id " + getId()
                    + ". A patient visit using it exists in storage."
                    + " Remove all instances before deleting this type.");
        }
    }

    public static final String IS_USED_BY_COL_EVENTS_QRY = "select count(ea) from "
        + EventAttr.class.getName()
        + " as ea where ea."
        + EventAttrPeer.STUDY_EVENT_ATTR.getName() + "=?)";

    public boolean isUsedByCollectionEvents() throws ApplicationException,
        BiobankException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_COL_EVENTS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":\"" + getLabel() + "\":\"" + getPermissible()
            + "\":" + getActivityStatus() + ":" + getEventAttrType().getName()
            + ":" + getStudy();
    }

    public static List<StudyEventAttrWrapper> getStudyEventAttrCollection(
        StudyWrapper study) {
        List<StudyEventAttrWrapper> result = new ArrayList<StudyEventAttrWrapper>();
        Collection<StudyEventAttr> StudyEventAttrCollection = study
            .getWrappedObject().getStudyEventAttrCollection();
        if (StudyEventAttrCollection != null) {
            for (StudyEventAttr StudyEventAttr : StudyEventAttrCollection) {
                result.add(new StudyEventAttrWrapper(study.getAppService(),
                    StudyEventAttr));
            }
        }
        return result;

    }
}
