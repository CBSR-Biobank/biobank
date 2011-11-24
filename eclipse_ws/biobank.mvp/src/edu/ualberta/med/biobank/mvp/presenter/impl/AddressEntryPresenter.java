package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.FieldModel;
import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.model.AbstractModel;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.user.ui.HasField;
import edu.ualberta.med.biobank.mvp.view.IView;

public class AddressEntryPresenter extends AbstractPresenter<View> {
    private final Model model;

    public interface View extends IView {
        HasField<String> getStreet1();

        HasField<String> getStreet2();

        HasField<String> getCity();

        HasField<String> getProvince();

        HasField<String> getPostalCode();

        HasField<String> getPhoneNumber();

        HasField<String> getFaxNumber();

        HasField<String> getCountry();
    }

    @Inject
    public AddressEntryPresenter(View view, EventBus eventBus) {
        super(view, eventBus);

        this.model = new Model();
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

        model.bind();
    }

    @Override
    protected void onUnbind() {
        model.unbind();
    }

    public static class Model extends AbstractModel<Address> {
        final FieldModel<String> street1;
        final FieldModel<String> street2;
        final FieldModel<String> city;
        final FieldModel<String> province;
        final FieldModel<String> postalCode;
        final FieldModel<String> phoneNumber;
        final FieldModel<String> faxNumber;
        final FieldModel<String> country;

        @SuppressWarnings("unchecked")
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

            ValidationPlugin.validateField(city)
                .using(new NotEmptyValidator("City is required"));
        }

        @Override
        public void onBind() {
        }

        @Override
        public void onUnbind() {
        }
    }
}
