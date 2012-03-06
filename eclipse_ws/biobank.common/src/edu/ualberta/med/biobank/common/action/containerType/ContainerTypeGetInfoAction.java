package edu.ualberta.med.biobank.common.action.containerType;

import java.util.List;

import org.hibernate.Query;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.containerType.ContainerTypeGetInfoAction.ContainerTypeInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.ModelNotFoundException;
import edu.ualberta.med.biobank.common.permission.containerType.ContainerTypeReadPermission;
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
            + " LEFT JOIN FETCH comments.user"
            + " WHERE ctype.id = ?";

    public static class ContainerTypeInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        private ContainerType containerType;

        public ContainerTypeInfo(ContainerType containerType) {
            this.containerType = containerType;
        }

        public ContainerType getContainerType() {
            return containerType;
        }
    }

    private final Integer ctypeId;

    public ContainerTypeGetInfoAction(Integer ctypeId) {
        this.ctypeId = ctypeId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        return new ContainerTypeReadPermission(ctypeId).isAllowed(context);
    }

    @Override
    public ContainerTypeInfo run(ActionContext context)
        throws ActionException {
        Query query = context.getSession().createQuery(CTYPE_INFO_HQL);
        query.setParameter(0, ctypeId);

        @SuppressWarnings("unchecked")
        List<ContainerType> containerTypes = query.list();

        if (containerTypes.size() != 1) {
            throw new ModelNotFoundException(ContainerType.class, ctypeId);
        }

        ContainerTypeInfo containerTypeInfo =
            new ContainerTypeInfo(containerTypes.get(0));
        return containerTypeInfo;
    }
}
