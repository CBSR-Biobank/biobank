package edu.ualberta.med.biobank.common.wrappers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SampleSourceWrapper extends ModelWrapper<SampleSource> implements
    Comparable<SampleSourceWrapper> {

    public SampleSourceWrapper(WritableApplicationService appService,
        SampleSource wrappedObject) {
        super(appService, wrappedObject);
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

    public Collection<StudyWrapper> getStudyCollection() {
        Collection<StudyWrapper> wrapperCollection = new HashSet<StudyWrapper>();
        Collection<Study> collection = wrappedObject.getStudyCollection();
        if (collection != null)
            for (Study study : collection) {
                wrapperCollection.add(new StudyWrapper(appService, study));
            }
        return wrapperCollection;
    }

    public List<StudyWrapper> getStudyCollectionSorted() {
        List<StudyWrapper> list = new ArrayList<StudyWrapper>(
            getStudyCollection());
        if (list.size() > 1) {
            Collections.sort(list);
        }
        return list;
    }

    @Override
    protected Class<SampleSource> getWrappedClass() {
        return SampleSource.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
    }

    public int compareTo(SampleSourceWrapper wrapper) {
        String myName = wrappedObject.getName();
        String wrapperName = wrapper.wrappedObject.getName();
        return ((myName.compareTo(wrapperName) > 0) ? 1 : (myName
            .equals(wrapperName) ? 0 : -1));
    }

}
