package edu.ualberta.med.biobank.test.action;

import org.junit.Assert;
import org.junit.Test;

import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Study;

public class TestActivityStatus extends TestAction {
    @Test
    public void testPersistedId() {
        String name = getMethodNameR();

        Study study = new Study();
        study.setName(name);
        study.setNameShort(name);
        study.setActivityStatus(ActivityStatus.ACTIVE);

        Integer studyId = (Integer) session.save(study);

        for (ActivityStatus activityStatus : ActivityStatus.values()) {
            study.setActivityStatus(activityStatus);

            session.update(study);
            session.flush();

            Integer activityStatusId =
                (Integer) session
                    .createSQLQuery(
                        "select activity_status_id from study where id = ?")
                    .setParameter(0, studyId)
                    .list().iterator().next();

            Assert.assertTrue(
                "stored id " + activityStatusId
                    + " does not match ActivityStatus.getId() "
                    + activityStatus.getId() + " of ActivityStatus "
                    + activityStatus.name(),
                activityStatusId.intValue() == activityStatus.getId());
        }
    }
}
