package edu.ualberta.med.biobank.common.action.container;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;

public class ContainerGetInfoAction implements Action<ContainerInfo> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(ContainerGetInfoAction.class.getName());

    // this query is ridiculous!
    @SuppressWarnings("nls")
    private static final String CONTAINER_INFO_HQL =
        "SELECT DISTINCT container"
            + " FROM " + Container.class.getName() + " container"
            + " INNER JOIN FETCH container.containerType ctype"
            + " LEFT JOIN FETCH ctype.childContainerTypes"
            + " LEFT JOIN FETCH ctype.childLabelingScheme"
            + " LEFT JOIN FETCH ctype.specimenTypes"
            + " LEFT JOIN FETCH container.position"
            + " INNER JOIN FETCH container.topContainer topContainer"
            + " INNER JOIN FETCH topContainer.containerType topContainerType"
            + " INNER JOIN FETCH topContainerType.childLabelingScheme"
            + " INNER JOIN FETCH container.site"
            + " LEFT JOIN FETCH container.childPositions childPos"
            + " LEFT JOIN FETCH childPos.container"
            + " LEFT JOIN FETCH container.specimenPositions spcPos"
            + " LEFT JOIN FETCH spcPos.specimen specimen"
            + " LEFT JOIN FETCH specimen.parentSpecimen parentSpecimen"
            + " LEFT JOIN FETCH specimen.collectionEvent cevent"
            + " LEFT JOIN FETCH cevent.patient patient"
            + " LEFT JOIN FETCH patient.study"
            + " LEFT JOIN FETCH parentSpecimen.processingEvent"
            + " LEFT JOIN FETCH container.comments containerComments"
            + " LEFT JOIN FETCH containerComments.user"
            + " LEFT JOIN FETCH specimen.comments"
            + " LEFT JOIN FETCH specimen.originInfo spcOriginInfo"
            + " LEFT JOIN FETCH spcOriginInfo.center"
            + " LEFT JOIN FETCH container.position position"
            + " LEFT JOIN FETCH position.parentContainer parentContainer"
            + " LEFT JOIN FETCH parentContainer.containerType parentCtype"
            + " LEFT JOIN FETCH parentCtype.capacity"
            + " LEFT JOIN FETCH parentCtype.childLabelingScheme"
            + " LEFT JOIN FETCH parentCtype.childContainerTypes"
            + " WHERE container.id = ?";

    public static class ContainerInfo implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Container container;
    }

    private final Integer containerId;

    public ContainerGetInfoAction(Integer containerId) {
        log.debug("containerId={}", containerId);
        if (containerId == null) {
            throw new IllegalArgumentException();
        }
        this.containerId = containerId;
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {

        Container c = context.load(Container.class, containerId);
        boolean result = new ContainerReadPermission(c.getSite().getId())
            .isAllowed(context);
        log.debug("isAllowed: containerId={} allowed={}", containerId, result);
        return result;
    }

    @Override
    public ContainerInfo run(ActionContext context) throws ActionException {
        log.debug("run: containerId={}", containerId);

        ContainerInfo containerInfo = new ContainerInfo();
        Query query = context.getSession().createQuery(CONTAINER_INFO_HQL);
        query.setParameter(0, containerId);

        containerInfo.container = (Container) query.uniqueResult();
        if (containerInfo.container == null) {
            throw new IllegalStateException("container is null");
        }

        return containerInfo;
    }

}
