package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.wrappers.base.SourceVesselBaseWrapper;
import edu.ualberta.med.biobank.model.SourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SourceVesselWrapper extends SourceVesselBaseWrapper {

    public SourceVesselWrapper(WritableApplicationService appService,
        SourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public SourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    public int compareTo(ModelWrapper<SourceVessel> wrapper) {
        if (wrapper instanceof SourceVesselWrapper) {
            SourceVesselWrapper svWrapper = (SourceVesselWrapper) wrapper;

            return nullSafeComparator(svWrapper.getId(), getId());
        }
        return 0;
    }

    @Override
    public String toString() {
        return getSourceVesselType().getName() + " " + getTimeDrawn();
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        checkProcessingEvent();
    }

    private void checkProcessingEvent() throws BiobankCheckException {
        ProcessingEventWrapper pevent = getProcessingEvent();
        if (pevent != null) {
            throw new BiobankCheckException(
                "Source vessel has a processing event. cannot be deleted.");
        }
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

    @Deprecated
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Deprecated
    public void checkUnique() throws ApplicationException {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public boolean isUsed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Deprecated
    public void setName(String name) {
        // TODO Auto-generated method stub

    }

    @Deprecated
    public static void persistSourceVessels(
        List<SourceVesselWrapper> addedOrModifiedSampleTypes,
        List<SourceVesselWrapper> deletedSampleTypes) {
        // TODO Auto-generated method stub

    }

}
