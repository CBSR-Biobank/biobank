package test.ualberta.med.biobank.internal;

import edu.ualberta.med.biobank.common.BiobankCheckException;
import edu.ualberta.med.biobank.common.wrappers.PvSampleSourceWrapper;
import edu.ualberta.med.biobank.model.PvSampleSource;

public class PvSampleSourceHelper extends DbHelper {

    public static PvSampleSourceWrapper newSampleSource(String name) {
        PvSampleSourceWrapper pss = new PvSampleSourceWrapper(appService,
            new PvSampleSource());
        pss.getSampleSource().setName(name);
        return pss;
    }

    public static PvSampleSourceWrapper addSampleSource(String name)
        throws BiobankCheckException, Exception {
        PvSampleSourceWrapper pss = newSampleSource(name);
        pss.persist();
        return pss;
    }

}
