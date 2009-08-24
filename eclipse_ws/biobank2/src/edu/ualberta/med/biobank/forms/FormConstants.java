package edu.ualberta.med.biobank.forms;


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

    public static final String[] ACTIVITY_STATUS = new String[] { "Active",
        "Closed", "Disabled" };

    private FormConstants() {
    }

}
