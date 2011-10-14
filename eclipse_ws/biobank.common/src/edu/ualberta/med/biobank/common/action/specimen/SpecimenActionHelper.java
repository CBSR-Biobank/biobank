package edu.ualberta.med.biobank.common.action.specimen;

import java.util.Collection;

import org.hibernate.Session;

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
        RowColPos rcp, Integer containerId) {
        // FIXME check if a position exists?
        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null && rcp == null && containerId == null) {
            specimen.setSpecimenPosition(null);
            session.delete(pos);
        }
        if (rcp != null && containerId != null) {
            if (pos == null) {
                pos = new SpecimenPosition();
                pos.setSpecimen(specimen);
                specimen.setSpecimenPosition(pos);
            }
            pos.setCol(rcp.getCol());
            Container container = (Container) session.get(Container.class,
                containerId);
            pos.setContainer(container);
            ContainerType type = container.getContainerType();
            String positionString = ContainerLabelingSchemeWrapper
                .getPositionString(rcp, type.getChildLabelingScheme().getId(),
                    type.getCapacity().getRowCapacity(), type.getCapacity()
                        .getColCapacity());
            pos.setPositionString(positionString);
            pos.setRow(rcp.getRow());
        }
    }
}
