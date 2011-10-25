import edu.ualberta.med.biobank.mvp.presenter.IReloadablePresenter;

public class FormManager {
    public IReloadablePresenter open(IReloadablePresenter presenter) {
        // check if matching presenter exists, if so, return it, otherwise call
        // .open() on the presenter.
        // e.g. FormManager.open(new CollectionEventEditPresenter(patient));
        return null;
    }
}
