package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.model.PvAttrType;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvAttrTypeWrapper extends ModelWrapper<PvAttrType> {

    public PvAttrTypeWrapper(WritableApplicationService appService,
        PvAttrType wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvAttrTypeWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name" };
    }

    @Override
    public Class<PvAttrType> getWrappedClass() {
        return PvAttrType.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String type) {
        String oldName = getName();
        wrappedObject.setName(type);
        propertyChangeSupport.firePropertyChange("name", oldName, type);
    }

    @Override
    public int compareTo(ModelWrapper<PvAttrType> o) {
        return 0;
    }

    public static List<PvAttrTypeWrapper> getAllWrappers(
        WritableApplicationService appService) throws ApplicationException {
        List<PvAttrType> objects = appService.search(PvAttrType.class,
            new PvAttrType());
        List<PvAttrTypeWrapper> wrappers = new ArrayList<PvAttrTypeWrapper>();
        for (PvAttrType pv : objects) {
            wrappers.add(new PvAttrTypeWrapper(appService, pv));
        }
        return wrappers;
    }

}
