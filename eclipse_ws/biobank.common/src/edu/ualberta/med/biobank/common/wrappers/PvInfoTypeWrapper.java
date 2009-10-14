package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvInfoTypeWrapper extends ModelWrapper<PvInfoType> {

    public PvInfoTypeWrapper(WritableApplicationService appService,
        PvInfoType wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvInfoTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangesNames() {
        return new String[] { "type" };
    }

    @Override
    public Class<PvInfoType> getWrappedClass() {
        return PvInfoType.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
    }

    public String getType() {
        return wrappedObject.getType();
    }

    public void setType(String type) {
        String oldType = getType();
        wrappedObject.setType(type);
        propertyChangeSupport.firePropertyChange("type", oldType, type);
    }

    @Override
    public int compareTo(ModelWrapper<PvInfoType> o) {
        return 0;
    }

}
