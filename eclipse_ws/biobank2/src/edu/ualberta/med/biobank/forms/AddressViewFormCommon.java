package edu.ualberta.med.biobank.forms;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;

public abstract class AddressViewFormCommon extends BiobankViewForm {

    protected void createAddressSection(ModelWrapper<?> wrapperObject) {
        Composite client = createSectionWithClient("Address");
        Section section = (Section) client.getParent();
        section.setExpanded(false);
        createAddressArea(client, wrapperObject);
    }

    protected void createAddressArea(Composite parent,
        ModelWrapper<?> wrapperObject) {
        for ()
        createWidgetsFromMap(AddressEntryFormCommon.ADDRESS_FIELDS, parent);
        setAdressValues(wrapperObject);
    }

    protected void setAdressValues(ModelWrapper<?> wrapperObject) {
        setWidgetsValues(AddressEntryFormCommon.ADDRESS_FIELDS, wrapperObject);
    }

}
