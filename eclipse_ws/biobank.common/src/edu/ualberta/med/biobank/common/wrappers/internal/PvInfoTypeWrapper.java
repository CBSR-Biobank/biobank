package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.PvInfoType;
import gov.nih.nci.system.applicationservice.ApplicationException;
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
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
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

    public static List<PvInfoTypeWrapper> getAllWrappers(
        WritableApplicationService appService) throws ApplicationException {
        List<PvInfoType> objects = appService.search(PvInfoType.class,
            new PvInfoType());
        List<PvInfoTypeWrapper> wrappers = new ArrayList<PvInfoTypeWrapper>();
        for (PvInfoType pv : objects) {
            wrappers.add(new PvInfoTypeWrapper(appService, pv));
        }
        return wrappers;
    }

}
