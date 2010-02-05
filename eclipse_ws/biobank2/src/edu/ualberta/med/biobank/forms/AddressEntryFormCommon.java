package edu.ualberta.med.biobank.forms;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.validators.PostalCodeValidator;

public abstract class AddressEntryFormCommon extends BiobankEntryForm {

    /*
     * Want to preserve insert order so using ListOrderedMap.
     */
    public static final Map<String, FieldInfo> ADDRESS_FIELDS;
    static {
        Map<String, FieldInfo> aMap = new TreeMap<String, FieldInfo>();
        aMap.put("street1", new FieldInfo("Street 1", Text.class, SWT.NONE,
            null, null, null));
        aMap.put("street2", new FieldInfo("Street 2", Text.class, SWT.NONE,
            null, null, null));
        aMap.put("city", new FieldInfo("City", Text.class, SWT.NONE, null,
            NonEmptyStringValidator.class, "Enter a city"));
        aMap.put("province", new FieldInfo("Province", Combo.class, SWT.NONE,
            FormConstants.PROVINCES, null, null));
        aMap.put("postalCode", new FieldInfo("Postal Code", Text.class,
            SWT.NONE, null, PostalCodeValidator.class, "Invalid postal code"));
        ADDRESS_FIELDS = Collections.unmodifiableMap(aMap);
    };

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        super.init(site, input);
    }

    protected void createAddressArea(ModelWrapper<?> wrapperObject) {
        Composite client = createSectionWithClient("Address");
        createBoundWidgetsFromMap(ADDRESS_FIELDS, wrapperObject, client);
    }

}
