package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankDeleteException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.SpecimenAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudySpecimenAttrBaseWrapper;
import edu.ualberta.med.biobank.model.SpecimenAttr;
import edu.ualberta.med.biobank.model.StudySpecimenAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudySpecimenAttrWrapper extends StudySpecimenAttrBaseWrapper {

    public StudySpecimenAttrWrapper(WritableApplicationService appService,
        StudySpecimenAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudySpecimenAttrWrapper(WritableApplicationService appService) {
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
        + SpecimenAttr.class.getName()
        + " as ea where ea."
        + SpecimenAttrPeer.STUDY_SPECIMEN_ATTR.getName() + "=?)";

    public boolean isUsedByCollectionEvents() throws ApplicationException,
        BiobankException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_COL_EVENTS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        return getCountResult(appService, c) > 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":\"" + getLabel() + "\":\"" + getPermissible()
            + "\":" + getActivityStatus() + ":" + getSpecimenAttrType().getName()
            + ":" + getStudy();
    }

    public static List<StudySpecimenAttrWrapper> getStudySpecimenAttrCollection(
        StudyWrapper study) {
        return study.getStudySpecimenAttrCollection(false);
    }
}
