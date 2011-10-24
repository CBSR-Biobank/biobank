import edu.ualberta.med.biobank.mvp.presenter.ReloadablePresenter;

public class FormManager {
    public ReloadablePresenter open(ReloadablePresenter presenter) {
        // check if matching presenter exists, if so, return it, otherwise call
        // .open() on the presenter.
        // e.g. FormManager.open(new CollectionEventEditPresenter(patient));
        return null;
    }
}
