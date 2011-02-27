package edu.ualberta.med.biobank.test.internal;

import java.util.Random;

import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.test.wrappers.TestCommon;

public class SpecimenHelper extends DbHelper {

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType,
        String activityStatus) throws Exception {
        SpecimenWrapper Specimen = new SpecimenWrapper(appService);
        Specimen.setSpecimenType(specimenType);
        Specimen.setInventoryId(TestCommon.getNewInventoryId(new Random()));
        if (activityStatus != null) {
            Specimen.setActivityStatus(ActivityStatusWrapper.getActivityStatus(
                appService, activityStatus));
        }
        return Specimen;
    }

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType)
        throws Exception {
        return newSpecimen(specimenType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING);
    }

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType,
        String activityStatus, ContainerWrapper container,
        CollectionEventWrapper cevent, Integer row, Integer col)
        throws Exception {
        SpecimenWrapper Specimen = newSpecimen(specimenType, activityStatus);
        if (container != null) {
            Specimen.setParent(container);
        }
        Specimen.setCollectionEvent(cevent);
        if ((row != null) && (col != null)) {
            Specimen.setPosition(new RowColPos(row, col));
        }
        return Specimen;
    }

    public static SpecimenWrapper newSpecimen(SpecimenTypeWrapper specimenType,
        ContainerWrapper container, CollectionEventWrapper cevent, Integer row,
        Integer col) throws Exception {
        return newSpecimen(specimenType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, cevent, row,
            col);
    }

    public static SpecimenWrapper addSpecimen(SpecimenTypeWrapper specimenType,
        String activityStatus, ContainerWrapper container,
        CollectionEventWrapper cevent, Integer row, Integer col)
        throws Exception {
        SpecimenWrapper Specimen = newSpecimen(specimenType, activityStatus,
            container, cevent, row, col);
        Specimen.persist();
        return Specimen;
    }

    public static SpecimenWrapper addSpecimen(SpecimenTypeWrapper specimenType,
        ContainerWrapper container, CollectionEventWrapper cevent, Integer row,
        Integer col) throws Exception {
        SpecimenWrapper Specimen = addSpecimen(specimenType,
            ActivityStatusWrapper.ACTIVE_STATUS_STRING, container, cevent, row,
            col);
        if (container != null)
            container.reload();
        return Specimen;
    }
}
