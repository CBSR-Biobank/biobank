package edu.ualberta.med.biobank.test.internal;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class AliquotedSpecimenHelper extends DbHelper {

    public static AliquotedSpecimenWrapper newSampleStorage(StudyWrapper study,
        SpecimenTypeWrapper type) throws Exception {
        AliquotedSpecimenWrapper aliquotedSpecimen = new AliquotedSpecimenWrapper(
            appService);
        aliquotedSpecimen.setStudy(study);
        aliquotedSpecimen.setSpecimenType(type);
        aliquotedSpecimen.setQuantity(r.nextInt(10));
        aliquotedSpecimen.setVolume(r.nextDouble());
        aliquotedSpecimen.setActivityStatus(ActivityStatusWrapper
            .getActivityStatus(appService,
                ActivityStatusWrapper.ACTIVE_STATUS_STRING));
        return aliquotedSpecimen;
    }

    public static AliquotedSpecimenWrapper addSampleStorage(StudyWrapper study,
        SpecimenTypeWrapper type) throws Exception {
        AliquotedSpecimenWrapper aliquotedSpecimen = newSampleStorage(study,
            type);
        aliquotedSpecimen.persist();
        return aliquotedSpecimen;
    }

    public static int addSampleStorages(StudyWrapper study, SiteWrapper site,
        String name) throws Exception {
        int nber = r.nextInt(15) + 1;
        for (int i = 0; i < nber; i++) {
            SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name
                + i);
            addSampleStorage(study, type);
        }
        study.reload();
        return nber;
    }
}
