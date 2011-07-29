package edu.ualberta.med.biobank.common.util;

public class ClassUtils {
    // returns the class (without the package if any)
    public static String getClassName(Class<?> c) {
        String FQClassName = c.getName();
        int firstChar;
        firstChar = FQClassName.lastIndexOf('.') + 1;
        if (firstChar > 0) {
            FQClassName = FQClassName.substring(firstChar);
        }
        return FQClassName;
    }

    // returns package and class name
    public static String getFullClassName(Class<?> c) {
        return c.getName();
    }

    // returns the package without the classname, empty string if
    // there is no package
    public static String getPackageName(Class<?> c) {
        String fullyQualifiedName = c.getName();
        int lastDot = fullyQualifiedName.lastIndexOf('.');
        if (lastDot == -1) {
            return ""; //$NON-NLS-1$
        }
        return fullyQualifiedName.substring(0, lastDot);
    }
}