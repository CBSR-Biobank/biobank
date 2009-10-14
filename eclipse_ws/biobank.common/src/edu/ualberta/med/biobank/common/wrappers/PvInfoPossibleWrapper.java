package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoPossible;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvInfoPossibleWrapper extends ModelWrapper<PvInfoPossible> {

    public PvInfoPossibleWrapper(WritableApplicationService appService,
        PvInfoPossible wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvInfoPossibleWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "label", "isDefault", "pvInfoType" };
    }

    @Override
    public Class<PvInfoPossible> getWrappedClass() {
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

    public void setPvInfoType(PvInfoTypeWrapper pvInfoType) {
        setPvInfoType(pvInfoType.wrappedObject);
    }

    public static List<PvInfoPossibleWrapper> getAllWrappers(
        WritableApplicationService appService) throws ApplicationException {
        List<PvInfoPossible> objects = appService.search(PvInfoPossible.class,
            new PvInfoPossible());
        List<PvInfoPossibleWrapper> wrappers = new ArrayList<PvInfoPossibleWrapper>();
        for (PvInfoPossible pv : objects) {
            wrappers.add(new PvInfoPossibleWrapper(appService, pv));
        }
        return wrappers;
    }

    @Override
    public int compareTo(ModelWrapper<PvInfoPossible> o) {
        return 0;
    }
}