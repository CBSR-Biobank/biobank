package edu.ualberta.med.biobank.mvp.view;

import org.eclipse.swt.widgets.Composite;


import edu.ualberta.med.biobank.forms.Messages;
import edu.ualberta.med.biobank.mvp.presenter.impl.AddressEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasField;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;
import edu.ualberta.med.biobank.mvp.view.util.InputTable;

public class AddressEntryView extends AbstractView implements
    AddressEntryPresenter.View {
    private final TextItem street1 = new TextItem();
    private final TextItem street2 = new TextItem();
    private final TextItem city = new TextItem();
    private final TextItem province = new TextItem();
    private final TextItem postalCode = new TextItem();
    private final TextItem phoneNumber = new TextItem();
    private final TextItem faxNumber = new TextItem();
    private final TextItem country = new TextItem();
    private InputTable table;

    @Override
    public HasField<String> getStreet1() {
        return street1;
    }

    @Override
    public HasField<String> getStreet2() {
        return street2;
    }

    @Override
    public HasField<String> getCity() {
        return city;
    }

    @Override
    public HasField<String> getProvince() {
        return province;
    }

    @Override
    public HasField<String> getPostalCode() {
        return postalCode;
    }

    @Override
    public HasField<String> getPhoneNumber() {
        return phoneNumber;
    }

    @Override
    public HasField<String> getFaxNumber() {
        return faxNumber;
    }

    @Override
    public HasField<String> getCountry() {
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
