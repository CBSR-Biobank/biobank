package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleSourceWrapper extends ModelWrapper<SourceVessel> {

    public SampleSourceWrapper(WritableApplicationService appService,
        SourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public SampleSourceWrapper(WritableApplicationService appService) {
        super(appService);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public void setName(String name) {
        String oldName = getName();
        wrappedObject.setName(name);
        propertyChangeSupport.firePropertyChange("name", oldName, name);
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "name" };
    }

    /**
     * Get study list. Use Study.setSourceVesselCollection to link studies and
     * sample sources
     */
    @SuppressWarnings("unchecked")
    public Collection<StudyWrapper> getStudyCollection(boolean sort) {
        List<StudyWrapper> clinicCollection = (List<StudyWrapper>) propertiesMap
            .get("studyCollection");
        if (clinicCollection == null) {
            Collection<Study> children = wrappedObject.getStudyCollection();
            if (children != null) {
                clinicCollection = new ArrayList<StudyWrapper>();
                for (Study study : children) {
                    clinicCollection.add(new StudyWrapper(appService, study));
                }
                propertiesMap.put("studyCollection", clinicCollection);
            }
        }
        if ((clinicCollection != null) && sort)
            Collections.sort(clinicCollection);
        return clinicCollection;
    }

    @Override
    public Class<SourceVessel> getWrappedClass() {
        return SourceVessel.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public static List<SampleSourceWrapper> getAllSampleSources(
        WritableApplicationService appService) throws ApplicationException {
        List<SourceVessel> list = appService.search(SourceVessel.class,
            new SourceVessel());
        List<SampleSourceWrapper> wrappers = new ArrayList<SampleSourceWrapper>();
        for (SourceVessel ss : list) {
            wrappers.add(new SampleSourceWrapper(appService, ss));
        }
        return wrappers;
    }

    @Override
    public int compareTo(ModelWrapper<SourceVessel> wrapper) {
        if (wrapper instanceof SampleSourceWrapper) {
            String name1 = wrappedObject.getName();
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
}
