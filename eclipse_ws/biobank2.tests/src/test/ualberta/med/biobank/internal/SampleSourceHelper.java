package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

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

    public static void deleteCreatedSampleSources() throws Exception {
        for (SampleSourceWrapper source : createdSampleSources) {
            source.reload();
            source.delete();
        }
        createdSampleSources.clear();
    }

    public static int addSampleSources(StudyWrapper study, String name)
        throws Exception {
        int nber = r.nextInt(15) + 1;
        List<SampleSourceWrapper> sources = new ArrayList<SampleSourceWrapper>();
        for (int i = 0; i < nber; i++) {
            sources.add(SampleSourceHelper.addSampleSource(name + i));
        }
        study.setSampleSourceCollection(sources);
        study.persist();
        return nber;
    }

}
