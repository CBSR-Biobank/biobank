package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SampleStorageHelper extends DbHelper {

    public static AliquotedSpecimenWrapper newSampleStorage(StudyWrapper study,
        SpecimenTypeWrapper type) throws Exception {
        AliquotedSpecimenWrapper sampleStorage = new AliquotedSpecimenWrapper(
            appService);
        sampleStorage.setStudy(study);
        sampleStorage.setSpecimenType(type);
        sampleStorage.setQuantity(r.nextInt(10));
        sampleStorage.setVolume(r.nextDouble());
        sampleStorage.setActivityStatus(ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        return sampleStorage;
    }

    public static AliquotedSpecimenWrapper addSampleStorage(StudyWrapper study,
        SpecimenTypeWrapper type) throws Exception {
        AliquotedSpecimenWrapper sampleStorage = newSampleStorage(study, type);
        sampleStorage.persist();
        return sampleStorage;
    }

    public static int addSampleStorages(StudyWrapper study, SiteWrapper site,
        String name) throws Exception {
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            SpecimenTypeWrapper type = SpecimenTypeHelper.addSampleType(name + i);
            addSampleStorage(study, type);
        }
        study.reload();
        return nber;
    }
}
