package edu.ualberta.med.biobank.forms;

import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.validators.PostalCode;

@SuppressWarnings("serial")
public abstract class AddressEntryFormCommon extends BiobankEntryForm {

    /*
     * Want to preserve insert order so using ListOrderedMap.
     */
    public static final ListOrderedMap ADDRESS_FIELDS = new ListOrderedMap() {
        {
            put("street1", new FieldInfo("Street 1", Text.class, SWT.NONE,
                null, null, null));
            put("street2", new FieldInfo("Street 2", Text.class, SWT.NONE,
                null, null, null));
            put("city", new FieldInfo("City", Text.class, SWT.NONE, null, null,
                null));
            put("province", new FieldInfo("Province", Combo.class, SWT.NONE,
                FormConstants.PROVINCES, null, null));
            put("postalCode", new FieldInfo("Postal Code", Text.class,
                SWT.NONE, null, PostalCode.class, "Invalid postal code"));
        }
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
