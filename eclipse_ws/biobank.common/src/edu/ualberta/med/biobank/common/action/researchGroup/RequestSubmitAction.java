package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.researchGroup.SubmitRequestPermission;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;

public class RequestSubmitAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString BLANK_SPECIMEN_ID_ERRMSG =
        bundle.tr("Blank specimen id, please check your your file for correct" +
            " input.").format();

    private final Integer rgId;
    private final List<String> specs;

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
        request.setCreatedAt(new Date());
        request.setSubmitted(new Date());
        request.setAddress(context.get(ResearchGroup.class,
            rgId).getAddress());

        context.getSession().saveOrUpdate(request);
        context.getSession().flush();

        for (String id : specs) {
            if (id == null || id.equals(""))
                throw new ActionException(BLANK_SPECIMEN_ID_ERRMSG);

            Query q = context.getSession().createQuery("from "
                + Specimen.class.getName() + " where inventoryId=?");
            q.setParameter(0, id);

            Specimen spec = (Specimen) q.list().get(0);
            if (spec == null)
                continue;
            RequestSpecimen r =
                new RequestSpecimen();
            r.setRequest(request);
            r.setState(RequestSpecimenState.AVAILABLE_STATE);
            r.setSpecimen(spec);
            context.getSession().saveOrUpdate(r);
        }

        return new IdResult(request.getId());
    }
}
