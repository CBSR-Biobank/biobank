package edu.ualberta.med.biobank.common.action.request;

import java.util.List;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.request.UpdateRequestPermission;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;

public class RequestDispatchAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 89092566507468524L;
    private DispatchSaveAction dsave;
    private List<Integer> specs;
    private Integer workingCenter;
    private RequestSpecimenState rsstate;

    public RequestDispatchAction(List<Integer> specs,
        RequestSpecimenState rsstate,
        DispatchSaveAction dsave,
        Integer workingCenter) {
        this.specs = specs;
        this.dsave = dsave;
        this.rsstate = rsstate;
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
        RequestStateChangeAction stateaction =
            new RequestStateChangeAction(specs, rsstate);
        stateaction.run(context);
        dsave.run(context);
        return new EmptyResult();
    }
}
