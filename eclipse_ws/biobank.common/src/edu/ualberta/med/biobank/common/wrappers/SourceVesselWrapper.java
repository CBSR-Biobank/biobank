package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.peer.SourceVesselPeer;
import edu.ualberta.med.biobank.model.SourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceVesselWrapper extends ModelWrapper<SourceVessel> {

    public SourceVesselWrapper(WritableApplicationService appService,
        SourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public SourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public SourceVesselTypeWrapper getSourceVesselType() {
        return getWrappedProperty(SourceVesselPeer.SOURCE_VESSEL_TYPE,
            SourceVesselTypeWrapper.class);
    }

    public void setSourceVesselType(SourceVesselTypeWrapper sourceVesselType) {
        setWrappedProperty(SourceVesselPeer.SOURCE_VESSEL_TYPE,
            sourceVesselType);
    }

    public String getName() {
        return getSourceVesselType().getName();
    }

    public Date getTimeDrawn() {
        return getProperty(SourceVesselPeer.TIME_DRAWN);
    }

    public void setTimeDrawn(Date time) {
        setProperty(SourceVesselPeer.TIME_DRAWN, time);
    }

    public Double getVolume() {
        return getProperty(SourceVesselPeer.VOLUME);
    }

    public void setVolume(Double vol) {
        setProperty(SourceVesselPeer.VOLUME, vol);
    }

    public void setPatient(PatientWrapper patientWrapper) {
        setWrappedProperty(SourceVesselPeer.PATIENT, patientWrapper);
    }

    public PatientWrapper getPatient() {
        return getWrappedProperty(SourceVesselPeer.PATIENT,
            PatientWrapper.class);
    }

    public CollectionEventWrapper getCollectionEvent() {
        return getWrappedProperty(SourceVesselPeer.COLLECTION_EVENT,
            CollectionEventWrapper.class);
    }

    public void setCollectionEvent(CollectionEventWrapper ce) {
        setWrappedProperty(SourceVesselPeer.COLLECTION_EVENT, ce);
    }

    public static List<SourceVesselWrapper> getAllSourceVessels(
        WritableApplicationService appService) throws ApplicationException {
        List<SourceVessel> list = appService.search(SourceVessel.class,
            new SourceVessel());
        List<SourceVesselWrapper> wrappers = new ArrayList<SourceVesselWrapper>();
        for (SourceVessel ss : list) {
            wrappers.add(new SourceVesselWrapper(appService, ss));
        }
        return wrappers;
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return SourceVesselPeer.PROP_NAMES;
    }

    @Override
    public Class<SourceVessel> getWrappedClass() {
        return SourceVessel.class;
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        if (getSourceVesselType() == null) {
            throw new BiobankCheckException("A SourceVesselType is required.");
        }
    }

    @Override
    public int compareTo(ModelWrapper<SourceVessel> wrapper) {
        if (wrapper instanceof SourceVesselWrapper) {
            SourceVesselWrapper svWrapper = (SourceVesselWrapper) wrapper;
            String name1 = getName();
            String name2 = svWrapper.getName();

            return nullSafeComparator(name1, name2);
        }
        return 0;
    }

    @Override
    public String toString() {
        return getName();
    }

    public static void persistSourceVessels(
        List<SourceVesselWrapper> addedOrModifiedTypes,
        List<SourceVesselWrapper> typesToDelete) throws BiobankCheckException,
        Exception {
        if (addedOrModifiedTypes != null) {
            for (SourceVesselWrapper ss : addedOrModifiedTypes) {
                ss.persist();
            }
        }
        if (typesToDelete != null) {
            for (SourceVesselWrapper ss : typesToDelete) {
                ss.delete();
            }
        }
    }
}
