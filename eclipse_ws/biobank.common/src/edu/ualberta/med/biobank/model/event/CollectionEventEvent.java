package edu.ualberta.med.biobank.model.event;


//@DiscriminatorValue("3")
public class CollectionEventEvent {
    // extends StudyEvent {
    // private static final long serialVersionUID = 1L;
    //
    // private EventObject collectionEvent;
    //
    // public CollectionEventEvent(CollectionEvent collectionEvent) {
    // setStudy(collectionEvent.getPatient().getStudy());
    // setCollectionEvent(new EventObject());
    // }
    //
    // @OneToOne
    // @JoinTable(name = "COLLECTION_EVENT_EVENT",
    // joinColumns = @JoinColumn(name = "EVENT_ID", unique = true))
    // public EventObject getCollectionEvent() {
    // return collectionEvent;
    // }
    //
    // public void setCollectionEvent(EventObject container) {
    // this.collectionEvent = container;
    // }
    //
    // // TODO: implement interface for really easy usertype?
    // public enum ContainerEventType {
    // DELETE(1),
    // READ_ALIQUOTED_SPECIMENS(2),
    // READ_EVENT_ATTRS(3),
    // READ(4),
    // READ_SOURCE_SPECIMENS(5),
    // CREATE(6),
    // UPDATE(7);
    //
    // private final Integer id;
    //
    // private ContainerEventType(int id) {
    // this.id = id;
    // }
    //
    // public Integer getId() {
    // return id;
    // }
    // }
}
