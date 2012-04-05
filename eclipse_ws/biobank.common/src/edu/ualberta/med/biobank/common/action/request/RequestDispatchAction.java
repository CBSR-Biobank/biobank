package edu.ualberta.med.biobank.common.action.request;

import java.util.List;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.EmptyResult;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.permission.request.UpdateRequestPermission;
import edu.ualberta.med.biobank.i18n.Msg;
import edu.ualberta.med.biobank.model.Dispatch;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;

public class RequestDispatchAction implements Action<EmptyResult> {

    /**
     * 
     */
    private static final long serialVersionUID = 89092566507468524L;
    private DispatchSaveInfo dInfo;
    private List<Integer> specs;
    private RequestSpecimenState rsstate;
    private Integer requestId;
    private Set<DispatchSpecimenInfo> dspecs;

    public RequestDispatchAction(Integer requestId, List<Integer> specs,
        RequestSpecimenState rsstate,
        DispatchSaveInfo dInfo, Set<DispatchSpecimenInfo> dspecs) {
        this.specs = specs;
        this.dInfo = dInfo;
        this.dspecs = dspecs;
        this.rsstate = rsstate;
        this.requestId = requestId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new UpdateRequestPermission(specs)
            .isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public EmptyResult run(ActionContext context) throws ActionException {
        // Dispatch is saved here because it is all one transaction
        DispatchSaveAction dsave =
            new DispatchSaveAction(dInfo, dspecs, null);

        if (!dsave.isAllowed(context))
            throw new ActionException(
                Msg.tr("You do not have permission to dispatch this."));
        RequestStateChangeAction stateaction =
            new RequestStateChangeAction(specs, rsstate);
        stateaction.run(context);
        Request request = context.get(Request.class, requestId);
        request.getDispatches()
            .add(context.get(Dispatch.class, dInfo.id));
        context.getSession().saveOrUpdate(request);
        dsave.run(context);
        return new EmptyResult();
    }
}
