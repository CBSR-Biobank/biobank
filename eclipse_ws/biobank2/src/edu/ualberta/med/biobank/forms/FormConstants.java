package edu.ualberta.med.biobank.forms;

import org.apache.commons.collections.map.ListOrderedMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.validators.EmailAddress;
import edu.ualberta.med.biobank.validators.PostalCode;
import edu.ualberta.med.biobank.validators.TelephoneNumber;

/**
 * Constants used by the various forms that allow the user to edit / view the
 * information stored in the ORM model objects.
 */
@SuppressWarnings("serial")
public class FormConstants {

    // used to select a list
    public static final String[] PROVINCES = new String[] { "Alberta",
        "British Columbia", "Manitoba", "New Brunswick",
        "Newfoundland and Labrador", "Northwest Territories", "Nova Scotia",
        "Nunavut", "Ontario", "Prince Edward Island", "Quebec", "Saskatchewan",
        "Yukon" };

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
                PROVINCES, null, null));
            put("postalCode", new FieldInfo("Postal Code", Text.class,
                SWT.NONE, null, PostalCode.class, "Invalid postal code"));
            put("phoneNumber", new FieldInfo("Phone Number", Text.class,
                SWT.NONE, null, TelephoneNumber.class,
                "Telephone number is invalid"));
            put("faxNumber", new FieldInfo("Fax Number", Text.class, SWT.NONE,
                null, TelephoneNumber.class, "Telephone number is invalid"));
            put("email", new FieldInfo("Email Address", Text.class, SWT.NONE,
                null, EmailAddress.class, "Email address is invalid"));
        }
    };

    public static final String[] ACTIVITY_STATUS = new String[] { "Active",
        "Closed", "Disabled" };

    private FormConstants() {
    }

}
