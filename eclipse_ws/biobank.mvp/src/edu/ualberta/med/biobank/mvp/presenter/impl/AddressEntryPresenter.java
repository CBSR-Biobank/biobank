package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;
import com.pietschy.gwt.pectin.client.form.validation.HasValidation;
import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.presenter.HasState;
import edu.ualberta.med.biobank.mvp.presenter.IStatefulPresenter;
import edu.ualberta.med.biobank.mvp.presenter.IValidatablePresenter;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.presenter.state.ModelState;
import edu.ualberta.med.biobank.mvp.presenter.validation.ValidationTree;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.IView;

public class AddressEntryPresenter extends AbstractPresenter<View>
    implements IStatefulPresenter, IValidatablePresenter {
    private final ValidationTree validation = new ValidationTree();
    private final ModelState state = new ModelState();
    private Integer addressId;

    public interface View extends IView {
        ValueField<String> getStreet1();

        ValueField<String> getStreet2();

        ValueField<String> getCity();

        ValueField<String> getProvince();

        ValueField<String> getPostalCode();

        ValueField<String> getPhoneNumber();

        ValueField<String> getFaxNumber();

        ValueField<String> getCountry();
    }

    @Inject
    public AddressEntryPresenter(View view, EventBus eventBus) {
        super(view, eventBus);
    }

    @Override
    protected void onBind() {
        state.add(view);

        validation.validate(view.getCity())
            .using(new NotEmptyValidator("city"));
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
    public HasState getState() {
        return state;
    }
}
