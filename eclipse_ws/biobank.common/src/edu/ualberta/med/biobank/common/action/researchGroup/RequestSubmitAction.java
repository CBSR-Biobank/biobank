package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.researchGroup.SubmitRequestPermission;
import edu.ualberta.med.biobank.common.util.RequestSpecimenState;
import edu.ualberta.med.biobank.i18n.Msg;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Specimen;

public class RequestSubmitAction implements Action<IdResult> {
    /**
     * 
     */
    private static final long serialVersionUID = -448974534976230815L;
    private Integer rgId;
    private List<String> specs;

    public RequestSubmitAction(Integer rgId, List<String> specs) {
        this.rgId = rgId;
        this.specs = specs;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new SubmitRequestPermission(rgId).isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public IdResult run(ActionContext context) throws ActionException {
        Request request = new Request();
        request.setResearchGroup(context.get(ResearchGroup.class, rgId));
        request.setCreated(new Date());
        request.setSubmitted(new Date());
        request.setAddress(context.get(ResearchGroup.class,
            rgId).getAddress());

        context.getSession().saveOrUpdate(request);
        context.getSession().flush();

        for (String id : specs) {
            if (id == null || id.equals(""))
                throw new ActionException(
                    Msg.tr("Blank specimen id, please check your your file for correct input."));

            Query q = context.getSession().createQuery("from "
                + Specimen.class.getName() + " where inventoryId=?");
            q.setParameter(0, id);

            Specimen spec = (Specimen) q.list().get(0);
            if (spec == null)
                continue;
            RequestSpecimen r =
                new RequestSpecimen();
            r.setRequest(request);
            r.setState(RequestSpecimenState.AVAILABLE_STATE.getId());
            r.setSpecimen(spec);
            context.getSession().saveOrUpdate(r);
        }

        return new IdResult(request.getId());
    }
}
