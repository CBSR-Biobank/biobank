package edu.ualberta.med.biobank.server.logging;


public class MessageGenerator {

    public static String generateStringMessage(String action,
        String patientNumber, String inventoryID, String locationLabel,
        String details, String type) {

        StringBuffer sb = new StringBuffer();
        append(sb, LogProperty.ACTION.getPropertyName(), action);
        append(sb, LogProperty.PATIENT_NUMBER.getPropertyName(), patientNumber);
        append(sb, LogProperty.INVENTORY_ID.getPropertyName(), inventoryID);
        append(sb, LogProperty.LOCATION_LABEL.getPropertyName(), locationLabel);
        append(sb, LogProperty.DETAILS.getPropertyName(), details);
        append(sb, LogProperty.TYPE.getPropertyName(), type);
        return sb.toString();
    }

    private static void append(StringBuffer sb, String property, String value) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(property).append("=").append(value);
        }
    }
}
