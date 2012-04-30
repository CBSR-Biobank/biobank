package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

@SuppressWarnings({ "unused", "deprecation" })
@Deprecated
public class SampleStorageHelper extends DbHelper {

    public static AliquotedSpecimenWrapper newSampleStorage(StudyWrapper study,
        SpecimenTypeWrapper type) throws Exception {
        return null;
    }

    public static AliquotedSpecimenWrapper addSampleStorage(StudyWrapper study,
        SpecimenTypeWrapper type) throws Exception {
        return null;
    }

    public static int addSampleStorages(StudyWrapper study, SiteWrapper site,
        String name) throws Exception {
        return 0;
    }
}
