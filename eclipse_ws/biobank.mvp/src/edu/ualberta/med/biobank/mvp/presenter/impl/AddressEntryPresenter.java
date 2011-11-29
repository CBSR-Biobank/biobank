package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.presenter.HasState;
import edu.ualberta.med.biobank.mvp.presenter.IValidatablePresenter;
import edu.ualberta.med.biobank.mvp.presenter.IViewStatePresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.presenter.model.SimpleViewState;
import edu.ualberta.med.biobank.mvp.presenter.validation.ValidationTree;
import edu.ualberta.med.biobank.mvp.user.ui.HasValueField;
import edu.ualberta.med.biobank.mvp.view.IView;

public class AddressEntryPresenter extends AbstractPresenter<View>
    implements IViewStatePresenter, IValidatablePresenter {
    private final ValidationTree validation = new ValidationTree();
    private final SimpleViewState viewState = new SimpleViewState();
    private Integer addressId;

    public interface View extends IView {
        HasValueField<String> getStreet1();

        HasValueField<String> getStreet2();

        HasValueField<String> getCity();

        HasValueField<String> getProvince();

        HasValueField<String> getPostalCode();

        HasValueField<String> getPhoneNumber();

        HasValueField<String> getFaxNumber();

        HasValueField<String> getCountry();
    }

    @Inject
    public AddressEntryPresenter(View view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    protected void onBind() {
        validation.validate(view.getCity())
            .using(new NotEmptyValidator("city"))
            .when(getViewState().dirty());
    }

    @Override
    protected void onUnbind() {
        validation.dispose();
    }

    public void setAddress(Address address) {
        this.addressId = address.getId();

        view.getStreet1().setValue(address.getStreet1());
        view.getStreet2().setValue(address.getStreet2());
        view.getCity().setValue(address.getCity());
        view.getProvince().setValue(address.getProvince());
        view.getPostalCode().setValue(address.getPostalCode());
        view.getPhoneNumber().setValue(address.getPhoneNumber());
        view.getFaxNumber().setValue(address.getFaxNumber());
        view.getCountry().setValue(address.getCountry());
    }

    public Address getAddress() {
        Address address = new Address();
        address.setId(addressId);

        address.setStreet1(view.getStreet1().getValue());
        address.setStreet2(view.getStreet2().getValue());
        address.setCity(view.getCity().getValue());
        address.setProvince(view.getProvince().getValue());
        address.setPostalCode(view.getPostalCode().getValue());
        address.setPhoneNumber(view.getPhoneNumber().getValue());
        address.setFaxNumber(view.getFaxNumber().getValue());
        address.setCountry(view.getCountry().getValue());

        return address;
    }

    @Override
    public HasValidation getValidation() {
        return validation;
    }

    @Override
    public HasState getViewState() {
        return viewState;
    }
}
