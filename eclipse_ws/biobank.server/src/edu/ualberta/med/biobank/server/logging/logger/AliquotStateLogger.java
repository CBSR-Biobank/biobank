package edu.ualberta.med.biobank.server.logging.logger;

import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.util.LabelingScheme;
import edu.ualberta.med.biobank.util.RowColPos;

public class AliquotStateLogger extends BiobankObjectStateLogger {

    protected AliquotStateLogger() {

    }

    @Override
    protected Log getLogObject(Object obj) {
        if (obj instanceof Aliquot) {
            Aliquot aliquot = (Aliquot) obj;
            Log log = new Log();
            log.setPatientNumber(aliquot.getPatientVisit().getPatient()
                .getPnumber());
            log.setInventoryId(aliquot.getInventoryId());
            AliquotPosition pos = aliquot.getAliquotPosition();
            if (pos != null) {
                Container parent = pos.getContainer();
                if (parent != null) {
                    ContainerType type = parent.getContainerType();
                    Capacity capacity = type.getCapacity();
                    log.setLocationLabel(parent.getLabel()
                        + LabelingScheme.getPositionString(new RowColPos(pos
                            .getRow(), pos.getCol()), type
                            .getChildLabelingScheme().getId(), capacity
                            .getRowCapacity(), capacity.getColCapacity()));
                }
            }
            log.setType("Aliquot");
            return log;
        }
        return null;
    }
}
