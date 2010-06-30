package edu.ualberta.med.biobank.common.wrappers;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudySourceVesselWrapper extends ModelWrapper<StudySourceVessel> {

    private StudyWrapper study;
    private SourceVesselWrapper ss;

    public StudySourceVesselWrapper(WritableApplicationService appService,
        StudySourceVessel wrappedObject) {
        super(appService, wrappedObject);
    }

    public StudySourceVesselWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected void deleteChecks() throws Exception {
    }

    @Override
    protected String[] getPropertyChangeNames() {
        return new String[] { "sourceVessel", "study", "needTimeDrawn",
            "needOriginalVolume" };
    }

    @Override
    public Class<StudySourceVessel> getWrappedClass() {
        return StudySourceVessel.class;
    }

    @Override
    protected void persistChecks() throws BiobankCheckException,
        ApplicationException, WrapperException {
    }

    @Override
    public int compareTo(ModelWrapper<StudySourceVessel> o) {
        if (o instanceof StudySourceVesselWrapper) {
            return getSourceVessel().compareTo(
                ((StudySourceVesselWrapper) o).getSourceVessel());
        }
        return 0;
    }

    public StudyWrapper getStudy() {
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        this.study = study;
        Study oldStudy = wrappedObject.getStudy();
        Study newStudy = study.wrappedObject;
        wrappedObject.setStudy(newStudy);
        propertyChangeSupport.firePropertyChange("study", oldStudy, newStudy);
    }

    public SourceVesselWrapper getSourceVessel() {
        if (ss == null) {
            SourceVessel s = wrappedObject.getSourceVessel();
            if (s == null) {
                return null;
            }
            ss = new SourceVesselWrapper(appService, s);
        }

        return ss;
    }

    protected void setSourceVessel(SourceVessel ss) {
        if (ss == null)
            this.ss = null;
        else
            this.ss = new SourceVesselWrapper(appService, ss);
        SourceVessel oldSs = wrappedObject.getSourceVessel();
        wrappedObject.setSourceVessel(ss);
        propertyChangeSupport.firePropertyChange("sourceVessel", oldSs, ss);
    }

    public void setSourceVessel(SourceVesselWrapper ss) {
        if (ss == null) {
            setSourceVessel((SourceVessel) null);
        } else {
            setSourceVessel(ss.getWrappedObject());
        }
    }

    public void setNeedTimeDrawn(Boolean needTimeDrawn) {
        Boolean oldNeedTimeDrawn = wrappedObject.getNeedTimeDrawn();
        wrappedObject.setNeedTimeDrawn(needTimeDrawn);
        propertyChangeSupport.firePropertyChange("needTimeDrawn",
            oldNeedTimeDrawn, needTimeDrawn);
    }

    public Boolean getNeedTimeDrawn() {
        return wrappedObject.getNeedTimeDrawn();
    }

    public void setNeedOriginalVolume(Boolean needOriginalVolume) {
        Boolean oldNeedOriginalVolume = wrappedObject.getNeedOriginalVolume();
        wrappedObject.setNeedOriginalVolume(needOriginalVolume);
        propertyChangeSupport.firePropertyChange("needOriginalVolume",
            oldNeedOriginalVolume, needOriginalVolume);
    }

    public Boolean getNeedOriginalVolume() {
        return wrappedObject.getNeedOriginalVolume();
    }

}
