package edu.ualberta.med.biobank.permission.request;

import java.util.List;

import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.permission.Permission;
import edu.ualberta.med.biobank.model.center.Center;
import edu.ualberta.med.biobank.model.study.RequestSpecimen;
import edu.ualberta.med.biobank.model.type.PermissionEnum;

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
        if (specs.size() == 0) return true;
        Integer id =
            context.load(RequestSpecimen.class, specs.get(0)).getSpecimen()
                .getCurrentCenter()
                .getId();
        for (Integer spec : specs)
            if (!context.load(RequestSpecimen.class, spec)
                .getSpecimen().getCurrentCenter().getId()
                .equals(id))
                return false;
        return PermissionEnum.REQUEST_UPDATE.isAllowed(context.getUser(),
            context.get(Center.class, id));
    }
}
