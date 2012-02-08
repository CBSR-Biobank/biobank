package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupReadInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.model.ResearchGroup;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author aaron
 * 
 */
public class ResearchGroupGetInfoAction implements
    Action<ResearchGroupReadInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String RESEARCH_INFO_HQL = "select distinct rg from "
    + ResearchGroup.class.getName() +" rg left join fetch rg.commentCollection inner join fetch rg.study inner join fetch rg.address where rg.id=?";
    // @formatter:on

    private final Integer rgId;

    public ResearchGroupGetInfoAction(Integer rgId) {
        this.rgId = rgId;
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

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.rg = (ResearchGroup) row;

        } else {
            throw new ActionException("No research group found with id:" + rgId); //$NON-NLS-1$
        }

        return sInfo;
    }

}
