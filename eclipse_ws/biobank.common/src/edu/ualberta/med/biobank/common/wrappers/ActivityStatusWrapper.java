package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.ActivityStatus;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ActivityStatusWrapper extends ModelWrapper<ActivityStatus> {

    public ActivityStatusWrapper(WritableApplicationService appService,
        ActivityStatus wrappedObject) {
        super(appService, wrappedObject);
    }

    public ActivityStatusWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name" };
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    @Override
    protected void deleteChecks() throws Exception {
        throw new BiobankCheckException("object of this type cannot be deleted");
    }

    @Override
    public Class<ActivityStatus> getWrappedClass() {
        return ActivityStatus.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
        throw new BiobankCheckException(
            "should not be adding objects of this type");
    }

    @Override
    public int compareTo(ModelWrapper<ActivityStatus> wrapper) {
        if (wrapper instanceof ActivityStatusWrapper) {
            String name1 = wrappedObject.getName();
            String name2 = wrapper.wrappedObject.getName();
            if (name1 != null && name2 != null) {
                return name1.compareTo(name2);
            }
        }
        return 0;
    }

    public static List<String> getAllActivityStatusNames(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria c = new HQLCriteria("from "
            + ActivityStatus.class.getName());
        List<ActivityStatus> result = appService.query(c);
        List<String> list = new ArrayList<String>();
        for (ActivityStatus ac : result) {
            list.add(ac.getName());
        }
        return list;
    }

    public static ActivityStatusWrapper getActivityStatus(
        WritableApplicationService appService, String name)
        throws ApplicationException, BiobankCheckException {
        HQLCriteria c = new HQLCriteria("from "
            + ActivityStatus.class.getName() + " where name = ?", Arrays
            .asList(new Object[] { name }));
        List<ActivityStatus> result = appService.query(c);
        if (result.size() == 0)
            return null;
        if (result.size() != 1) {
            throw new BiobankCheckException("Invalid size for HQL query result");
        }
        return new ActivityStatusWrapper(appService, result.get(0));
    }

}
