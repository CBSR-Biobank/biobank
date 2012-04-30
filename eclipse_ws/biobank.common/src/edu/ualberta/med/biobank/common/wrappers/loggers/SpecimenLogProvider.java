package edu.ualberta.med.biobank.common.wrappers.loggers;

import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Log;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;

public class SpecimenLogProvider implements WrapperLogProvider<Specimen> {
    private static final long serialVersionUID = 1L;

    @Override
    public Log getLog(Specimen specimen) {
        Log log = new Log();

        Center currentCenter = specimen.getCurrentCenter();
        if (currentCenter != null) {
            log.setCenter(currentCenter.getNameShort());
        }

        CollectionEvent collectionEvent = specimen.getCollectionEvent();
        if (collectionEvent != null) {
            Patient patient = collectionEvent.getPatient();
            if (patient != null) {
                log.setPatientNumber(patient.getPnumber());
            }
        }

        log.setInventoryId(specimen.getInventoryId());

        SpecimenPosition pos = specimen.getSpecimenPosition();
        if (pos != null) {
            Container container = pos.getContainer();
            if (container != null) {
                String locationLabel = container.getLabel()
                    + pos.getPositionString();
                log.setLocationLabel(locationLabel);
            }
        }

        return log;
    }

    @Override
    public Log getObjectLog(Object model) {
        if (model instanceof Specimen)
            return getLog((Specimen) model);
        if (model instanceof SpecimenPosition)
            return getLog(((SpecimenPosition) model).getSpecimen());
        return null;
    }
}
