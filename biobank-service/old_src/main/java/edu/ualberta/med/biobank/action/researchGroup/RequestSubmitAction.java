package edu.ualberta.med.biobank.action.researchGroup;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.IdResult;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.CommonBundle;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.study.Request;
import edu.ualberta.med.biobank.model.study.RequestSpecimen;
import edu.ualberta.med.biobank.model.study.Specimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.permission.researchGroup.SubmitRequestPermission;

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
        request.setTimeCreated(new Date());
        request.setSubmitted(new Date());
        request.setAddress(context.get(ResearchGroup.class,
            rgId).getAddress());

        context.getSession().saveOrUpdate(request);
        context.getSession().flush();

        for (String id : specs) {
            if (id == null || id.equals(""))
                throw new LocalizedException(BLANK_SPECIMEN_ID_ERRMSG);

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
