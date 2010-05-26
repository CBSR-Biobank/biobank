package edu.ualberta.med.biobank.server.logging;

import edu.ualberta.med.biobank.server.logging.user.BiobankThreadVariable;
import edu.ualberta.med.biobank.server.logging.user.UserInfo;

public class MessageGenerator {

    public static String generateStringMessage(String action,
        String patientNumber, String inventoryID, String locationLabel,
        String details) {

        UserInfo userInfo = BiobankThreadVariable.get();
        if (null == userInfo)
            userInfo = new UserInfo();
        StringBuffer sb = new StringBuffer();
        append(sb, LogProperty.ACTION.getPropertyName(), action);
        append(sb, LogProperty.PATIENT_NUMBER.getPropertyName(),
            patientNumber);
        append(sb, LogProperty.INVENTORY_ID.getPropertyName(), inventoryID);
        append(sb, LogProperty.LOCATION_LABEL.getPropertyName(),
            locationLabel);
        append(sb, LogProperty.DETAILS.getPropertyName(), details);
        return sb.toString();
    }

    private static void append(StringBuffer sb, String type, String value) {
        if (value != null) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(type).append("=").append(value);
        }
    }
}
