package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.RequestSpecimenInfo;
import edu.ualberta.med.biobank.common.permission.request.UpdateRequestPermission;
import edu.ualberta.med.biobank.model.RequestSpecimen;

public class RequestUpdateSpecimensAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 89092566507468524L;
    private DispatchSaveAction dsave;
    private List<RequestSpecimenInfo> specs;
    private Integer workingCenter;

    public RequestUpdateSpecimensAction(List<RequestSpecimenInfo> specs,
        DispatchSaveAction dsave,
        Integer workingCenter) {
        this.specs = specs;
        this.dsave = dsave;
        this.workingCenter = workingCenter;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UpdateRequestPermission(workingCenter, specs)
            .isAllowed(context);
    }

    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        // Dispatch is saved here because it is all one transaction
        for (RequestSpecimenInfo spec : specs) {
            RequestSpecimen specimen =
                context.load(RequestSpecimen.class, spec.requestSpecimenId);
            specimen.setState(spec.requestSpecimenStateId);
            if (spec.claimedBy != null) specimen.setClaimedBy(spec.claimedBy);
            context.getSession().saveOrUpdate(specimen);
        }
        if (dsave != null) dsave.run(context);
        return new EmptyResult();
    }

}
