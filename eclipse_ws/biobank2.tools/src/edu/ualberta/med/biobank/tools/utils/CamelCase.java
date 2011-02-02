package edu.ualberta.med.biobank.tools.utils;

public class CamelCase {

    public static String toCamelCase(String str, boolean firstCharUpperCase) {
        StringBuffer sb = new StringBuffer();
        String[] splitStr = str.split("_");
        boolean firstTime = true;
        for (String temp : splitStr) {
            if (firstTime && !firstCharUpperCase) {
                sb.append(temp.toLowerCase());
                firstTime = false;
            } else {
                sb.append(Character.toUpperCase(temp.charAt(0)));
                sb.append(temp.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    public static String toTitleCase(String str) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0, n = str.length(); i < n; ++i) {
            char ch = str.charAt(i);

            if (Character.isUpperCase(ch) && (i > 0)) {
                sb.append("_" + ch);
            } else {
                sb.append(Character.toUpperCase(ch));
            }
        }

        return sb.toString();
    }

}
