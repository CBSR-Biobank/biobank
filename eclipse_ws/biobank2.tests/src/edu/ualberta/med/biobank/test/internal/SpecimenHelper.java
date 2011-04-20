package edu.ualberta.med.biobank.test.internal;

import java.util.Date;
import java.util.Random;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.OriginInfoWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class SpecimenHelper extends DbHelper {

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType,
        String activityStatus, Date createdAt) throws Exception {
        SpecimenWrapper specimen = new SpecimenWrapper(appService);
        specimen.setSpecimenType(specimenType);
        specimen.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        if (activityStatus != null) {
            specimen.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
                appService, activityStatus));
        }
        specimen.setCreatedAt(createdAt);
        return specimen;
    }

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType)
        throws Exception {
        return newSpecimen(specimenType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, Utils.getRandomDate());
    }

    public static SpecimenWrapper newSpecimen(String specimenTypeName)
        throws Exception {
        return newSpecimen(SpecimenTypeHelper.addSpecimenType(specimenTypeName));
    }

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType,
        String activityStatus, ContainerWrapper container,
        CollectionEventWrapper cevent, Integer row, Integer col,
        OriginInfoWrapper oi) throws Exception {
        SpecimenWrapper specimen = newSpecimen(specimenType, activityStatus,
            Utils.getRandomDate());
        if (container != null) {
            specimen.setParent(container);
        }
        specimen.setCollectionEvent(cevent);
        if ((row != null) && (col != null)) {
            specimen.setPosition(new RowColPos(row, col));
        }
        specimen.setOriginInfo(oi);
        specimen.setCurrentCenter(oi.getCenter());
        return specimen;
    }

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType,
        ContainerWrapper container, CollectionEventWrapper cevent, Integer row,
        Integer col, OriginInfoWrapper oi) throws Exception {
        return newSpecimen(specimenType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, cevent, row,
            col, oi);
    }

    public static SpecimenWrapper addSpecimen(SpecimenTypeWrapper specimenType,
        String activityStatus, ContainerWrapper container,
        CollectionEventWrapper cevent, Integer row, Integer col,
        OriginInfoWrapper oi) throws Exception {
        SpecimenWrapper specimen = newSpecimen(specimenType, activityStatus,
            container, cevent, row, col, oi);
        specimen.persist();
        return specimen;
    }

    public static SpecimenWrapper addSpecimen() throws Exception {
        SpecimenTypeWrapper st = SpecimenTypeHelper.addSpecimenType("testst"
            + r.nextInt());
        st.persist();
        SpecimenWrapper newSpec = newSpecimen(st);
        newSpec.setCreatedAt(Utils.getRandomDate());
        newSpec.setActivityStatus(ActivityStatusWrapper
            .getActiveActivityStatus(appService));

        StudyWrapper study = StudyHelper.addStudy("Study-" + r.nextInt());
        SiteWrapper site = SiteHelper.addSite("testsite" + r.nextInt());
        PatientWrapper patient = PatientHelper.addPatient(
            "testp" + r.nextInt(), study);
        OriginInfoWrapper oi = OriginInfoHelper.addOriginInfo(site);

        newSpec.setSpecimenType(st);
        newSpec.setOriginInfo(oi);
        CollectionEventWrapper ce = CollectionEventHelper.addCollectionEvent(
            site, patient, 2, oi, newSpec);
        return ce.getOriginalSpecimenCollection(false).get(0);
    }

    public static SpecimenWrapper addSpecimen(SpecimenTypeWrapper specimenType,
        ContainerWrapper container, CollectionEventWrapper cevent, Integer row,
        Integer col, OriginInfoWrapper oi) throws Exception {
        SpecimenWrapper specimen = addSpecimen(specimenType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, cevent, row,
            col, oi);
        if (container != null)
            container.reload();
        return specimen;
    }

    public static SpecimenWrapper addSpecimen(SpecimenTypeWrapper specimenType,
        ContainerWrapper container, CollectionEventWrapper cevent, Integer row,
        Integer col, CenterWrapper<?> center) throws Exception {
        OriginInfoWrapper oi = new OriginInfoWrapper(appService);
        oi.setCenter(center);
        oi.persist();
        return addSpecimen(specimenType, container, cevent, row, col, oi);
    }
}
