package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvAttrWrapper extends ModelWrapper<PvAttr> {

    private StudyPvAttrWrapper studyPvAttr;
    private PatientVisitWrapper pv;

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
        if (studyPvAttr == null) {
            StudyPvAttr s = wrappedObject.getStudyPvAttr();
            if (s == null)
                return null;
            studyPvAttr = new StudyPvAttrWrapper(appService, s);
        }
        return studyPvAttr;
    }

    public void setStudyPvAttr(StudyPvAttrWrapper studyPvAttr) {
        this.studyPvAttr = studyPvAttr;
        StudyPvAttr oldPvAttr = wrappedObject.getStudyPvAttr();
        StudyPvAttr newPvAttr = null;
        if (studyPvAttr != null) {
            newPvAttr = studyPvAttr.getWrappedObject();
        }
        wrappedObject.setStudyPvAttr(newPvAttr);
        propertyChangeSupport.firePropertyChange("studyPvAttr", oldPvAttr,
            newPvAttr);
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
        if (pv == null) {
            PatientVisit p = wrappedObject.getPatientVisit();
            if (p == null)
                return null;
            pv = new PatientVisitWrapper(appService, p);
        }
        return pv;
    }

    public void setPatientVisit(PatientVisitWrapper pv) {
        this.pv = pv;
        PatientVisit oldPv = wrappedObject.getPatientVisit();
        PatientVisit newPv = null;
        if (pv != null) {
            newPv = pv.getWrappedObject();
        }
        wrappedObject.setPatientVisit(newPv);
        propertyChangeSupport.firePropertyChange("patientVisit", oldPv, newPv);
    }

    @Override
    public int compareTo(ModelWrapper<PvAttr> o) {
        return 0;
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pv = null;
        studyPvAttr = null;
    }

}
