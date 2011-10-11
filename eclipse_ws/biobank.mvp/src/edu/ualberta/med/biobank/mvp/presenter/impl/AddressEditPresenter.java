package edu.ualberta.med.biobank.mvp.presenter.impl;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEditPresenter.Display;
import edu.ualberta.med.biobank.mvp.view.View;

public class AddressEditPresenter extends BasePresenter<Display> {
    private Address address;

    public interface Display extends View {
        HasValue<String> getStreet1();

        HasValue<String> getStreet2();

        HasValue<String> getCity();

        HasValue<String> getProvince();

        HasValue<String> getPostalCode();

        HasValue<String> getPhoneNumber();

        HasValue<String> getFaxNumber();

        HasValue<String> getCountry();
    }

    public void setAddress(Address address) {
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
        display.getStreet1().setValue(address.getStreet1());
        display.getStreet2().setValue(address.getStreet2());
        display.getCity().setValue(address.getCity());
        display.getProvince().setValue(address.getProvince());
        display.getPostalCode().setValue(address.getPostalCode());
        display.getPhoneNumber().setValue(address.getPhoneNumber());
        display.getFaxNumber().setValue(address.getFaxNumber());
        display.getCountry().setValue(address.getCountry());

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
