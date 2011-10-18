package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEditPresenter;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;

public class AddressEntryView implements AddressEditPresenter.View {
    private final TextItem street1 = new TextItem();
    private final TextItem street2 = new TextItem();
    private final TextItem city = new TextItem();
    private final TextItem province = new TextItem();
    private final TextItem postalCode = new TextItem();
    private final TextItem phoneNumber = new TextItem();
    private final TextItem faxNumber = new TextItem();
    private final TextItem country = new TextItem();

    @Override
    public void create(Composite parent) {
        Widget widget = new Widget(parent, SWT.NONE);

        street1.setText(widget.street1);
    }

    @Override
    public HasValue<String> getStreet1() {
        return street1;
    }

    @Override
    public HasValue<String> getStreet2() {
        return street2;
    }

    @Override
    public HasValue<String> getCity() {
        return city;
    }

    @Override
    public HasValue<String> getProvince() {
        return province;
    }

    @Override
    public HasValue<String> getPostalCode() {
        return postalCode;
    }

    @Override
    public HasValue<String> getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public HasValue<String> getFaxNumber() {
        return faxNumber;
    }

    @Override
    public HasValue<String> getCountry() {
        return country;
    }

    public static class Widget extends Composite {
        public Text street1;

        public Widget(Composite parent, int style) {
            super(parent, style);

            street1 = new Text(this, SWT.NONE);
        }
    }
}
