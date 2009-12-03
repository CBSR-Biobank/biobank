package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvAttrWrapper extends ModelWrapper<PvAttr> {

    public PvAttrWrapper(WritableApplicationService appService,
        PvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "studyPvAttr", "value", "patientVisit" };
    }

    @Override
    public Class<PvAttr> getWrappedClass() {
        return PvAttr.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public StudyPvAttrWrapper getStudyPvAttr() {
        StudyPvAttr studyPvAttr = wrappedObject.getStudyPvAttr();
        if (studyPvAttr == null) {
            return null;
        }
        return new StudyPvAttrWrapper(appService, studyPvAttr);
    }

    public void setStudyPvAttr(StudyPvAttr studyPvAttr) {
        StudyPvAttr oldInfo = wrappedObject.getStudyPvAttr();
        wrappedObject.setStudyPvAttr(studyPvAttr);
        propertyChangeSupport.firePropertyChange("studyPvAttr", oldInfo,
            studyPvAttr);
    }

    public void setStudyPvAttr(StudyPvAttrWrapper studyPvAttr) {
        setStudyPvAttr(studyPvAttr.getWrappedObject());
    }

    public void setValue(String value) {
        String oldValue = wrappedObject.getValue();
        wrappedObject.setValue(value);
        propertyChangeSupport.firePropertyChange("value", oldValue, value);
    }

    public String getValue() {
        return wrappedObject.getValue();
    }

    public PatientVisitWrapper getPatientVisit() {
        PatientVisit pv = wrappedObject.getPatientVisit();
        if (pv == null) {
            return null;
        }
        return new PatientVisitWrapper(appService, pv);
    }

    public void setPatientVisit(PatientVisit pv) {
        PatientVisit oldPv = wrappedObject.getPatientVisit();
        wrappedObject.setPatientVisit(pv);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPv, pv);
    }

    public void setPatientVisit(PatientVisitWrapper pv) {
        setPatientVisit(pv.getWrappedObject());
    }

    @Override
    public int compareTo(ModelWrapper<PvAttr> o) {
        return 0;
    }
}
