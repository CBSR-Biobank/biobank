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
import edu.ualberta.med.biobank.mvp.view.IView;

public class AddressEditPresenter extends BasePresenter<View> {
    private Model model = new Model();

    public interface View extends IView {
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

    public Model getModel() {
        return model;
    }

    @Override
    protected void onBind() {
        model.bind();
    }

    @Override
    protected void onUnbind() {
        model.unbind();
    }

    public class Model extends BaseModel<Address> {
        final FieldModel<String> street1;
        final FieldModel<String> street2;
        final FieldModel<String> city;
        final FieldModel<String> province;
        final FieldModel<String> postalCode;
        final FieldModel<String> phoneNumber;
        final FieldModel<String> faxNumber;
        final FieldModel<String> country;

        private Model() {
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
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onBind() {
            // TODO: make Validator-s take a field name arg only?
            ValidationPlugin.validateField(city)
                .using(new NotEmptyValidator("City is required"));

            binder.bind(street1).to(view.getStreet1());
            binder.bind(street2).to(view.getStreet2());
            binder.bind(city).to(view.getCity());
            binder.bind(province).to(view.getProvince());
            binder.bind(postalCode).to(view.getPostalCode());
            binder.bind(phoneNumber).to(view.getPhoneNumber());
            binder.bind(faxNumber).to(view.getFaxNumber());
            binder.bind(country).to(view.getCountry());

            // TODO: (1) wrap validation plugin to add listeners to the field
            // and the condition to re-validate. (2) aggregate validation? Make
            // a protected model bind() and unbind() method?

            // TODO: make models implement HasValidation? Also allow them to
            // listen to the validation of other models?

            // TODO: what about unbinding?
        }

        @Override
        public void onUnbind() {
            // TODO Auto-generated method stub
        }
    }
}
