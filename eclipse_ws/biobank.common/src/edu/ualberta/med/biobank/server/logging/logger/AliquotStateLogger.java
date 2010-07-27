package edu.ualberta.med.biobank.server.logging.logger;

import java.util.Map;

import edu.ualberta.med.biobank.common.util.LabelingScheme;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.model.Aliquot;
import edu.ualberta.med.biobank.model.AliquotPosition;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.PatientVisit;

public class AliquotStateLogger extends BiobankObjectStateLogger {

    protected AliquotStateLogger() {

    }

    @Override
    protected Log getLogObject(Object obj, Map<String, Object> statesMap) {
        if (obj instanceof Aliquot) {
            Log log = new Log();
            PatientVisit visit = (PatientVisit) statesMap.get("patientVisit");
            log.setSite(visit.getPatient().getStudy().getSite().getNameShort());
            log.setPatientNumber(visit.getPatient().getPnumber());
            log.setInventoryId((String) statesMap.get("inventoryId"));
            AliquotPosition pos = (AliquotPosition) statesMap
                .get("aliquotPosition");
            if (pos != null) {
                Container parent = pos.getContainer();
                if (parent != null) {
                    ContainerType type = parent.getContainerType();
                    Capacity capacity = type.getCapacity();
                    log.setLocationLabel(parent.getLabel()
                        + LabelingScheme.getPositionString(
                            new RowColPos(pos.getRow(), pos.getCol()), type
                                .getChildLabelingScheme().getId(), capacity
                                .getRowCapacity(), capacity.getColCapacity())
                        + " (" + type.getNameShort() + ")");
                }
            }
            log.setType("Aliquot");
            return log;
        }
        return null;
    }
}
