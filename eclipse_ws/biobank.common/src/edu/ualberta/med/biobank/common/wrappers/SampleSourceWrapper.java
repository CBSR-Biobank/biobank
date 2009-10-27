package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleSourceWrapper extends ModelWrapper<SampleSource> {

    public SampleSourceWrapper(WritableApplicationService appService,
        SampleSource wrappedObject) {
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
    protected String[] getPropertyChangesNames() {
        return new String[] { "name" };
    }

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

    public void setStudyCollection(Collection<Study> studies, boolean setNull) {
        Collection<Study> oldStudies = wrappedObject.getStudyCollection();
        wrappedObject.setStudyCollection(studies);
        propertyChangeSupport.firePropertyChange("studyCollection", oldStudies,
            studies);
        if (setNull) {
            propertiesMap.put("studyCollection", null);
        }
    }

    public void setStudyCollection(List<StudyWrapper> studies) {
        Collection<Study> studyObjects = new HashSet<Study>();
        for (StudyWrapper study : studies) {
            studyObjects.add(study.getWrappedObject());
        }
        setStudyCollection(studyObjects, false);
        propertiesMap.put("studyCollection", studies);
    }

    @Override
    public Class<SampleSource> getWrappedClass() {
        return SampleSource.class;
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
        List<SampleSource> list = appService.search(SampleSource.class,
            new SampleSource());
        List<SampleSourceWrapper> wrappers = new ArrayList<SampleSourceWrapper>();
        for (SampleSource ss : list) {
            wrappers.add(new SampleSourceWrapper(appService, ss));
        }
        return wrappers;
    }

    @Override
    public int compareTo(ModelWrapper<SampleSource> wrapper) {
        String name1 = wrappedObject.getName();
        String name2 = wrapper.wrappedObject.getName();
        return ((name1.compareTo(name2) > 0) ? 1 : (name1.equals(name2) ? 0
            : -1));
    }
}
