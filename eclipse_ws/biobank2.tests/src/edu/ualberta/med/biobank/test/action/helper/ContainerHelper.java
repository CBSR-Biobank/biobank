package edu.ualberta.med.biobank.test.action.helper;

import java.util.Set;

import org.hibernate.Session;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerLabelingScheme;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;

public class ContainerHelper {

    public static SpecimenPosition placeSpecimenInContainer(Session session,
                                                            Specimen specimen,
                                                            Container container) {
        ContainerType ctype = container.getContainerType();

        SpecimenType specimenType = specimen.getSpecimenType();
        Set<SpecimenType> specimenTypes = ctype.getSpecimenTypes();
        if (!specimenTypes.contains(specimenType)) {
            ctype.getSpecimenTypes().add(specimenType);
            session.update(ctype);
            session.flush();
        }

        RowColPos emptyPosition = getEmptyPosition(container);
        SpecimenPosition specimenPosition = new SpecimenPosition();
        specimenPosition.setRow(emptyPosition.getRow());
        specimenPosition.setCol(emptyPosition.getCol());
        specimenPosition.setSpecimen(specimen);
        specimenPosition.setContainer(container);
        specimen.setSpecimenPosition(specimenPosition);
        container.getSpecimenPositions().add(specimenPosition);

        String positionString = ContainerLabelingScheme.getPositionString(
            emptyPosition,
            ctype.getChildLabelingScheme().getId(),
            ctype.getCapacity().getRowCapacity(),
            ctype.getCapacity().getColCapacity(),
            ctype.getLabelingLayout());
        specimenPosition.setPositionString(positionString);
        return specimenPosition;
    }

    /*
     * Returns the first empty position
     */
    public static RowColPos getEmptyPosition(Container container) {
        ContainerType ctype = container.getContainerType();
        Integer rowCap = ctype.getCapacity().getRowCapacity();
        Integer colCap = ctype.getCapacity().getColCapacity();
        for (int row = 0; row < rowCap; ++row) {
            for (int col = 0; col < colCap; ++col) {
                RowColPos pos = new RowColPos(row, col);
                if (container.isPositionFree(pos)) {
                    return pos;
                }
            }
        }
        return null;
    }

}
