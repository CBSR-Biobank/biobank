package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvInfoPossibleWrapper extends ModelWrapper<PvInfoPossible> {

    public PvInfoPossibleWrapper(WritableApplicationService appService,
        PvInfoPossible wrappedObject) {
        super(appService, wrappedObject);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "label", "isDefault", "pvInfoType" };
    }

    @Override
    protected Class<PvInfoPossible> getWrappedClass() {
        return PvInfoPossible.class;
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
        String oldLabel = getLabel();
        wrappedObject.setLabel(label);
        propertyChangeSupport.firePropertyChange("label", oldLabel, label);
    }

    public Boolean getIsDefault() {
        return wrappedObject.getIsDefault();
    }

    public void setIsDefault(Boolean isDefault) {
        Boolean oldIsDefault = getIsDefault();
        wrappedObject.setIsDefault(isDefault);
        propertyChangeSupport.firePropertyChange("isDefault", oldIsDefault,
            isDefault);
    }

    public PvInfoTypeWrapper getPvInfoType() {
        return new PvInfoTypeWrapper(appService, wrappedObject.getPvInfoType());
    }

    public void setPvInfoType(PvInfoType pvInfoType) {
        PvInfoType oldPvInfo = wrappedObject.getPvInfoType();
        wrappedObject.setPvInfoType(pvInfoType);
        propertyChangeSupport.firePropertyChange("pvInfoType", oldPvInfo,
            pvInfoType);
    }
}
