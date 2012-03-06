package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.base.GroupBaseWrapper;
import edu.ualberta.med.biobank.model.Group;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class GroupWrapper extends GroupBaseWrapper {

    public GroupWrapper(WritableApplicationService appService,
        Group wrappedObject) {
        super(appService, wrappedObject);
    }

    public GroupWrapper(WritableApplicationService appService) {
        super(appService);
    }

    private static final String ALL_GROUPS_QRY = " from " //$NON-NLS-1$
        + Group.class.getName();

    public static final List<GroupWrapper> getAllGroups(
        WritableApplicationService appService) throws ApplicationException {
        HQLCriteria criteria = new HQLCriteria(ALL_GROUPS_QRY,
            new ArrayList<Object>());

        List<Group> groups = appService.query(criteria);
        return ModelWrapper.wrapModelCollection(appService, groups,
            GroupWrapper.class);
    }

    @Override
    public int compareTo(ModelWrapper<Group> group2) {
        if (group2 instanceof GroupWrapper) {
            String name1 = getName();
            String name2 = ((GroupWrapper) group2).getName();

            if (name1 == null || name2 == null)
                return 0;
            return name1.compareTo(name2);
        }
        return 0;
    }

    @Override
    public GroupWrapper duplicate() {
        return (GroupWrapper) super.duplicate();
    }

    /**
     * Duplicate a group: create a new one that will have the exact same
     * relations. This duplicated group is not yet saved into the DB.
     */
    @Override
    public GroupWrapper createDuplicate() {
        GroupWrapper newGroup = new GroupWrapper(appService);
        newGroup.setName(getName());
        return newGroup;
    }

    @Override
    public String toString() {
        return getName();
    }

}
