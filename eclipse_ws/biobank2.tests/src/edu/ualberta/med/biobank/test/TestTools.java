package edu.ualberta.med.biobank.test;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class TestTools {

    @Test
    public void testCamelCase() {
        String className = "ProcessingEvent";
        String tableName = CamelCase.toTitleCase(className);
        Assert.assertEquals("PROCESSING_EVENT", tableName);

        String backToClassName = CamelCase.toCamelCase(tableName, true, true);
        Assert.assertEquals(className, backToClassName);

        String associationName = "specimens";
        String methodName = CamelCase.toCamelCase(associationName, true);
        Assert.assertEquals("Specimens", methodName);

        String lowerCases = CamelCase.toCamelCase(associationName, false);
        Assert.assertEquals("specimens", lowerCases);

        String columnName = "NAME_SHORT";
        String attributeName = CamelCase.toCamelCase(columnName, false, true);
        Assert.assertEquals("nameShort", attributeName);

    }
}
