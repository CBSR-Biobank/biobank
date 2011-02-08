package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.exception.BiobankException;
import edu.ualberta.med.biobank.common.exception.BiobankQueryResultSizeException;
import edu.ualberta.med.biobank.common.peer.SourceVesselPeer;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class SourceVesselWrapper extends ModelWrapper<SourceVessel> {

    public SourceVesselWrapper(WritableApplicationService appService,
        SourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public SourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getName() {
        return getProperty(SourceVesselPeer.NAME);
    }

    public void setName(String name) {
        setProperty(SourceVesselPeer.NAME, name);
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

    @Override
    protected List<String> getPropertyChangeNames() {
        return SourceVesselPeer.PROP_NAMES;
    }

    @Override
    public Class<SourceVessel> getWrappedClass() {
        return SourceVessel.class;
    }

    @Override
    protected void deleteChecks() throws BiobankException, ApplicationException {
        if (isUsed())
            throw new BiobankCheckException(
                "Source vessel is in use. Please remove from all corresponding studies and patient visits before deleting.");
    }

    @Override
    protected void persistChecks() throws BiobankException,
        ApplicationException {
        checkUnique();
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
    public int compareTo(ModelWrapper<SourceVessel> wrapper) {
        if (wrapper instanceof SourceVesselWrapper) {
            String name1 = getName();
            String name2 = wrapper.wrappedObject.getName();
            return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
                : -1));
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

    public boolean isUsed() throws ApplicationException, BiobankException {
        String queryString = "select count(s) from "
            + StudySourceVessel.class.getName()
            + " as s where s.sourceVessel=?)";
        HQLCriteria c = new HQLCriteria(queryString,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results = appService.query(c);
        if (results.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        if (results.get(0) > 0) {
            return true;
        }
        String queryString2 = "select count(s) from "
            + SourceVessel.class.getName() + " as s where s.sourceVessel=?)";
        HQLCriteria c2 = new HQLCriteria(queryString2,
            Arrays.asList(new Object[] { wrappedObject }));
        List<Long> results2 = appService.query(c2);
        if (results2.size() != 1) {
            throw new BiobankQueryResultSizeException();
        }
        return results2.get(0) > 0;
    }

    public void checkUnique() throws ApplicationException, BiobankException {
        checkNoDuplicates(SourceVessel.class, SourceVesselPeer.NAME.getName(),
            getName(), "A source vessel with name");
    }

    public void setPatient(PatientWrapper patientWrapper) {
        setWrappedProperty(SourceVesselPeer.PATIENT, patientWrapper);
    }

    public PatientWrapper getPatient() {
        return getWrappedProperty(SourceVesselPeer.PATIENT,
            PatientWrapper.class);
    }
}
