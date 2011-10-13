package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter.View;
import edu.ualberta.med.biobank.mvp.view.BaseView;

public class AddressEntryPresenter extends BasePresenter<View> {
    private Address address;

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
    protected void onBind() {
        // TODO: validation binding?
    }

    @Override
    protected void onUnbind() {
    }
}
