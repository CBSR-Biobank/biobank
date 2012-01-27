package edu.ualberta.med.biobank.model;

public class EventAttr extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String value;
    private CollectionEvent collectionEvent;
    private StudyEventAttr studyEventAttr;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
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
}
