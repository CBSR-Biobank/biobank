package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

import java.util.List;

@Deprecated
@SuppressWarnings("unused")
public class StudyPvAttrWrapper {

    private static class StudyPvAttr {

    }

    public StudyPvAttrWrapper(WritableApplicationService appService,
        StudyPvAttr wrappedObject) {
    }

    public StudyPvAttrWrapper(WritableApplicationService appService) {
    }

    protected void deleteChecks() throws BiobankException, ApplicationException {
    }

    public boolean isUsedByProcessingEvents() throws ApplicationException,
        BiobankException {
        return false;
    }

    public static List<StudyPvAttrWrapper> getStudyPvAttrCollection(
        StudyWrapper study) {
        return null;
    }
}
