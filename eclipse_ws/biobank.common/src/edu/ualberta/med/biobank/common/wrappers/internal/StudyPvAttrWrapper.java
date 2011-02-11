package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.PvAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.base.StudyPvAttrBaseWrapper;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class StudyPvAttrWrapper extends StudyPvAttrBaseWrapper {

    public StudyPvAttrWrapper(WritableApplicationService appService,
        StudyPvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudyPvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsedByPatientVisits()) {
            throw new BiobankCheckException("Unable to delete PvAttr with id "
                + getId() + ". A patient visit using it exists in storage."
                + " Remove all instances before deleting this type.");
        }
    }

    public static final String IS_USED_BY_VISITS_QRY = "select count(pva) from "
        + PvAttr.class.getName()
        + " as pva where pva."
        + PvAttrPeer.STUDY_PV_ATTR.getName() + "=?)";

    public boolean isUsedByPatientVisits() throws ApplicationException,
        BiobankException {
        HQLCriteria c = new HQLCriteria(IS_USED_BY_VISITS_QRY,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return results.get(0) > 0;
    }

    @Override
    public String toString() {
        return "" + getId() + ":\"" + getLabel() + "\":\"" + getPermissible()
            + "\":" + getActivityStatus() + ":" + getPvAttrType().getName()
            + ":" + getStudy();
    }

    public static List<StudyPvAttrWrapper> getStudyPvAttrCollection(
        StudyWrapper study) {
        List<StudyPvAttrWrapper> result = new ArrayList<StudyPvAttrWrapper>();
        Collection<StudyPvAttr> studyPvAttrCollection = study
            .getWrappedObject().getStudyPvAttrCollection();
        if (studyPvAttrCollection != null) {
            for (StudyPvAttr studyPvAttr : studyPvAttrCollection) {
                result.add(new StudyPvAttrWrapper(study.getAppService(),
                    studyPvAttr));
            }
        }
        return result;

    }
}
