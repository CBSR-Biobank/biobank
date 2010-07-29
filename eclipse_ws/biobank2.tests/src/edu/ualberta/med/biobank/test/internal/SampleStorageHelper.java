package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleStorageWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class SampleStorageHelper extends DbHelper {

    public static SampleStorageWrapper newSampleStorage(StudyWrapper study,
        SampleTypeWrapper type) throws Exception {
        SampleStorageWrapper sampleStorage = new SampleStorageWrapper(
            appService);
        sampleStorage.setStudy(study);
        sampleStorage.setSampleType(type);
        sampleStorage.setQuantity(r.nextInt(10));
        sampleStorage.setVolume(r.nextDouble());
        sampleStorage.setActivityStatus(ActivityStatusWrapper
            .getActivityStatus(appService, "Active"));
        return sampleStorage;
    }

    public static SampleStorageWrapper addSampleStorage(StudyWrapper study,
        SampleTypeWrapper type) throws Exception {
        SampleStorageWrapper sampleStorage = newSampleStorage(study, type);
        sampleStorage.persist();
        return sampleStorage;
    }

    public static int addSampleStorages(StudyWrapper study, SiteWrapper site,
        String name) throws Exception {
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            SampleTypeWrapper type = SampleTypeHelper.addSampleType(name + i);
            addSampleStorage(study, type);
        }
        study.reload();
        return nber;
    }
}
