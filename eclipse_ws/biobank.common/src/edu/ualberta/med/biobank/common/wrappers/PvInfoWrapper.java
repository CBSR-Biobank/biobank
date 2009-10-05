package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvInfoWrapper extends ModelWrapper<PvInfo> {

    public PvInfoWrapper(WritableApplicationService appService,
        PvInfo wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "label", "possibleValues", "pvInfoPossible",
            "pvInfoType" };
    }

    @Override
    protected Class<PvInfo> getWrappedClass() {
        return PvInfo.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public void setLabel(String label) {
        String oldLabel = wrappedObject.getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public String getPossibleValues() {
        return wrappedObject.getPossibleValues();
    }

    public void setPossibleValues(String possibleValues) {
        String oldPV = wrappedObject.getPossibleValues();
        wrappedObject.setPossibleValues(possibleValues);
        propertyChangeSupport.firePropertyChange("possibleValues", oldPV,
            possibleValues);
    }

    public PvInfoPossibleWrapper getPvInfoPossible() {
        return new PvInfoPossibleWrapper(appService, wrappedObject
            .getPvInfoPossible());
    }

    public void setPvInfoPossible(PvInfoPossible pvInfoPossible) {
        PvInfoPossible oldPVInfoPossible = wrappedObject.getPvInfoPossible();
        wrappedObject.setPvInfoPossible(pvInfoPossible);
        propertyChangeSupport.firePropertyChange("pvInfoPossible",
            oldPVInfoPossible, pvInfoPossible);
    }

    public void setPvInfoPossible(PvInfoPossibleWrapper pvInfoPossible) {
        setPvInfoPossible(pvInfoPossible.wrappedObject);
    }

    public PvInfoTypeWrapper getPvInfoType() {
        return new PvInfoTypeWrapper(appService, wrappedObject.getPvInfoType());
    }

    public void setPvInfoType(PvInfoType pvInfoType) {
        PvInfoType oldPvInfoType = wrappedObject.getPvInfoType();
        wrappedObject.setPvInfoType(pvInfoType);
        propertyChangeSupport.firePropertyChange("pvInfoType", oldPvInfoType,
            pvInfoType);
    }

    public void setPvInfoType(PvInfoTypeWrapper pvInfoType) {
        setPvInfoType(pvInfoType.wrappedObject);
    }

}
