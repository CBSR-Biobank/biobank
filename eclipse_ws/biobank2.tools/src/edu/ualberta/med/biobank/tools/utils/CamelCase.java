package edu.ualberta.med.biobank.tools.utils;

@SuppressWarnings("nls")
public class CamelCase {

    public static String toCamelCase(String str, boolean firstCharUpperCase) {
        return toCamelCase(str, firstCharUpperCase, false);
    }

    public static String toCamelCase(String str, boolean firstCharUpperCase,
        boolean lowerOtherChars) {
        StringBuffer sb = new StringBuffer();
        String[] splitStr = str.split("_");
        boolean firstTime = true;

        for (String temp : splitStr) {
            if (firstTime && !firstCharUpperCase) {
                sb.append(temp.toLowerCase());
                firstTime = false;
            } else {
                sb.append(Character.toUpperCase(temp.charAt(0)));
                String others = temp.substring(1);
                if (lowerOtherChars)
                    others = others.toLowerCase();
                sb.append(others);
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
