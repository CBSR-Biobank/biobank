package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
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
    private boolean created = false;

    @Override
    public void create(Composite parent) {
        if (!created) {
            Label street1Label = new Label(parent, SWT.NONE);
            street1Label.setText("street1");
            street1.setValidationControl(street1Label);
            street1.setText(new Text(parent, SWT.BORDER));

            Label cityLabel = new Label(parent, SWT.NONE);
            cityLabel.setText("city");
            street1.setValidationControl(cityLabel);
            city.setText(new Text(parent, SWT.BORDER));

            created = true;
        } else {
            // TODO: write super class and override onCreate(parent) method?
            // TODO: complain like crazy?
        }
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
}
