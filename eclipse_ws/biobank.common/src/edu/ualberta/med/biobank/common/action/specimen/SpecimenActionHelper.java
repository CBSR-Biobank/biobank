package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class SpecimenActionHelper {

    public static void setParent(ActionContext actionContext,
        Specimen specimen,
        Integer parentSpecimenId) {
        Specimen parent = null;
        if (parentSpecimenId != null) {
            parent = actionContext.load(Specimen.class,
                parentSpecimenId);
            specimen.setCollectionEvent(parent.getCollectionEvent());
            specimen.setParentSpecimen(parent);
        }
        Specimen topSpecimen = parent == null ? specimen : parent
            .getTopSpecimen();
        specimen.setTopSpecimen(topSpecimen);
        if (specimen.equals(parent)) { // parent to itself
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
    public static void setPosition(ActionContext actionContext,
        Specimen specimen,
        RowColPos rcp, Integer containerId) {
        // FIXME check if a position exists?
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null && rcp == null && containerId == null) {
            specimen.setSpecimenPosition(null);
            // FIXME not sure this will work. Needs to be tested.
            actionContext.getSession().delete(pos);
        }
        if (rcp != null && containerId != null) {
            if (pos == null) {
                pos = new SpecimenPosition();
                pos.setSpecimen(specimen);
                specimen.setSpecimenPosition(pos);
            }
            pos.setRow(rcp.getRow());
            pos.setCol(rcp.getCol());

            Container container = actionContext.load(Container.class,
                containerId);
            pos.setContainer(container);
            ContainerType type = container.getContainerType();
            String positionString = ContainerLabelingSchemeWrapper
                .getPositionString(rcp, type.getChildLabelingScheme().getId(),
                    type.getCapacity().getRowCapacity(), type.getCapacity()
                        .getColCapacity());
            pos.setPositionString(positionString);
        } else if ((rcp == null && containerId != null)
            || (rcp != null && containerId == null)) {
            throw new ActionException(
                LString.tr("Parent container and position should either both be set or both be null"));
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
