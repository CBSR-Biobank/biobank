package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientVisitWrapper;
import edu.ualberta.med.biobank.model.PatientVisit;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoData;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvInfoDataWrapper extends ModelWrapper<PvInfoData> {

    public PvInfoDataWrapper(WritableApplicationService appService,
        PvInfoData wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvInfoDataWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "pvInfo", "value", "patientVisit" };
    }

    @Override
    public Class<PvInfoData> getWrappedClass() {
        return PvInfoData.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public PvInfoWrapper getPvInfo() {
        PvInfo pvInfo = wrappedObject.getPvInfo();
        if (pvInfo == null) {
            return null;
        }
        return new PvInfoWrapper(appService, pvInfo);
    }

    public void setPvInfo(PvInfo pvInfo) {
        PvInfo oldInfo = wrappedObject.getPvInfo();
        wrappedObject.setPvInfo(pvInfo);
        propertyChangeSupport.firePropertyChange("pvInfo", oldInfo, pvInfo);
    }

    public void setPvInfo(PvInfoWrapper pvInfo) {
        setPvInfo(pvInfo.getWrappedObject());
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
    public int compareTo(ModelWrapper<PvInfoData> o) {
        return 0;
    }
}
