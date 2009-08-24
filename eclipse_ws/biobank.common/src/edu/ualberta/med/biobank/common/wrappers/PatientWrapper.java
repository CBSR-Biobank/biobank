package edu.ualberta.med.biobank.common.wrappers;

import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.DatabaseResult;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class PatientWrapper extends ModelWrapper<Patient> {

    public PatientWrapper(WritableApplicationService appService, Patient patient) {
        super(appService, patient);
    }

    public Patient getPatient() {
        return wrappedObject;
    }

    @Override
    public Integer getId() {
        return wrappedObject.getId();
    }

    public String getNumber() {
        return wrappedObject.getNumber();
    }

    public void setNumber(String number) {
        wrappedObject.setNumber(number);
    }

    public Study getStudy() {
        return wrappedObject.getStudy();
    }

    public void setStudy(Study study) {
        wrappedObject.setStudy(study);
    }

    @Override
    protected void internalReload() throws Exception {
        wrappedObject = ModelUtils.getObjectWithId(appService, Patient.class,
            getId());
    }

    public boolean checkPatientNumberUnique() throws ApplicationException {
        if (isNew()) {
            HQLCriteria c = new HQLCriteria("from " + Patient.class.getName()
                + "where study = ? and number = ?", Arrays.asList(new Object[] {
                wrappedObject.getStudy(), getNumber() }));

            List<Object> results = appService.query(c);
            return results.size() == 0;
        }
        return true;
    }

    @Override
    protected Patient getNewObject() {
        return new Patient();
    }

    @Override
    protected void firePropertyChanges(Patient oldValue, Patient wrappedObject) {
        propertyChangeSupport.firePropertyChange("number",
            oldValue.getNumber(), wrappedObject.getNumber());
    }

    @Override
    protected DatabaseResult persistChecks() throws ApplicationException {
        if (checkPatientNumberUnique()) {
            return DatabaseResult.OK;
        }
        return new DatabaseResult("A patient with number \"" + getNumber()
            + "\" already exists.");
    }

}
