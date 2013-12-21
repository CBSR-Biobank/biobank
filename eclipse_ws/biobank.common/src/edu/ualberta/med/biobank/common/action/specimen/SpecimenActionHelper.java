package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;
import java.util.Set;
import java.util.Stack;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.DispatchSpecimen;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.ShipmentInfo;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class SpecimenActionHelper {
    private static final Bundle bundle = new CommonBundle();

    public static void setParent(Specimen specimen, Specimen parentSpecimen) {
        if (parentSpecimen != null) {
            specimen.setCollectionEvent(parentSpecimen.getCollectionEvent());
            specimen.setParentSpecimen(parentSpecimen);
        }
        Specimen topSpecimen = (parentSpecimen == null)
            ? specimen : parentSpecimen.getTopSpecimen();
        specimen.setTopSpecimen(topSpecimen);
        if (specimen.equals(parentSpecimen)) { // parent to itself
            specimen.setOriginalCollectionEvent(specimen.getCollectionEvent());
        }
    }

    public static void setQuantityFromType(Specimen specimen) {
        Study study = specimen.getCollectionEvent().getPatient().getStudy();
        Collection<AliquotedSpecimen> aliquotedSpecimenCollection = study
            .getAliquotedSpecimens();
        if (aliquotedSpecimenCollection != null)
            // FIXME query
            for (AliquotedSpecimen as : aliquotedSpecimenCollection)
                if (specimen.getSpecimenType().equals(as.getSpecimenType())) {
                    specimen.setQuantity(as.getVolume());
                    return;
                }
    }

    @SuppressWarnings("nls")
    public static void createOrChangePosition(Specimen specimen,
        Container container, RowColPos rcp) {
        if (container == null) {
            throw new NullPointerException("container is null");
        }
        if (rcp == null) {
            throw new NullPointerException("rcp is null");
        }

        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos == null) {
            pos = new SpecimenPosition();
            pos.setSpecimen(specimen);
            specimen.setSpecimenPosition(pos);
        }

        pos.setRow(rcp.getRow());
        pos.setCol(rcp.getCol());

        pos.setContainer(container);
        ContainerType type = container.getContainerType();
        String positionString = ContainerLabelingScheme.getPositionString(
            rcp, type.getChildLabelingScheme().getId(), type.getCapacity().getRowCapacity(),
            type.getCapacity().getColCapacity(), type.getLabelingLayout());
        pos.setPositionString(positionString);
    }

    @SuppressWarnings("nls")
    public static void setPosition(ActionContext actionContext,
        Specimen specimen, RowColPos rcp, Integer containerId) {
        // FIXME check if a position exists?
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if ((pos != null) && (rcp == null) && (containerId == null)) {
            specimen.setSpecimenPosition(null);
            // FIXME not sure this will work. Needs to be tested.
            actionContext.getSession().delete(pos);
        }

        if (rcp != null && containerId != null) {
            Container container = actionContext.load(Container.class,
                containerId);
            createOrChangePosition(specimen, container, rcp);
        } else if ((rcp == null && containerId != null)
            || (rcp != null && containerId == null)) {
            throw new LocalizedException(
                bundle
                    .tr("Parent container and position should either both be set or both be null")
                    .format());
        }
    }

    @SuppressWarnings("nls")
    public static String getPositionString(
        Specimen specimen,
        boolean fullString,
        boolean addTopParentShortName) {

        if (specimen.getSpecimenPosition() == null)
            return null;

        StringBuffer position = new StringBuffer();

        position.append(specimen.getSpecimenPosition().getPositionString());
        if (fullString) {
            position.insert(0, specimen.getSpecimenPosition().getContainer().getLabel());
        }
        if (addTopParentShortName) {
            position.append(" (");
            position.append(specimen.getSpecimenPosition().getContainer()
                .getContainerType().getNameShort());
            position.append(")");
        }
        return position.toString();
    }

    /**
     * Returns the "brief" information for a specimen. It is meant to only be called by actions.
     * 
     * @param context The context the action is running under.
     * @param specimenId The specimen id. Can be null.
     * @param inventoryId The inventory id. Can be null.
     * @return The specimen informaiton along with many of its associations.
     */
    @SuppressWarnings("nls")
    public static SpecimenBriefInfo getSpecimenBriefInfo(
        ActionContext context,
        Integer specimenId,
        String inventoryId) {
        Criteria criteria = context.getSession().createCriteria(Specimen.class);

        if ((specimenId == null) && (inventoryId == null)) {
            throw new IllegalArgumentException("specimen id and inventory id cannot both be null");
        }

        if (specimenId != null) {
            criteria.add(Restrictions.eq("id", specimenId));
        }

        if (inventoryId != null) {
            criteria.add(Restrictions.eq("inventoryId", inventoryId));
        }

        Specimen specimen = (Specimen) criteria.uniqueResult();

        if (specimen == null) return null;

        // lazy load some associations
        specimen.getTopSpecimen().getSpecimenType().getName();
        specimen.getSpecimenType().getName();
        specimen.getCurrentCenter().getName();
        specimen.getOriginInfo().getCenter().getName();
        specimen.getCollectionEvent().getPatient().getPnumber();
        specimen.getCollectionEvent().getPatient().getStudy().getName();

        SpecimenPosition specimenPosition = specimen.getSpecimenPosition();
        if (specimenPosition != null) {
            specimenPosition.getRow();
            specimenPosition.getContainer().getLabel();
        }

        ShipmentInfo shipmentInfo = specimen.getOriginInfo().getShipmentInfo();
        if (shipmentInfo != null) {
            shipmentInfo.getShippingMethod().getName();
        }

        Set<DispatchSpecimen> dispatchSpecimens = specimen.getDispatchSpecimens();
        if (dispatchSpecimens != null) {
            for (DispatchSpecimen ds : dispatchSpecimens) {
                ds.getDispatch().getSenderCenter().getName();
            }
        }

        Set<Specimen> childSpecimens = specimen.getChildSpecimens();
        for (Specimen child : childSpecimens) {
            child.getSpecimenType().getName();
        }

        Set<Comment> comments = specimen.getComments();
        for (Comment comment : comments) {
            comment.getUser().getFullName();
        }

        ProcessingEvent pevent = specimen.getProcessingEvent();
        if (pevent != null) {
            specimen.getProcessingEvent().getCenter().getName();
        }
        Specimen topSpecimen = specimen.getTopSpecimen();
        if (topSpecimen != null) {
            ProcessingEvent topPevent = topSpecimen.getProcessingEvent();
            if (topPevent != null) {
                topPevent.getCenter();
            }
            topSpecimen.getOriginInfo().getCenter().getName();
        }
        Specimen parentSpecimen = specimen.getParentSpecimen();
        if (parentSpecimen != null) {
            ProcessingEvent parentPevent = parentSpecimen.getProcessingEvent();
            if (parentPevent != null) {
                parentPevent.getCenter();
            }
            parentSpecimen.getOriginInfo().getCenter().getName();
        }

        // get all parent containers - can be used for visualisation
        Stack<Container> parents = new Stack<Container>();
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null) {
            Container container = pos.getContainer();
            while (container != null) {
                container.getContainerType().getChildLabelingScheme().getName();
                container.getContainerType().getCapacity().getRowCapacity();
                parents.push(container);
                container = container.getParentContainer();
            }
        }

        BatchOperation batch = (BatchOperation) context.getSession()
            .createCriteria(BatchOperationSpecimen.class)
            .add(Restrictions.eq("specimen.id", specimen.getId())).uniqueResult();

        return new SpecimenBriefInfo(specimen, parents, batch);
    }
}
