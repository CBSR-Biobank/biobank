package edu.ualberta.med.biobank.test.action.batchoperation.ceventattr;

import java.util.HashSet;
import java.util.Set;

import edu.ualberta.med.biobank.common.action.batchoperation.ceventattr.CeventAttrBatchOpInputPojo;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrTypeEnum;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.test.NameGenerator;

public class CeventAttrBatchOpPojoHelper {
    private final NameGenerator nameGenerator;

    CeventAttrBatchOpPojoHelper(NameGenerator nameGenerator) {
        this.nameGenerator = nameGenerator;
    }

    Set<CeventAttrBatchOpInputPojo> createCeventAttrs(
        Set<StudyEventAttr> studyEventAttrs, Set<Patient> patients) {
        Set<CeventAttrBatchOpInputPojo> csvInfos = new HashSet<CeventAttrBatchOpInputPojo>();

        for (StudyEventAttr studyEventAttr : studyEventAttrs) {
            for (Patient patient : patients) {
                CeventAttrBatchOpInputPojo pojo = new CeventAttrBatchOpInputPojo();
                pojo.setPatientNumber(patient.getPnumber());
                pojo.setVisitNumber(patient.getCollectionEvents().size());
                pojo.setAttrName(studyEventAttr.getGlobalEventAttr().getLabel());

                String value = null;
                String eventAttrTypeName = studyEventAttr.getGlobalEventAttr().getEventAttrType().getName();
                switch (EventAttrTypeEnum.getEventAttrType(eventAttrTypeName)) {
                case TEXT:
                    value = nameGenerator.next(EventAttr.class);
                    break;
                case SELECT_SINGLE:
                    value = "Option1";
                    break;
                case SELECT_MULTIPLE:
                    value = "Option2;Option3";
                    break;
                case NUMBER:
                    value = "12.34";
                    break;
                case DATE_TIME:
                    value = "2000-01-01 12:00";
                    break;
                }

                pojo.setAttrValue(value);
                csvInfos.add(pojo);
            }
        }
        return csvInfos;
    }

}
