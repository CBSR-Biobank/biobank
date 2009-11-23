package test.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import junit.framework.Assert;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;

public class TestCommon {

    public static ContainerTypeWrapper addSampleTypes(ContainerTypeWrapper ct,
        List<SampleTypeWrapper> sampleTypes) throws Exception {
        Assert.assertTrue("not enough sample types for test", (sampleTypes
            .size() > 10));
        ct.setSampleTypeCollection(sampleTypes);
        ct.persist();
        ct.reload();
        return ct;
    }

    public static List<SampleTypeWrapper> getRandomSampleTypeList(Random r,
        List<SampleTypeWrapper> list) {
        List<SampleTypeWrapper> result = new ArrayList<SampleTypeWrapper>();
        for (SampleTypeWrapper st : list) {
            if (r.nextBoolean()) {
                result.add(st);
            }
        }
        return result;
    }

}
