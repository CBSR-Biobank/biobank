package edu.ualberta.med.biobank.common.action.specimenType;

import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeReadPermission;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.SpecimenType;

public class SpecimenTypesGetForContainerTypesAction implements
Action<ListResult<SpecimenType>> {
    private static final long serialVersionUID = 1L;

    private Set<Integer> containerTypeIds;

    public SpecimenTypesGetForContainerTypesAction(Set<ContainerType> containerTypes) {

        for (ContainerType ctype : containerTypes) {
            containerTypeIds.add(ctype.getId());
        }
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        for (Integer ctypeId : containerTypeIds) {
            ContainerType ctype = context.load(ContainerType.class, ctypeId);
            boolean allowed = new ContainerTypeReadPermission(ctype.getSite()).isAllowed(context);
            if (!allowed) return false;
        }
        return true;
    }

    @Override
    public ListResult<SpecimenType> run(ActionContext context) throws ActionException {
        // TODO Auto-generated method stub
        return null;
    }

}
