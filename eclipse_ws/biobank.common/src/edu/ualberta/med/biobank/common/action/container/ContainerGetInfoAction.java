package edu.ualberta.med.biobank.common.action.container;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.common.action.ListResult;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.permission.container.ContainerReadPermission;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class ContainerGetInfoAction implements Action<ListResult<Container>> {
    private static final long serialVersionUID = 1L;

    // private static Logger log = LoggerFactory
    // .getLogger(ContainerGetInfoAction.class.getName());

    public static class ContainerData implements ActionResult {
        private static final long serialVersionUID = 1L;
        public Container container;
    }

    private final Integer siteId;

    private final Integer containerId;

    private final String label;

    private final String productBarcode;

    /**
     * Can retrieve a container by it's ID, label, or product barcode.
     * 
     * @param container The container to return. If the ID field is not null then the container with
     *            the corresponding ID is returned. The same for the label and product barcode
     *            fields.
     * @param site The site the container belongs to. Can be null.
     */
    @SuppressWarnings("nls")
    public ContainerGetInfoAction(Container container, Site site) {
        this.containerId = container.getId();
        this.label = container.getLabel();
        this.productBarcode = container.getProductBarcode();
        this.siteId = (site == null) ? null : site.getId();

        if ((this.containerId == null) && (this.siteId == null)) {
            throw new IllegalArgumentException("both container ID and site ID cannot be null");
        }
    }

    /**
     * Can retrieve a container by it's ID, label, or product barcode.
     * 
     * @param container The container to return. If the ID field is not null then the container with
     *            the corresponding ID is returned. The same for the label and product barcode
     *            fields.
     */
    public ContainerGetInfoAction(Container container) {
        this(container, null);
    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        Integer siteId = this.siteId;
        if (siteId == null) {
            Container c = context.load(Container.class, containerId);
            siteId = c.getSite().getId();
        }
        return new ContainerReadPermission(siteId).isAllowed(context);
    }

    @SuppressWarnings("nls")
    @Override
    public ListResult<Container> run(ActionContext context) throws ActionException {
        Criteria criteria = context.getSession().createCriteria(Container.class);

        if (this.containerId != null) {
            criteria.add(Restrictions.eq("id", this.containerId));
        }

        if (this.label != null) {
            criteria.add(Restrictions.eq("label", this.label));
        }

        if (this.productBarcode != null) {
            criteria.add(Restrictions.eq("productBarcode", this.productBarcode));
        }

        if (this.siteId != null) {
            criteria.add(Restrictions.eq("site.id", this.siteId));
        }

        @SuppressWarnings("unchecked")
        List<Container> containers = criteria.list();

        // load associations
        for (Container container : containers) {
            ContainerType ctype = container.getContainerType();
            ctype.getChildContainerTypes().size();
            ctype.getSpecimenTypes().size();
            ctype.getChildLabelingScheme().getMaxCapacity();
            container.getPosition();
            container.getTopContainer();
            container.getSite().getName();

            ContainerType parentCtype = container.getTopContainer().getContainerType();
            parentCtype.getChildLabelingScheme().getMaxCapacity();

            ContainerPosition containerPos = container.getPosition();

            if (containerPos != null) {
                parentCtype = containerPos.getParentContainer().getContainerType();
                parentCtype.getChildLabelingScheme().getMaxCapacity();
                parentCtype.getChildContainerTypes().size();
            }

            for (ContainerPosition pos : container.getChildPositions()) {
                pos.getContainer().getLabel();
            }

            for (SpecimenPosition pos : container.getSpecimenPositions()) {
                Specimen specimen = pos.getSpecimen();
                specimen.getParentSpecimen().getInventoryId();
                specimen.getCollectionEvent().getPatient().getStudy().getName();
                ProcessingEvent pevent = specimen.getProcessingEvent();
                if (pevent != null) {
                    pevent.getWorksheet();
                }
                specimen.getOriginInfo().getCenter().getName();

                for (Comment comment : specimen.getComments()) {
                    comment.getUser().getLogin();
                }
            }

            for (Comment comment : container.getComments()) {
                comment.getUser().getLogin();
            }
        }

        return new ListResult<Container>(containers);
    }

}
