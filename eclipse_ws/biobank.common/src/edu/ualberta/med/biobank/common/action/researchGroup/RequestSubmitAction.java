package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.researchGroup.SubmitRequestPermission;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.Request;
import edu.ualberta.med.biobank.model.RequestSpecimen;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;

public class RequestSubmitAction implements Action<IdResult>
{
    private static final long serialVersionUID = 1L;
    private static final Bundle bundle = new CommonBundle();

    @SuppressWarnings("nls")
    public static final LString BLANK_SPECIMEN_ID_ERRMSG = bundle.tr("Blank specimen id, please check your your file for correct" + " input.").format();

    private final Integer rgId;
    private final List<String> specs;
    private final List<String> studies;
    private final Integer workingCenterId;

    @SuppressWarnings("nls")
    private final String SPECIMEN_INFO_HQL = "SELECT specimen "
            + " FROM " + Specimen.class.getName() + " specimen"
            + " INNER JOIN FETCH specimen.collectionEvent collectionEvent"
            + " LEFT JOIN FETCH collectionEvent.patient patient"
            + " LEFT JOIN FETCH patient.study"
            + " WHERE specimen.inventoryId = ?";

    public RequestSubmitAction(Integer rgId, List<String> specs)
    {
        this.rgId = rgId;
        this.specs = specs;
        this.studies = null;
        this.workingCenterId = null;
    }

    public RequestSubmitAction(Integer rgId, List<String> specs, List<String> studies, Integer workingCenterId)
    {
        this.rgId = rgId;
        this.specs = specs;
        this.studies = studies;
        this.workingCenterId = workingCenterId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException
    {
        return new SubmitRequestPermission(rgId).isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public IdResult run(ActionContext context) throws ActionException
    {
        Request request = new Request();
        request.setResearchGroup(context.get(ResearchGroup.class, rgId));
        request.setCreatedAt(new Date());
        request.setSubmitted(new Date());
        request.setAddress(context.get(ResearchGroup.class,rgId).getAddress());

        context.getSession().saveOrUpdate(request);
        context.getSession().flush();

        //OHSDEV
        // before upload specimens let's verify if they already uploaded before
        // it will provide the list of blank or uploaded already specimens
        checkSpecimens(context,specs);

        for (String id : specs)
        {
		Query q = context.getSession().createQuery("from " + Specimen.class.getName() + " where inventoryId=?");
		q.setParameter(0, id);
		Specimen spec = (Specimen) q.list().get(0);

		if (spec == null)
			continue;

		RequestSpecimen r = new RequestSpecimen();
		r.setRequest(request);
		r.setState(RequestSpecimenState.AVAILABLE_STATE);
		r.setSpecimen(spec);
		context.getSession().saveOrUpdate(r);
        }

        return new IdResult(request.getId());
    }

    //OHSDEV
    @SuppressWarnings("nls")
    private void checkSpecimens(ActionContext context,List<String> specs) throws ActionException
    {
	StringBuffer procesedSpecimens = new StringBuffer(StringUtil.EMPTY_STRING);
	StringBuffer blankSpecimens = new StringBuffer(StringUtil.EMPTY_STRING);
	StringBuffer wrongSpecimens = new StringBuffer(StringUtil.EMPTY_STRING);
	StringBuffer duplicateSpecimens = new StringBuffer(StringUtil.EMPTY_STRING);
	int numErrors = 0;
	List<String> procesedIds = new ArrayList<String>();

	for (String id : specs)
	{
		if (numErrors > 9) break;

		if(procesedIds.contains(id))
		{
			duplicateSpecimens.append("Duplicate specimen found "+ id).append(System.lineSeparator());
			numErrors++;
		}

            if (id == null || id.equals(""))
            {
			blankSpecimens.append("Blank specimen id, please check your your file for correct ").append(System.lineSeparator());
			numErrors++;
            }

            Query q = context.getSession().createQuery("from " + Specimen.class.getName() + " where inventoryId=?");
            q.setParameter(0, id);

	        @SuppressWarnings("unchecked")
			List<Object> ret = q.list();
	        if(ret == null || ret.size()==0)
	        {
			wrongSpecimens.append("Inventory ID "+ id + " is not correct ").append(System.lineSeparator());
			numErrors++;
			continue;
	        }

	        Specimen spec = (Specimen) q.list().get(0);
            if (spec != null)
            {
		Set<RequestSpecimen> reqSpecimen = spec.getRequestSpecimens();

		if(reqSpecimen != null && !reqSpecimen.isEmpty())
		{
			procesedSpecimens.append ("Specimen " + id + " has already been requested ").append(System.lineSeparator());
			numErrors++;
		}
            }
            procesedIds.add(id);

            if (spec != null)
            {
	            if(spec.getSpecimenPosition() == null) {
				wrongSpecimens.append("Inventory ID "+ id + " requested does not have a position assigned ").append(System.lineSeparator());
				numErrors++;
	            }

		        if(!spec.getCurrentCenter().getId().equals(workingCenterId)) {
				wrongSpecimens.append("Inventory ID "+ id + " requested does not belong to current center ").append(System.lineSeparator());
				numErrors++;
		        }
            }

            q = context.getSession().createQuery(SPECIMEN_INFO_HQL);
            q.setParameter(0, id);
            spec = (Specimen) q.list().get(0);

            if (spec != null)
            {
		String specimenStudyId =  spec.getCollectionEvent().getPatient().getStudy().getId().toString();

		if(!studies.contains(specimenStudyId))
		{
				wrongSpecimens.append("Inventory ID "+ id + " requested does not belong to the studies associated with the Research Group ").append(System.lineSeparator());
				numErrors++;
		}
            }
	}

	if (duplicateSpecimens.length() != 0 || wrongSpecimens.length() != 0 || procesedSpecimens.length() !=0 || blankSpecimens.length() !=0 )
	{
		 throw new LocalizedException(bundle.tr("Some specimens that are being requested require attention - "+ System.lineSeparator() +
				 duplicateSpecimens.toString() + blankSpecimens.toString() + procesedSpecimens.toString() + wrongSpecimens.toString()).format());
	}
    }
}