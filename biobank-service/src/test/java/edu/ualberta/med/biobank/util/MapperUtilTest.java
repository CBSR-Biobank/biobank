package edu.ualberta.med.biobank.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Test;

public class MapperUtilTest {
    @Test
    public void test() {
        Mapper<Integer, Integer, Integer> numberCounter =
            new Mapper<Integer, Integer, Integer>() {
                @Override
                public Integer getKey(Integer type) {
                    return type;
                }

                @Override
                public Integer getValue(Integer type, Integer count) {
                    return count == null ? new Integer(1) : new Integer(
                        count + 1);
                }
            };

        List<Integer> numbers = new ArrayList<Integer>();
        for (int i = 1; i <= 5; i++) {
            for (int j = 1; j <= i; j++) {
                numbers.add(i);
            }
        }

        for (Map.Entry<Integer, Integer> entry : MapperUtil.map(numbers,
            numberCounter).entrySet()) {
            Assert.assertTrue(entry.getKey().equals(entry.getValue()));
        }
    }
}
