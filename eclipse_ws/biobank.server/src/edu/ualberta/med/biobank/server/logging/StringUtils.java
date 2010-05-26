package edu.ualberta.med.biobank.server.logging;

public class StringUtils {

    /**
     * returns true of the string is blank or null
     */
    public static boolean isBlank(String str) {
        boolean test = false;
        if (str == null) {
            test = true;
        } else {
            if (str.equals("")) {
                test = true;
            }
        }
        return test;
    }

    /**
     * initialises a string.
     */
    public static String initString(String str) {
        String test = "";
        if (str != null) {
            test = str.trim();
        }
        return test;
    }

}
