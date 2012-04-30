package edu.ualberta.med.biobank.test.wrappers.checks;

import junit.framework.Assert;

import org.junit.Test;

import edu.ualberta.med.biobank.common.wrappers.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.common.wrappers.WrapperTransaction;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.test.TestDatabase;

@Deprecated
public class TestChecks extends TestDatabase {
    @Test
    public void testTransactions() throws Exception {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName("a");
        study.setNameShort("a");
        study.setActivityStatus(ActivityStatus.ACTIVE);

        study.setStudyEventAttr("asdf", EventAttrTypeEnum.NUMBER,
            new String[] { "1" });

        study.persist();

        study.deleteStudyEventAttr("asdf");
        study.setStudyEventAttr("jkl", EventAttrTypeEnum.NUMBER,
            new String[] { "1" });

        study.persist();

        StudyWrapper study2 = new StudyWrapper(appService);
        study2.setName("b");
        study2.setNameShort("b");
        study2.setActivityStatus(ActivityStatus.ACTIVE);

        StudyWrapper study3 = new StudyWrapper(appService);
        study3.setName("c");
        study3.setNameShort("c");
        study3.setActivityStatus(ActivityStatus.ACTIVE);

        WrapperTransaction tx = new WrapperTransaction(appService);

        tx.persist(study);
        tx.persist(study2);
        tx.persist(study3);
        tx.delete(study);
        tx.delete(study2);
        tx.delete(study3);

        tx.commit();

    }

    @Test
    public void checkNumbers() throws Exception {
        Integer i = 0;
        Long l = 0L;

        if (!i.equals(l)) {
            System.out.println("not equal!");
        }

        Assert.assertEquals(i, l);
    }
}
