package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
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
        String positionString = ContainerLabelingSchemeWrapper
            .getPositionString(rcp, type.getChildLabelingScheme().getId(),
                type.getCapacity().getRowCapacity(), type.getCapacity()
                    .getColCapacity());
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

    public static String getPositionString(Specimen specimen,
        boolean fullString, boolean addTopParentShortName) {
        if (specimen.getSpecimenPosition() == null)
            return null;

        String position = specimen.getSpecimenPosition().getPositionString();
        if (fullString) {
            position = specimen.getSpecimenPosition().getContainer().getLabel()
                + position;
        }
        if (addTopParentShortName)
            position += " (" //$NON-NLS-1$ 
                + specimen.getSpecimenPosition().getContainer()
                    .getContainerType().getNameShort() + ")"; //$NON-NLS-1$
        return position;
    }
}
