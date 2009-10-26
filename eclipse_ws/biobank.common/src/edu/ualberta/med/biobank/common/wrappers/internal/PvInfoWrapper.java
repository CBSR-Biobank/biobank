package edu.ualberta.med.biobank.common.wrappers.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvInfoWrapper extends ModelWrapper<PvInfo> {

    public PvInfoWrapper(WritableApplicationService appService,
        PvInfo wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvInfoWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "label", "possibleValues", "pvInfoPossible",
            "pvInfoType" };
    }

    @Override
    public Class<PvInfo> getWrappedClass() {
        return PvInfo.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public String getLabel() {
        return wrappedObject.getLabel();
    }

    public void setLabel(String label) {
        String oldLabel = wrappedObject.getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public String getAllowedValues() {
        return wrappedObject.getAllowedValues();
    }

    public void setAllowedValues(String possibleValues) {
        String oldPV = wrappedObject.getAllowedValues();
        wrappedObject.setAllowedValues(possibleValues);
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
        setPvInfoPossible(pvInfoPossible.getWrappedObject());
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
        setPvInfoType(pvInfoType.getWrappedObject());
    }

    @Override
    public int compareTo(ModelWrapper<PvInfo> o) {
        return 0;
    }

}
