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

        display.getStreet1().setValue(address.getStreet1());
        display.getStreet2().setValue(address.getStreet2());
        display.getCity().setValue(address.getCity());
        display.getProvince().setValue(address.getProvince());
        display.getPostalCode().setValue(address.getPostalCode());
        display.getPhoneNumber().setValue(address.getPhoneNumber());
        display.getFaxNumber().setValue(address.getFaxNumber());
        display.getCountry().setValue(address.getCountry());
    }

    public Address getAddress() {
        address.setStreet1(display.getStreet1().getValue());
        address.setStreet2(display.getStreet2().getValue());
        address.setCity(display.getCity().getValue());
        address.setProvince(display.getProvince().getValue());
        address.setPostalCode(display.getPostalCode().getValue());
        address.setPhoneNumber(display.getPhoneNumber().getValue());
        address.setFaxNumber(display.getFaxNumber().getValue());
        address.setCountry(display.getCountry().getValue());

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
