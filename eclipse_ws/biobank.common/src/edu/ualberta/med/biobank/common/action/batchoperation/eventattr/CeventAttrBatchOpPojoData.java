package edu.ualberta.med.biobank.common.action.batchoperation.eventattr;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpPojoHelper;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.StudyEventAttr;

public class CeventAttrBatchOpPojoData implements IBatchOpPojoHelper {

    private final CeventAttrBatchOpInputPojo pojo;
    private Patient patient;
    private CollectionEvent collectionEvent;
    private StudyEventAttr studyEventAttr;

    public CeventAttrBatchOpPojoData(CeventAttrBatchOpInputPojo pojo) {
        this.pojo = pojo;
    }

    @Override
    public int getCsvLineNumber() {
        return pojo.getLineNumber();
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectionEvent getCollectionEvent() {
        return collectionEvent;
    }

    public void setCollectionEvent(CollectionEvent collectionEvent) {
        this.collectionEvent = collectionEvent;
    }

    public StudyEventAttr getStudyEventAttr() {
        return studyEventAttr;
    }

    public void setStudyEventAttr(StudyEventAttr studyEventAttr) {
        this.studyEventAttr = studyEventAttr;
    }

    @SuppressWarnings("nls")
    public EventAttr getCeventEventAttr() {

    }
}
