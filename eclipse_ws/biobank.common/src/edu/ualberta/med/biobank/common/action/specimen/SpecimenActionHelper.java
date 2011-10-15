package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.action.ActionException;
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
            parent = (Specimen) session.get(Specimen.class, parentSpecimenId);
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
        RowColPos rcp, Container container) {
        // FIXME check if a position exists?
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null && rcp == null && container == null) {
            specimen.setSpecimenPosition(null);
            // FIXME not sure this will work. Needs to be tested.
            session.delete(pos);
        }
        if (rcp != null && container != null) {
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
        } else if ((rcp == null && container != null)
            || (rcp != null && container == null)) {
            throw new ActionException(
                "Problem: position and parent container should be both null or both set"); //$NON-NLS-1$
        }
    }
}
