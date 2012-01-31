package edu.ualberta.med.biobank.common.permission.request;

import java.util.List;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.info.RequestSpecimenInfo;
import edu.ualberta.med.biobank.common.permission.Permission;
import edu.ualberta.med.biobank.common.permission.PermissionEnum;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.RequestSpecimen;

public class UpdateRequestPermission implements Permission {

    /**
     * 
     */
    private static final long serialVersionUID = 5212290952626711959L;
    private Integer workingCenterId;
    private List<RequestSpecimenInfo> specs;

    public UpdateRequestPermission(Integer workingCenterId,
        List<RequestSpecimenInfo> specs) {
        this.workingCenterId = workingCenterId;
        this.specs = specs;
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        for (RequestSpecimenInfo spec : specs)
            if (!context.load(RequestSpecimen.class, spec.requestSpecimenId)
                .getSpecimen().getCurrentCenter().getId()
                .equals(workingCenterId))
                return false;
        return PermissionEnum.REQUEST_UPDATE.isAllowed(context.getUser(),
            context.get(Center.class, workingCenterId));
    }
}
