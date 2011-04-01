package edu.ualberta.med.biobank.common.security;

/**
 * Match the centers features protection group of the security. The name should
 * match exactly the protection group name.
 */
public enum SecurityFeature {
    ASSIGN("Center Feature: Assign positions"), CLINIC_SHIPMENT(
        "Center Feature: Clinic Shipments"), COLLECTION_EVENT(
        "Center Feature: Collection Event"), DISPATCH_REQUEST(
        "Center Feature: Dispatch/Request"), LINK(
        "Center Feature: Link specimens"), PROCESSING_EVENT(
        "Center Feature: Processing Event"), REPORTS("Center Feature: Reports"), LOGGING(
        "Center Feature: Logging");

    private String name;

    private SecurityFeature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
