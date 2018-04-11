package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.model.ResearchGroup;

/**
 *
 * Action object that reads all Research Groups from the database
 *
 * Code Changes -
 * 		1> Change return type from MapResult to a ListResult for the list of Research Groups
 *
 * @author OHSDEV
 *
 */
public class ResearchGroupGetAllAction implements
	Action<ListResult<ResearchGroup>> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    public static final String ALL_RG = "from "
        + ResearchGroup.class.getName();

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // anyone can call this... but only users with permissions will get
        // results
        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ListResult<ResearchGroup> run(ActionContext context) throws ActionException
    {
        Query q = context.getSession().createQuery(ALL_RG);
        List<ResearchGroup> researchGroups = q.list();
        List<ResearchGroup> readableResearchGroups = new ArrayList<ResearchGroup>();

        for (ResearchGroup researchGroup : researchGroups)
            if (new ResearchGroupReadPermission(researchGroup.getId()).isAllowed(context))
                readableResearchGroups.add(researchGroup);

        return new ListResult<ResearchGroup>(readableResearchGroups);
    }
}