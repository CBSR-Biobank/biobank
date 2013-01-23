package edu.ualberta.med.biobank.common.action.containerType;

import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.containerType.ContainerLabelingSchemeGetInfoAction.ContainerLabelingSchemeInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;

public class ContainerLabelingSchemeGetInfoAction
implements Action<ContainerLabelingSchemeInfo> {

    private static final long serialVersionUID = 1L;

    public static class ContainerLabelingSchemeInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        private ContainerLabelingScheme labelingScheme;

        public ContainerLabelingScheme getLabelingScheme() {
            return labelingScheme;
        }
        public void setLabelingScheme(ContainerLabelingScheme labelingScheme) {
            this.labelingScheme = labelingScheme;
        }
    }

    private final String schemeName;

    public ContainerLabelingSchemeGetInfoAction(String schemeName) {
        this.schemeName = schemeName;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        // permissions not required to retrieve this information
        return true;
    }

    @SuppressWarnings("nls")
    @Override
    public ContainerLabelingSchemeInfo run(ActionContext context) throws ActionException {
        ContainerLabelingSchemeInfo schemeInfo = new ContainerLabelingSchemeInfo();
        schemeInfo.setLabelingScheme((ContainerLabelingScheme) context.getSession()
            .createCriteria(ContainerLabelingScheme.class)
            .add(Restrictions.eq("name", schemeName)).uniqueResult());
        if (schemeInfo.getLabelingScheme() != null) {
            return schemeInfo;
        }
        return null;
    }

}
