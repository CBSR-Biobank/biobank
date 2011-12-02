package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.widgets.Composite;


import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.ValueField;
import edu.ualberta.med.biobank.mvp.view.item.TextBox;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;

public class AddressEntryView extends AbstractView implements
    AddressEntryPresenter.View {
    private final TextBox street1 = new TextBox();
    private final TextBox street2 = new TextBox();
    private final TextBox city = new TextBox();
    private final TextBox province = new TextBox();
    private final TextBox postalCode = new TextBox();
    private final TextBox phoneNumber = new TextBox();
    private final TextBox faxNumber = new TextBox();
    private final TextBox country = new TextBox();
    private InputTable table;

    @Override
    public ValueField<String> getStreet1() {
        return street1;
    }

    @Override
    public ValueField<String> getStreet2() {
        return street2;
    }

    @Override
    public ValueField<String> getCity() {
        return city;
    }

    @Override
    public ValueField<String> getProvince() {
        return province;
    }

    @Override
    public ValueField<String> getPostalCode() {
        return postalCode;
    }

    @Override
    public ValueField<String> getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public ValueField<String> getFaxNumber() {
        return faxNumber;
    }

    @Override
    public ValueField<String> getCountry() {
        return country;
    }

    @Override
    protected void onCreate(Composite parent) {
        table = new InputTable(parent);

        street1.setValidationControl(
            table.addLabel(Messages.AddressEntryFormCommon_street1_label));
        street1.setText(table.addText());

        street2.setValidationControl(
            table.addLabel(Messages.AddressEntryFormCommon_street2_label));
        street2.setText(table.addText());

        city.setValidationControl(table
            .addLabel(Messages.AddressEntryFormCommon_city_label));
        city.setText(table.addText());

        province.setValidationControl(table
            .addLabel(Messages.AddressEntryFormCommon_province_label));
        province.setText(table.addText());

        postalCode.setValidationControl(table
            .addLabel(Messages.AddressEntryFormCommon_postalCode_label));
        postalCode.setText(table.addText());

        country.setValidationControl(table
            .addLabel(Messages.AddressEntryFormCommon_country_label));
        country.setText(table.addText());
    }
}
