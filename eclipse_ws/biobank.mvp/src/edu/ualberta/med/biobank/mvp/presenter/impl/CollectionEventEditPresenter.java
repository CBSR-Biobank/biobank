package edu.ualberta.med.biobank.mvp.presenter.impl;

public class CollectionEventEditPresenter {
    // public abstract class CollectionEventEditPresenter extends
    // BaseEntryPresenter<Display> {
    // public interface Display extends CloseableView, ReloadableView,
    // SaveableView {
    // void setPatient(Patient patient);
    //
    // HasValue<Integer> getVisitNumber();
    // }
    //
    // @Override
    // protected void doSave() {
    // SaveCollectionEventAction save = new SaveCollectionEventAction();
    // // TODO: populate save action with appropriate data
    //
    // dispatcher.exec(save, new ActionCallback<Integer>() {
    // @Override
    // public void onFailure(Throwable caught) {
    // // TODO: replace with a better message.
    // eventBus.fireEvent(new AlertEvent("FAIL!"));
    // }
    //
    // @Override
    // public void onSuccess(Integer result) {
    //
    // }
    // });
    // }
    //
    // @Override
    // public void doPopulate() {
    // CollectionEvent collectionEvent = getCollectionEvent();
    //
    // display.setPatient(collectionEvent.getPatient());
    // display.getVisitNumber().setValue(collectionEvent.getVisitNumber());
    // }
    //
    // @Override
    // protected void onUnbind() {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // protected abstract CollectionEvent getCollectionEvent();
    //
    // public static class Update extends CollectionEventEditPresenter {
    // private final Integer collectionEventId;
    //
    // public Update(CollectionEvent collectionEvent) {
    // this.collectionEventId = collectionEvent.getId();
    // }
    //
    // @Override
    // protected CollectionEvent getCollectionEvent() {
    // return null; // action to get CE
    // }
    // }
    //
    // public static class Create extends CollectionEventEditPresenter {
    // public Create(Integer patientId) {
    // }
    //
    // @Override
    // protected CollectionEvent getCollectionEvent() {
    // CollectionEvent tmp = new CollectionEvent();
    // tmp.setVisitNumber(null); // action to get next visit number
    // tmp.setPatient(null); // action to get patient;
    //
    // // TODO: work on action batch query?
    //
    // return tmp;
    // }
    // }
}
