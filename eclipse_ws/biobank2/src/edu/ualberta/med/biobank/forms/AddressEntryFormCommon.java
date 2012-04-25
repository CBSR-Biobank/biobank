package edu.ualberta.med.biobank.forms;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.common.peer.AddressPeer;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.gui.common.forms.FieldInfo;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Address;

@SuppressWarnings("nls")
public abstract class AddressEntryFormCommon extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(AddressEntryFormCommon.class);

    /*
     * Want to preserve insert order so using ListOrderedMap.
     */
    public static final Map<String, FieldInfo> ADDRESS_FIELDS;
    static {
        Map<String, FieldInfo> aMap = new LinkedHashMap<String, FieldInfo>();
        aMap.put(AddressPeer.STREET1.getName(), new FieldInfo(
            Address.Property.STREET1.toString(),
            BgcBaseText.class,
            SWT.NONE, null, null, null));
        aMap.put(AddressPeer.STREET2.getName(), new FieldInfo(
            Address.Property.STREET2.toString(),
            BgcBaseText.class,
            SWT.NONE, null, null, null));
        aMap.put(AddressPeer.CITY.getName(), new FieldInfo(
            Address.Property.CITY.toString(),
            BgcBaseText.class,
            SWT.NONE, null, NonEmptyStringValidator.class,
            // validation error message
            i18n.tr("Enter a city")));
        aMap.put(AddressPeer.PROVINCE.getName(), new FieldInfo(
            Address.Property.PROVINCE.toString(), BgcBaseText.class,
            SWT.NONE, null, null, null));
        aMap.put(AddressPeer.POSTAL_CODE.getName(), new FieldInfo(
            Address.Property.POSTAL_CODE.toString(),
            BgcBaseText.class, SWT.NONE, null, null, null));
        aMap.put(AddressPeer.COUNTRY.getName(), new FieldInfo(
            Address.Property.COUNTRY.toString(), BgcBaseText.class,
            SWT.NONE, null, null, null));
        ADDRESS_FIELDS = Collections.unmodifiableMap(aMap);
    };

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        super.init(site, input);
    }

    protected void createAddressArea(ModelWrapper<?> wrapperObject) {
        Composite client = createSectionWithClient(Address.NAME.singular().toString());
        createBoundWidgetsFromMap(ADDRESS_FIELDS, wrapperObject, client);
    }
}
