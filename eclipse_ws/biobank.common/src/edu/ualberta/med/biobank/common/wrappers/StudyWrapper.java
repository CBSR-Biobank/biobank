package edu.ualberta.med.biobank.common.wrappers;

import java.util.Collection;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.model.PvInfo;
import edu.ualberta.med.biobank.model.SampleSource;
import edu.ualberta.med.biobank.model.SampleStorage;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudyWrapper extends ModelWrapper<Study> implements
    Comparable<StudyWrapper> {

    public StudyWrapper(WritableApplicationService appService,
        Study wrappedObject) {
        super(appService, wrappedObject);
    }

    public String getName() {
        return wrappedObject.getName();
    }

    public String getNameShort() {
        return wrappedObject.getNameShort();
    }

    public String getActivityStatus() {
        return wrappedObject.getActivityStatus();
    }

    public String getComment() {
        return wrappedObject.getComment();
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    protected void firePropertyChanges(Study oldWrappedObject,
        Study newWrappedObject) {
        // TODO Auto-generated method stub

    }

    @Override
    protected Class<Study> getWrappedClass() {
        return Study.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException, Exception {
        // TODO Auto-generated method stub
    }

    @Override
    public int compareTo(StudyWrapper arg0) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Collection<ContactWrapper> getContactWrapperCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<SampleStorage> getSampleStorageCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<SampleSource> getSampleSourceCollection() {
        // TODO Auto-generated method stub
        return null;
    }

    public Collection<PvInfo> getPvInfoCollection() {
        // TODO Auto-generated method stub
        return null;
    }

}
