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

    private Model model = new Model();

    @Inject
    public AddressEditPresenter(View view, EventBus eventBus) {
        super(view, eventBus);
    }

    public void editAddress(Address address) {
        model.setValue(address);
    }

    public Address getAddress() {
        return model.getValue();
    }

    public Model getModel() {
        return model;
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

        public Model() {
            super(Address.class);
            street1 = addField(String.class, "street1");
            street2 = addField(String.class, "street2");
            city = addField(String.class, "city");
            province = addField(String.class, "province");
            postalCode = addField(String.class, "postalCode");
            phoneNumber = addField(String.class, "phoneNumber");
            faxNumber = addField(String.class, "faxNumber");
            country = addField(String.class, "country");

            ValidationPlugin.validateField(city)
                .using(new NotEmptyValidator("City is required"));
        }
    }
}
