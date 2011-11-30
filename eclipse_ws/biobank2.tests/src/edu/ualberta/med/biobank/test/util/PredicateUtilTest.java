package edu.ualberta.med.biobank.test.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

import org.junit.Test;

import edu.ualberta.med.biobank.common.util.Predicate;
import edu.ualberta.med.biobank.common.util.PredicateUtil;

public class PredicateUtilTest {
    @Test
    public void test() {
        Predicate<Integer> le3 = new Predicate<Integer>() {
            @Override
            public boolean evaluate(Integer i) {
                return i <= 3;
            }
        };

        Predicate<Integer> ge3 = new Predicate<Integer>() {
            @Override
            public boolean evaluate(Integer i) {
                return i >= 3;
            }
        };

        Collection<Integer> all, filtered;
        all = new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8));
        filtered = new LinkedList<Integer>();

        PredicateUtil.filterInto(all, le3, filtered);
        Arrays.equals(all.toArray(), Arrays.asList(1, 2, 3).toArray());

        filtered = PredicateUtil.filter(all,
            PredicateUtil.andPredicate(le3, ge3));
        Arrays.equals(all.toArray(), Arrays.asList(3).toArray());

        PredicateUtil.filterOut(all, PredicateUtil.notPredicate(le3));
        Arrays.equals(all.toArray(), Arrays.asList(4, 5, 6, 7, 8).toArray());
    }
}
