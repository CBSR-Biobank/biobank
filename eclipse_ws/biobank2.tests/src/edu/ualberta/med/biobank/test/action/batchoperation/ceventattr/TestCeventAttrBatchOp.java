package edu.ualberta.med.biobank.test.action.batchoperation.ceventattr;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import junit.framework.Assert;

import org.hibernate.criterion.Restrictions;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpAction;
import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.test.action.TestAction;
import edu.ualberta.med.biobank.test.action.batchoperation.CsvUtil;

public class TestCeventAttrBatchOp extends TestAction {

    private static Logger log = LoggerFactory.getLogger(TestCeventAttrBatchOp.class);

    private static final String CSV_NAME = "import_cevent_attr.csv";

    private CeventAttrBatchOpPojoHelper pojoHelper;

    private Study study;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        pojoHelper = new CeventAttrBatchOpPojoHelper(factory.getNameGenerator());

        // delete the CSV file if it exists
        File file = new File(CSV_NAME);
        file.delete();

        session.beginTransaction();
        factory.createSite();
        study = factory.createStudy();
        factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.TEXT);
        factory.createStudyEventAttr();

        factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.SELECT_SINGLE);
        factory.createStudyEventAttr();

        factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.SELECT_MULTIPLE);
        factory.createStudyEventAttr();

        factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.NUMBER);
        factory.createStudyEventAttr();

        // uncomment when a date-time global event attribute is added
        // factory.setDefaultEventAttrTypeEnum(EventAttrTypeEnum.DATE_TIME);
        // factory.createStudyEventAttr();
    }

    @Test
    public void noErrors() throws Exception {
        Set<Patient> patients = new HashSet<Patient>();
        patients.add(factory.createPatient());
        factory.createCollectionEvent();
        session.getTransaction().commit();

        Set<CeventAttrBatchOpInputPojo> pojos =
            pojoHelper.createCeventAttrs(study.getStudyEventAttrs(), patients);
        CeventCsvWriter.write(CSV_NAME, pojos);

        try {
            CeventAttrBatchOpAction importAction = new CeventAttrBatchOpAction(
                factory.getDefaultSite(), pojos, new File(CSV_NAME));
            exec(importAction);
        } catch (BatchOpErrorsException e) {
            CsvUtil.showErrorsInLog(log, e);
            Assert.fail("errors in CVS data: " + e.getMessage());
        }

        checkPojosAgainstDb(pojos);
    }

    private void checkPojosAgainstDb(Set<CeventAttrBatchOpInputPojo> pojos) {
        for (CeventAttrBatchOpInputPojo pojo : pojos) {
            EventAttr eventAttr = (EventAttr) session.createCriteria(EventAttr.class, "ea")
                .createAlias("ea.studyEventAttr", "sea")
                .createAlias("sea.globalEventAttr", "gea")
                .createAlias("ea.collectionEvent", "cevent")
                .createAlias("cevent.patient", "patient")
                .add(Restrictions.eq("patient.pnumber", pojo.getPatientNumber()))
                .add(Restrictions.eq("cevent.visitNumber", pojo.getVisitNumber()))
                .add(Restrictions.eq("gea.label", pojo.getAttrName()))
                .uniqueResult();

            Assert.assertEquals(pojo.getAttrValue(), eventAttr.getValue());
        }
    }

}
