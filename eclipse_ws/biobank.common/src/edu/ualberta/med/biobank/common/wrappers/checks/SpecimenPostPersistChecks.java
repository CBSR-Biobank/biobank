package edu.ualberta.med.biobank.common.wrappers.checks;

import java.text.MessageFormat;

import org.hibernate.Session;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.actions.LoadModelAction;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.server.applicationservice.exceptions.BiobankSessionException;

public class SpecimenPostPersistChecks extends LoadModelAction<Specimen> {
    private static final long serialVersionUID = 1L;

    private static final String WRONG_COLLECTION_EVENT = "Specimen {0} has a Collection Event which differs from it's parent or top Specimen.";
    private static final String TOP_SPECIMEN_COLLECTION_EVENT_UNSET = "Specimen {0} is a top Specimen, but not part of its Collection Event's original specimen collection.";

    public SpecimenPostPersistChecks(ModelWrapper<Specimen> wrapper) {
        super(wrapper);
    }

    @Override
    public void doLoadModelAction(Session session, Specimen specimen)
        throws BiobankSessionException {
        checkSameCollectionEvent(specimen);
        checkOriginalCollectionEvent(specimen);
    }

    private static void checkSameCollectionEvent(Specimen specimen)
        throws BiobankSessionException {
        CollectionEvent collectionEvent = specimen.getCollectionEvent();
        Specimen topSpecimen = specimen.getTopSpecimen();
        if (topSpecimen != null && topSpecimen.getCollectionEvent() != null
            && !topSpecimen.getCollectionEvent().equals(collectionEvent)) {
            String msg = MessageFormat.format(WRONG_COLLECTION_EVENT,
                specimen.getInventoryId());
            throw new BiobankSessionException(msg);
        }
    }

    private static void checkOriginalCollectionEvent(Specimen specimen)
        throws BiobankSessionException {
        boolean isTopSpecimen = specimen.equals(specimen.getTopSpecimen());

        if (isTopSpecimen && specimen.getCollectionEvent() != null
            && specimen.getOriginalCollectionEvent() == null) {
            String msg = MessageFormat.format(
                TOP_SPECIMEN_COLLECTION_EVENT_UNSET, specimen.getInventoryId());
            throw new BiobankSessionException(msg);
        }
    }
}
