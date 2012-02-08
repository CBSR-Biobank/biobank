package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.model.ActivityStatus;

@Deprecated
public class AliquotedSpecimenHelper extends DbHelper {

    public static AliquotedSpecimenWrapper newAliquotedSpecimen(
        StudyWrapper study, SpecimenTypeWrapper type) throws Exception {
        AliquotedSpecimenWrapper aliquotedSpecimen =
            new AliquotedSpecimenWrapper(
                appService);
        aliquotedSpecimen.setStudy(study);
        aliquotedSpecimen.setSpecimenType(type);
        aliquotedSpecimen.setQuantity(r.nextInt(10));
        aliquotedSpecimen.setVolume(r.nextDouble());
        aliquotedSpecimen.setActivityStatus(ActivityStatus.ACTIVE);
        return aliquotedSpecimen;
    }

    public static AliquotedSpecimenWrapper addAliquotedSpecimen(
        StudyWrapper study, SpecimenTypeWrapper type) throws Exception {
        AliquotedSpecimenWrapper aliquotedSpecimen = newAliquotedSpecimen(
            study, type);
        aliquotedSpecimen.persist();
        return aliquotedSpecimen;
    }

    public static List<AliquotedSpecimenWrapper> addRandAliquotedSpecimens(
        StudyWrapper study, SiteWrapper site, String name) throws Exception {
        int nber = r.nextInt(15) + 1;
        List<AliquotedSpecimenWrapper> list =
            new ArrayList<AliquotedSpecimenWrapper>();
        for (int i = 0; i < nber; i++) {
            SpecimenTypeWrapper type = SpecimenTypeHelper.addSpecimenType(name
                + i);
            list.add(addAliquotedSpecimen(study, type));
        }
        study.reload();
        return list;
    }

    public static int addAliquotedSpecimens(StudyWrapper study,
        SiteWrapper site, String name) throws Exception {
        return addRandAliquotedSpecimens(study, site, name).size();
    }
}
