package edu.ualberta.med.biobank.common;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class VarCharLengths {

    private static VarCharLengths instance = null;

    Properties properties;

    public static VarCharLengths getInstance() throws BiobankCheckException {
        if (instance != null) {
            return instance;
        }

        try {
            instance = new VarCharLengths();
            return instance;
        } catch (Exception e) {
            throw new BiobankCheckException(
                "cound not create VarCharLengths singleton", e);
        }
    }

    private VarCharLengths() throws FileNotFoundException, IOException {
        properties = new Properties();
        // expect the file next to this TestProp.java:
        InputStream is = VarCharLengths.class
            .getResourceAsStream("VarCharLengths.properties");
        properties.load(is);
    }

    public int getMaxSize(String key) {
        try {
            return Integer.parseInt(properties.getProperty(transform(key)));
        } catch (Exception e) {
            return -1;
        }
    }

    public String transform(String key) {
        char[] keyChars = key.toCharArray();
        for (int i = 0; i < keyChars.length; i++) {
            if (Character.isUpperCase(keyChars[i]))
                return transform(key.replace(String.valueOf(keyChars[i]), "_"
                    + Character.toLowerCase(keyChars[i])));
        }
        return key.toUpperCase();
    }
}
