package edu.ualberta.med.biobank.test.action;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import edu.ualberta.med.biobank.common.action.activityStatus.ActivityStatusEnum;
import edu.ualberta.med.biobank.common.action.dispatch.DispatchSaveAction;
import edu.ualberta.med.biobank.common.action.info.DispatchSaveInfo;
import edu.ualberta.med.biobank.common.action.info.DispatchSpecimenInfo;
import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
import edu.ualberta.med.biobank.common.action.patient.PatientSaveAction;
import edu.ualberta.med.biobank.common.util.DispatchState;
import edu.ualberta.med.biobank.test.Utils;
import edu.ualberta.med.biobank.test.action.helper.DispatchHelper;
import edu.ualberta.med.biobank.test.action.helper.ShipmentInfoHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.StudyHelper;

public class TestDispatch extends TestAction {

    @Rule
    public TestName testname = new TestName();

    private String name;
    private Integer studyId;
    private Integer patientId;
    private Integer siteId;
    private Integer centerId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = testname.getMethodName() + r.nextInt();
        studyId =
            StudyHelper
                .createStudy(appService, name, ActivityStatusEnum.ACTIVE);
        siteId =
            SiteHelper.createSite(appService, name + "1", "Edmonton",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>(studyId));
        centerId =
            SiteHelper.createSite(appService, name + "2", "Calgary",
                ActivityStatusEnum.ACTIVE, new HashSet<Integer>(studyId));
        patientId =
            appService.doAction(new PatientSaveAction(null, studyId, name,
                Utils.getRandomDate())).getId();
    }

    @Test
    public void saveWithSpecs() throws Exception {
        DispatchSaveInfo d =
            DispatchHelper.createSaveDispatchInfoRandom(appService, siteId,
                centerId, DispatchState.CREATION.getId(),
                Utils.getRandomString(5));
        Set<DispatchSpecimenInfo> specs =
            DispatchHelper.createSaveDispatchSpecimenInfoRandom(appService,
                patientId, centerId);
        ShipmentInfoSaveInfo shipsave =
            ShipmentInfoHelper.createRandomShipmentInfo(appService);
        appService.doAction(new DispatchSaveAction(d, specs, shipsave));
    }
}
