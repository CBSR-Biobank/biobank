package edu.ualberta.med.biobank.test.action;

import java.util.ArrayList;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventGetInfoAction.CEventInfo;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction;
import edu.ualberta.med.biobank.common.action.collectionEvent.CollectionEventSaveAction.SaveCEventSpecimenInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenGetInfoAction.SpecimenBriefInfo;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.test.action.helper.CollectionEventHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper;
import edu.ualberta.med.biobank.test.action.helper.SiteHelper.Provisioning;

public class TestSpecimen extends TestAction {

    private String name;

    private Provisioning provisioning;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();
        name = getMethodNameR();
        provisioning =
            SiteHelper.provisionProcessingConfiguration(EXECUTOR, name);
    }

    @Test
    public void saveNew() throws Exception {

    }

    @Test
    public void checkGetAction() throws Exception {
        final Integer typeId = getSpecimenTypes().get(0).getId();
        final Map<String, SaveCEventSpecimenInfo> specs =
            CollectionEventHelper.createSaveCEventSpecimenInfoRandomList(5,
                typeId, EXECUTOR.getUserId(), provisioning.siteId);

        // Save a new cevent
        final Integer ceventId = EXECUTOR.exec(
            new CollectionEventSaveAction(null, provisioning.patientIds
                .get(0),
                R.nextInt(20) + 1, 1, null,
                new ArrayList<SaveCEventSpecimenInfo>(specs.values()),
                null))
            .getId();

        CEventInfo ceventInfo =
            EXECUTOR.exec(new CollectionEventGetInfoAction(ceventId));

        SpecimenInfo specimenInfo = ceventInfo.sourceSpecimenInfos.get(0);

        SpecimenBriefInfo specimenBriefInfo =
            EXECUTOR.exec(new SpecimenGetInfoAction(specimenInfo.specimen
                .getId()));

        Assert.assertEquals(specimenInfo.specimen, specimenBriefInfo.specimen);
    }
}
