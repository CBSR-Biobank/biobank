package edu.ualberta.med.biobank.common.security;

/**
 * Match the centers features protection group of the security. The name should
 * match exactly the protection group name.
 */
@SuppressWarnings("nls")
@Deprecated
public enum SecurityFeature {
    ASSIGN("Center Feature: Assign positions"),
    CLINIC_SHIPMENT( //$NON-NLS-1$
        "Center Feature: Clinic Shipments"),
    COLLECTION_EVENT( //$NON-NLS-1$
        "Center Feature: Collection Event"),
    DISPATCH_REQUEST( //$NON-NLS-1$
        "Center Feature: Dispatch/Request"),
    LINK( //$NON-NLS-1$
        "Center Feature: Link specimens"),
    PROCESSING_EVENT( //$NON-NLS-1$
        "Center Feature: Processing Event"), //$NON-NLS-1$
    REPORTS("Center Feature: Reports"), LOGGING( //$NON-NLS-1$ 
        "Center Feature: Logging"), //$NON-NLS-1$
    PRINTER_LABELS("Center Feature: Label Printing"); //$NON-NLS-1$

    private String name;

    private SecurityFeature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
