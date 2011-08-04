package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.EventAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction;
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
        if (isNew()) {
            return false;
        }
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
        return study.getStudyEventAttrCollection(false);
    }

    // TODO: remove this override when all persist()-s are like this!
    @Override
    public void persist() throws Exception {
        WrapperTransaction.persist(this, appService);
    }

    @Override
    public void delete() throws Exception {
        WrapperTransaction.delete(this, appService);
    }
}
