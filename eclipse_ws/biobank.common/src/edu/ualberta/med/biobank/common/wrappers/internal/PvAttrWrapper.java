package edu.ualberta.med.biobank.common.wrappers.internal;

import java.util.List;

import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.peer.PvAttrPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.PvAttr;
import edu.ualberta.med.biobank.model.StudyPvAttr;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class PvAttrWrapper extends ModelWrapper<PvAttr> {

    private StudyPvAttrWrapper studyPvAttr;
    private ProcessingEventWrapper pv;

    public PvAttrWrapper(WritableApplicationService appService,
        PvAttr wrappedObject) {
        super(appService, wrappedObject);
    }

    public PvAttrWrapper(WritableApplicationService appService) {
        super(appService);
    }

    @Override
    protected List<String> getPropertyChangeNames() {
        return PvAttrPeer.PROP_NAMES;
    }

    @Override
    public Class<PvAttr> getWrappedClass() {
        return PvAttr.class;
    }

    @Override
    protected void deleteChecks() throws BiobankCheckException,
        ApplicationException {
    }

    public StudyPvAttrWrapper getStudyPvAttr() {
        if (studyPvAttr == null) {
            StudyPvAttr s = wrappedObject.getStudyPvAttr();
            if (s == null)
                return null;
            studyPvAttr = new StudyPvAttrWrapper(appService, s);
        }
        return studyPvAttr;
    }

    public void setStudyPvAttr(StudyPvAttrWrapper studyPvAttr) {
        this.studyPvAttr = studyPvAttr;
        StudyPvAttr oldPvAttr = wrappedObject.getStudyPvAttr();
        StudyPvAttr newPvAttr = null;
        if (studyPvAttr != null) {
            newPvAttr = studyPvAttr.getWrappedObject();
        }
        wrappedObject.setStudyPvAttr(newPvAttr);
        propertyChangeSupport.firePropertyChange("studyPvAttr", oldPvAttr,
            newPvAttr);
    }

    public void setValue(String value) {
        String oldValue = wrappedObject.getValue();
        wrappedObject.setValue(value);
        propertyChangeSupport.firePropertyChange("value", oldValue, value);
    }

    public String getValue() {
        return wrappedObject.getValue();
    }

    public ProcessingEventWrapper getProcessingEvent() {
        if (pv == null) {
            ProcessingEvent p = wrappedObject.getProcessingEvent();
            if (p == null)
                return null;
            pv = new ProcessingEventWrapper(appService, p);
        }
        return pv;
    }

    public void setProcessingEvent(ProcessingEventWrapper pv) {
        this.pv = pv;
        ProcessingEvent oldPv = wrappedObject.getProcessingEvent();
        ProcessingEvent newPv = null;
        if (pv != null) {
            newPv = pv.getWrappedObject();
        }
        wrappedObject.setProcessingEvent(newPv);
        propertyChangeSupport.firePropertyChange("processingEvent", oldPv,
            newPv);
    }

    @Override
    public int compareTo(ModelWrapper<PvAttr> o) {
        return 0;
    }

    @Override
    public void reload() throws Exception {
        super.reload();
        pv = null;
        studyPvAttr = null;
    }

}
