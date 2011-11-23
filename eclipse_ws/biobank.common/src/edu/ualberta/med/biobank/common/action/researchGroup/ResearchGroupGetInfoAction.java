package edu.ualberta.med.biobank.common.action.researchGroup;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupFormReadInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupReadPermission;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.model.User;

/**
 * Retrieve a patient information using a patient id
 * 
 * @author aaron
 * 
 */
public class ResearchGroupGetInfoAction implements Action<ResearchGroupFormReadInfo> {
    private static final long serialVersionUID = 1L;
    // @formatter:off
    @SuppressWarnings("nls")
    private static final String RESEARCH_INFO_HQL = "select rg from "
    + ResearchGroup.class.getName() +" rg inner join fetch rg.activityStatus left join fetch rg.commentCollection inner join fetch rg.study inner join fetch rg.address where rg.id=?";
    // @formatter:on

    private final Integer rgId;

    public ResearchGroupGetInfoAction(Integer rgId) {
        this.rgId = rgId;
    }

    @Override
    public boolean isAllowed(User user, Session session) {
        return new ResearchGroupReadPermission(rgId).isAllowed(user, session);
    }

    @Override
    public ResearchGroupFormReadInfo run(User user, Session session) throws ActionException {
        ResearchGroupFormReadInfo sInfo = new ResearchGroupFormReadInfo();

        Query query = session.createQuery(RESEARCH_INFO_HQL);
        query.setParameter(0, rgId);

        @SuppressWarnings("unchecked")
        List<Object[]> rows = query.list();
        if (rows.size() == 1) {
            Object row = rows.get(0);

            sInfo.rg = (ResearchGroup) row;

        } else {
            throw new ActionException("No patient found with id:" + rgId); //$NON-NLS-1$
        }

        return sInfo;
    }

}
