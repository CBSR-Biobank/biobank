package edu.ualberta.med.biobank.common.action.researchGroup;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.model.ResearchGroup;

/**
 * 
 * Action object that reads a specific Research Group along with
 * it's associated studies from the database
 *
 * Code Changes -
 * 		1> Change the query to be similar to a Site(SiteGetInfoAction)
 * 		2> Add the joins for getting the studies & address of the Research Group
 * 		3> Remove DISTINCT as we can have multiple rows with same Research Group Info and different Study
 * 		4> Add a call to new class ResearchGroupGetStudyInfoAction to get the short name for the studies
 *
 * @author aaron (Original code that has been updated)
 * @author OHSDEV
 * 
 */
public class ResearchGroupGetInfoAction implements
    Action<ResearchGroupReadInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String RESEARCH_INFO_HQL =
        "SELECT rg FROM "
            + ResearchGroup.class.getName() + " rg"
            + " INNER JOIN FETCH rg.address address"		//OHSDEV
            + " LEFT JOIN FETCH rg.studies studies"			//OHSDEV
            + " LEFT JOIN FETCH rg.comments comments"
            + " LEFT JOIN FETCH comments.user"
            + " where rg.id=?";

    private final Integer rgId;

    public ResearchGroupGetInfoAction(Integer rgId) {
        this.rgId = rgId;
    }

    public ResearchGroupGetInfoAction(ResearchGroup rg) {
        this(rg.getId());
    }

    @Override
    public boolean isAllowed(ActionContext context) {
        return new ResearchGroupReadPermission(rgId).isAllowed(context);
    }

    @Override
    public ResearchGroupReadInfo run(ActionContext context)
        throws ActionException {
        ResearchGroupReadInfo sInfo = new ResearchGroupReadInfo();

        Query query = context.getSession().createQuery(RESEARCH_INFO_HQL);
        query.setParameter(0, rgId);

        ResearchGroup researchGroup = (ResearchGroup) query.uniqueResult();

        sInfo.setResearchGroup(researchGroup);
        sInfo.setStudies(new ResearchGroupGetStudyInfoAction(rgId).run(context).getList());			//OHSDEV

        return sInfo;
    }
}