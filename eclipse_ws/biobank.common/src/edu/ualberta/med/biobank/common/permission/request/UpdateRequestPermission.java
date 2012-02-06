package edu.ualberta.med.biobank.common.permission.request;

import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.RequestSpecimen;

public class UpdateRequestPermission implements Permission {

    /**
     * 
     */
    private static final long serialVersionUID = 5212290952626711959L;
    private List<Integer> specs;

    public UpdateRequestPermission(List<Integer> specs) {
        this.specs = specs;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        Integer id = specs.get(0);
        for (Integer spec : specs)
            if (!context.load(RequestSpecimen.class, spec)
                .getSpecimen().getCurrentCenter().getId()
                .equals(id))
                return false;
        return PermissionEnum.REQUEST_UPDATE.isAllowed(context.getUser(),
            context.get(Center.class, id));
    }
}
