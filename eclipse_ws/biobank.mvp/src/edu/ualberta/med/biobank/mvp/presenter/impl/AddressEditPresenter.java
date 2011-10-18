package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.event.ValidationEvent;
import edu.ualberta.med.biobank.mvp.event.ValidationHandler;
import edu.ualberta.med.biobank.mvp.event.ui.HasValidationHandlers;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEditPresenter.View;
import edu.ualberta.med.biobank.mvp.validation.ValidationManager;
import edu.ualberta.med.biobank.mvp.view.BaseView;

public class AddressEditPresenter extends BasePresenter<View> implements
    HasValidationHandlers {
    private final HandlerManager handlerManager = new HandlerManager(this);
    private final ValidationManager validationManager = new ValidationManager();
    private Address address;

    // TODO: make a "HasValue<Aggregate> agg = new AggregateValue<Aggregate>()"
    // that listens to a list of other HasValues and fires a changed event
    // whenever any one of them changes, and provides some aggregate object back
    // to validate?

    public interface View extends BaseView {
        HasValue<String> getStreet1();

        HasValue<String> getStreet2();

        HasValue<String> getCity();

        HasValue<String> getProvince();

        HasValue<String> getPostalCode();

        HasValue<String> getPhoneNumber();

        HasValue<String> getFaxNumber();

        HasValue<String> getCountry();
    }

    @Inject
    public AddressEditPresenter(View view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    protected void onBind() {
        // validationManager.validateValue(view.getCity(), new
        // NotEmptyValidator(
        // "city"));
        //
        // validationManager.validateValue(view.getCity())
        // .using(new NotEmptyValidator("city")).when(view.getCheckBox());
        //
        // validationManager.validateValue(view.getCity())
        // .with(new NotEmptyValidator("city")).when(view.getProvince());
    }

    @Override
    protected void onUnbind() {
    }

    public void editAddress(Address address) {
        this.address = address;

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
    public void fireEvent(GwtEvent<?> event) {
        handlerManager.fireEvent(event);
    }

    @Override
    public HandlerRegistration addValidationHandler(ValidationHandler handler) {
        return handlerManager.addHandler(ValidationEvent.getType(), handler);
    }
}
