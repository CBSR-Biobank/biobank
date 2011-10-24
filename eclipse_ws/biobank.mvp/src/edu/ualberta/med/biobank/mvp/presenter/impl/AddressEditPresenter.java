package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;
import com.pietschy.gwt.pectin.client.value.MutableValueModel;

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

    private Model model = new Model();

    @Inject
    public AddressEditPresenter(View view, EventBus eventBus) {
        super(view, eventBus);
    }

    public MutableValueModel<Address> getAddress() {
        return model.getMutableValueModel();
    }

    @Override
    protected void onBind() {
        binder.bind(model.street1).to(view.getStreet1());
        binder.bind(model.street2).to(view.getStreet2());
        binder.bind(model.city).to(view.getCity());
        binder.bind(model.province).to(view.getProvince());
        binder.bind(model.postalCode).to(view.getPostalCode());
        binder.bind(model.phoneNumber).to(view.getPhoneNumber());
        binder.bind(model.faxNumber).to(view.getFaxNumber());
        binder.bind(model.country).to(view.getCountry());

        model.bind();
    }

    @Override
    protected void onUnbind() {
        model.unbind();
    }

    public static class Model extends BaseModel<Address> {
        final FieldModel<String> street1;
        final FieldModel<String> street2;
        final FieldModel<String> city;
        final FieldModel<String> province;
        final FieldModel<String> postalCode;
        final FieldModel<String> phoneNumber;
        final FieldModel<String> faxNumber;
        final FieldModel<String> country;

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

            // TODO: (1) wrap validation plugin to add listeners to the field
            // and the condition to re-validate. (2) aggregate validation? Make
            // a protected model bind() and unbind() method?

            // TODO: what about unbinding?
        }
    }
}
