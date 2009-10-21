package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;

public class SampleSourceHelper extends DbHelper {

    protected static List<SampleSourceWrapper> createdSampleSources = new ArrayList<SampleSourceWrapper>();

    public static SampleSourceWrapper newSampleSource(String name) {
        SampleSourceWrapper source = new SampleSourceWrapper(appService);
        source.setName(name);
        return source;
    }

    public static SampleSourceWrapper addSampleSource(String name,
        boolean addToCreatedList) throws Exception {
        SampleSourceWrapper source = newSampleSource(name);
        source.persist();
        if (addToCreatedList) {
            createdSampleSources.add(source);
        }
        return source;
    }

    public static SampleSourceWrapper addSampleSource(String name)
        throws Exception {
        return addSampleSource(name, true);
    }

    public static void deleteCreatedSampleSource() throws Exception {
        for (SampleSourceWrapper source : createdSampleSources) {
            source.reload();
            source.delete();
        }
    }

}
