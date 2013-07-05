package edu.ualberta.med.biobank.test.util;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.common.util.InventoryIdUtil;

public class InventoryIdUtilTest {
    @Test
    public void test() {
    	Assert.assertTrue(InventoryIdUtil.isFormatMicroplatePosition("##123##34"));
    	Assert.assertFalse(InventoryIdUtil.isFormatMicroplatePosition("#123#34"));
    	Assert.assertTrue(InventoryIdUtil.isFormatMicroplate("123"));
    	Assert.assertFalse(InventoryIdUtil.isFormatMicroplate("#123"));
    	Assert.assertEquals(InventoryIdUtil.microplatePart("##123##34"), "123");
    	Assert.assertEquals(InventoryIdUtil.positionPart("##123##34"), "34");
    	Assert.assertEquals(InventoryIdUtil.formatMicroplatePosition("123","45"), "##123##45");
    	Assert.assertEquals(InventoryIdUtil.patternFromMicroplateId("123"), "##123##%");
    }
}
