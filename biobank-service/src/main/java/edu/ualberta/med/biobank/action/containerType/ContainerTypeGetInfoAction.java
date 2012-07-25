package edu.ualberta.med.biobank.action.containerType;

import org.hibernate.Query;

import edu.ualberta.med.biobank.action.Action;
import edu.ualberta.med.biobank.action.ActionContext;
import edu.ualberta.med.biobank.action.ActionResult;
import edu.ualberta.med.biobank.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.action.exception.ActionException;
import edu.ualberta.med.biobank.permission.containerType.ContainerTypeReadPermission;
import edu.ualberta.med.biobank.model.ContainerType;

public class ContainerTypeGetInfoAction implements Action<ContainerTypeInfo> {
    private static final long serialVersionUID = 1L;

    @SuppressWarnings("nls")
    private static final String CTYPE_INFO_HQL =
        "SELECT DISTINCT ctype"
            + " FROM " + ContainerType.class.getName() + " ctype"
            + " INNER JOIN FETCH ctype.capacity"
            + " INNER JOIN FETCH ctype.childLabelingScheme"
            + " LEFT JOIN FETCH ctype.childContainerTypes"
            + " LEFT JOIN FETCH ctype.specimenTypes"
            + " LEFT JOIN FETCH ctype.comments comments"
            + " INNER JOIN FETCH ctype.site site"
            + " LEFT JOIN FETCH site.containerTypes"
            + " LEFT JOIN FETCH comments.user"
            + " WHERE ctype.id = ?";

    public static class ContainerTypeInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        private ContainerType containerType;

        public ContainerTypeInfo() {
        }

        public ContainerTypeInfo(ContainerType containerType) {
            this.containerType = containerType;
        }

        public ContainerType getContainerType() {
            return containerType;
        }
    }

    private final Integer ctypeId;

    public ContainerTypeGetInfoAction(Integer ctypeId) {
        if (ctypeId == null) {
            throw new IllegalArgumentException();
        }
        this.ctypeId = ctypeId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        ContainerType ctype = context.load(ContainerType.class, ctypeId);
        return new ContainerTypeReadPermission(ctype.getSite())
            .isAllowed(context);
    }

    @Override
    public ContainerTypeInfo run(ActionContext context)
        throws ActionException {
        Query query = context.getSession().createQuery(CTYPE_INFO_HQL);
        query.setParameter(0, ctypeId);
        ContainerTypeInfo containerTypeInfo =
            new ContainerTypeInfo((ContainerType) query.uniqueResult());
        return containerTypeInfo;
    }
}
