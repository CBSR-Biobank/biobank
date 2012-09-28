package edu.ualberta.med.biobank.common.action.container;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Container;

public class ContainerGetInfoByLabelAction implements
    Action<ListResult<Container>> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory
        .getLogger(ContainerGetInfoByLabelAction.class.getName());

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
            + " LEFT JOIN FETCH parentCtype.childLabelingScheme"
            + " LEFT JOIN FETCH parentCtype.childContainerTypes"
            + " WHERE (container.label = ? OR container.label = ?"
            + " OR container.label = ?) AND container.site.id=?";

    private final String label;

    private final Integer siteId;

    @SuppressWarnings("nls")
    public ContainerGetInfoByLabelAction(String label, Integer siteId) {
        log.debug("containerId={}", label);
        if (label == null) {
            throw new IllegalArgumentException();
        }
        this.label = label;
        this.siteId = siteId;
    }

    @SuppressWarnings("nls")
    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        boolean result = new ContainerReadPermission(siteId)
            .isAllowed(context);
        log.debug("isAllowed: containerId={} allowed={}", label, result);
        return result;
    }

    @SuppressWarnings({ "nls", "unchecked" })
    @Override
    public ListResult<Container> run(ActionContext context)
        throws ActionException {
        log.debug("run: containerId={}", label);

        // FIXME: this is hideous. We need delimeters instead of guesswork

        Query query = context.getSession().createQuery(CONTAINER_INFO_HQL);
        query.setParameter(0, label);
        query.setParameter(1, label.substring(0, label.length() - 2));
        query.setParameter(2, label.substring(0, label.length() - 3));
        query.setParameter(3, siteId);
        return new ListResult<Container>(query.list());
    }

}
