package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerLabelingSchemeWrapper;
import edu.ualberta.med.biobank.model.AliquotedSpecimen;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.Study;

public class SpecimenActionHelper {

    public static void setParent(Session session, Specimen specimen,
        Integer parentSpecimenId) {
        Specimen parent = null;
        if (parentSpecimenId != null) {
            parent = ActionUtil.sessionGet(session, Specimen.class,
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
            .getAliquotedSpecimenCollection();
        if (aliquotedSpecimenCollection != null)
            // FIXME query
            for (AliquotedSpecimen as : aliquotedSpecimenCollection)
                if (specimen.getSpecimenType().equals(as.getSpecimenType())) {
                    specimen.setQuantity(as.getVolume());
                    return;
                }
    }

    public static void setPosition(Session session, Specimen specimen,
        RowColPos rcp, Integer containerId) {
        // FIXME check if a position exists?
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null && rcp == null && containerId == null) {
            specimen.setSpecimenPosition(null);
            // FIXME not sure this will work. Needs to be tested.
            session.delete(pos);
        }
        if (rcp != null && containerId != null) {
            if (pos == null) {
                pos = new SpecimenPosition();
                pos.setSpecimen(specimen);
                specimen.setSpecimenPosition(pos);
            }
            pos.setRow(rcp.getRow());
            pos.setCol(rcp.getCol());

            Container container = ActionUtil.sessionGet(session,
                Container.class, containerId);
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
                "Problem: position and parent container should be both null or both set"); //$NON-NLS-1$
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
