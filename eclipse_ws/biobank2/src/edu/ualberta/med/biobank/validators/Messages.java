package edu.ualberta.med.biobank.validators;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {
    private static final String BUNDLE_NAME = "edu.ualberta.med.biobank.validators.messages"; //$NON-NLS-1$
    public static String CabinetInventoryIDValidator_error_msg;
    public static String CabinetInventoryIDValidator_nonstring_error_msg;
    public static String EmailAddressValidator_nonstring_error_msg;
    public static String EmptyStringValidator_nonstring_msg;
    public static String InventoryIdValidator_checking_error_msg;
    public static String InventoryIdValidator_nonstring_error_msg;
    public static String PalletLabelValidator_nonstring_error_msg;
    public static String PathValidator_nonstring_error_msg;
    public static String PostalCodeValidator_nonstring_error_msg;
    public static String ScannerBarcodeValidator_nonstring_error_msg;
    public static String StringLengthValidator_nonstring_error_msg;
    public static String TelephoneNumberValidator_nonstring_error_msg;
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, Messages.class);
    }

    private Messages() {
    }
}
