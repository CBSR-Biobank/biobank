package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.model.BaseModel;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEditPresenter.View;
import edu.ualberta.med.biobank.mvp.view.BaseView;

public class AddressEditPresenter extends BasePresenter<View> {
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

    private Model address = new Model();

    @Inject
    public AddressEditPresenter(View view, EventBus eventBus) {
        super(view, eventBus);
    }

    public HasValue<Address> getAddress() {
        return null;
    }

    @Override
    protected void onBind() {
        binder.bind(address.street1).to(view.getStreet1());
        binder.bind(address.street2).to(view.getStreet2());
        binder.bind(address.city).to(view.getCity());
        binder.bind(address.province).to(view.getProvince());
        binder.bind(address.postalCode).to(view.getPostalCode());
        binder.bind(address.phoneNumber).to(view.getPhoneNumber());
        binder.bind(address.faxNumber).to(view.getFaxNumber());
        binder.bind(address.country).to(view.getCountry());
    }

    @Override
    protected void onUnbind() {
    }

    public static class Model extends BaseModel<Address> {
        protected final FieldModel<String> street1;
        protected final FieldModel<String> street2;
        protected final FieldModel<String> city;
        protected final FieldModel<String> province;
        protected final FieldModel<String> postalCode;
        protected final FieldModel<String> phoneNumber;
        protected final FieldModel<String> faxNumber;
        protected final FieldModel<String> country;

        @SuppressWarnings("unchecked")
        public Model() {
            super(Address.class);

            street1 = fieldOfType(String.class)
                .boundTo(provider, "street1");
            street2 = fieldOfType(String.class)
                .boundTo(provider, "street2");
            city = fieldOfType(String.class)
                .boundTo(provider, "city");
            province = fieldOfType(String.class)
                .boundTo(provider, "province");
            postalCode = fieldOfType(String.class)
                .boundTo(provider, "postalCode");
            phoneNumber = fieldOfType(String.class)
                .boundTo(provider, "phoneNumber");
            faxNumber = fieldOfType(String.class)
                .boundTo(provider, "faxNumber");
            country = fieldOfType(String.class)
                .boundTo(provider, "country");

            // TODO: make Validator-s take a field name arg only?
            ValidationPlugin.validateField(city)
                .using(new NotEmptyValidator("City is required"));
        }
    }
}
