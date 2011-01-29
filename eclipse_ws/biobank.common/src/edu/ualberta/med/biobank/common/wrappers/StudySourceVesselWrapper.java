package edu.ualberta.med.biobank.common.wrappers;

import java.util.List;

import edu.ualberta.med.biobank.common.peer.StudySourceVesselPeer;
import edu.ualberta.med.biobank.model.SourceVessel;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudySourceVessel;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class StudySourceVesselWrapper extends ModelWrapper<StudySourceVessel> {

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
    protected List<String> getPropertyChangeNames() {
        return StudySourceVesselPeer.PROP_NAMES;
    }

    @Override
    public Class<StudySourceVessel> getWrappedClass() {
        return StudySourceVessel.class;
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
        StudyWrapper study = (StudyWrapper) propertiesMap.get("study");
        if (study == null) {
            Study s = wrappedObject.getStudy();
            if (s == null)
                return null;
            study = new StudyWrapper(appService, s);
            propertiesMap.put("study", study);
        }
        return study;
    }

    public void setStudy(StudyWrapper study) {
        propertiesMap.put("study", study);
        Study oldStudy = wrappedObject.getStudy();
        Study newStudy = study.wrappedObject;
        wrappedObject.setStudy(newStudy);
        propertyChangeSupport.firePropertyChange("study", oldStudy, newStudy);
    }

    public SourceVesselWrapper getSourceVessel() {
        SourceVesselWrapper ss = (SourceVesselWrapper) propertiesMap
            .get("sourceVessel");
        if (ss == null) {
            SourceVessel s = wrappedObject.getSourceVessel();
            if (s == null) {
                return null;
            }
            ss = new SourceVesselWrapper(appService, s);
            propertiesMap.put("sourceVessel", ss);
        }

        return ss;
    }

    public void setSourceVessel(SourceVesselWrapper ss) {
        propertiesMap.put("sourceVessel", ss);
        SourceVessel oldSs = wrappedObject.getSourceVessel();
        SourceVessel newSs = null;
        if (ss != null) {
            newSs = ss.getWrappedObject();
        }
        wrappedObject.setSourceVessel(newSs);
        propertyChangeSupport.firePropertyChange("sourceVessel", oldSs, newSs);
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
