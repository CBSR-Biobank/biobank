package edu.ualberta.med.biobank.common.security;

public enum Feature {
    ASSIGN("Center Feature: Assign positions"), CLINIC_SHIPMENT(
        "Center Feature: Clinic Shipments"), COLLECTION_EVENT(
        "Center Feature: Collection Event"), DISPATCH_REQUEST(
        "Center Feature: Dispatch/Request"), LINK(
        "Center Feature: Link specimens"), PROCESSING_EVENT(
        "Center Feature: Processing Event"), REPORTS("Center Feature: Reports");

    private String name;

    private Feature(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
