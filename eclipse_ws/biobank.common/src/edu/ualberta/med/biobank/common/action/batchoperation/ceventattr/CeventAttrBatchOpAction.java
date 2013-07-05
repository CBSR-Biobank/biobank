package edu.ualberta.med.biobank.common.action.batchoperation.ceventattr;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.CommonBundle;
import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrTypeEnum;
import edu.ualberta.med.biobank.common.action.eventattr.EventAttrUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LocalizedException;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationEventAttr;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.EventAttr;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.GlobalEventAttr;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyEventAttr;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class CeventAttrBatchOpAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CeventAttrBatchOpAction.class);

    private static final Bundle bundle = new CommonBundle();

    public static final int SIZE_LIMIT = 1000;

    private static final Tr CEVENT_ERROR =
        bundle.tr("collection event does not exist for patient \"{0}\" and visit number {1}");

    private static final Tr STUDY_EVENT_ATTR_INVALID_ERROR =
        bundle.tr("event attribute with name \"{0}\" not defined in study");

    private static final Tr STUDY_EVENT_ATTR_LOCKED_ERROR =
        bundle.tr("event attribute with name \"{0}\" is locked in study");

    private static final Tr EVENT_ATTR_ALREADY_EXISTS_ERROR =
        bundle.tr("event attribute already exists for patient \"{0}\" and visit number {1}");

    private final CompressedReference<ArrayList<CeventAttrBatchOpInputPojo>> compressedList;

    private final FileData fileData;

    private final Integer workingCenterId;

    private Center workingCenterOnServerSide;

    private ArrayList<CeventAttrBatchOpInputPojo> pojos = null;

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    public CeventAttrBatchOpAction(Center workingCenter,
        Set<CeventAttrBatchOpInputPojo> inputPojos, File inputFile)
        throws IOException, NoSuchAlgorithmException {

        if (inputPojos.size() > SIZE_LIMIT) {
            throw new IllegalArgumentException("pojo list size exceeds maximum");
        }

        this.workingCenterId = workingCenter.getId();
        this.fileData = FileData.fromFile(inputFile);

        compressedList = new CompressedReference<ArrayList<CeventAttrBatchOpInputPojo>>(
            new ArrayList<CeventAttrBatchOpInputPojo>(inputPojos));
        log.debug("constructor exit");
    }

    private void decompressData() {
        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }

        try {
            pojos = compressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }

    }

    @Override
    public boolean isAllowed(ActionContext context) throws ActionException {
        log.debug("isAllowed: start");
        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }

        decompressData();
        User user = context.getUser();
        workingCenterOnServerSide = context.load(Center.class, workingCenterId);

        return PermissionEnum.BATCH_OPERATIONS.isAllowed(user, workingCenterOnServerSide)
            && BatchOpActionUtil.hasPermissionOnStudies(user, getStudiesForValidPatients(context));
    }

    /*
     * Returns the studies for the valid patient numbers only
     */
    private Set<Study> getStudiesForValidPatients(ActionContext context) {
        Set<String> pnumbersProcessed = new HashSet<String>();
        Map<String, Study> studiesByName = new HashMap<String, Study>();

        // the set will yield only the unique names
        for (CeventAttrBatchOpInputPojo pojo : pojos) {
            String pnumber = pojo.getPatientNumber();

            if (pnumbersProcessed.contains(pnumber)) {
                continue;
            }

            pnumbersProcessed.add(pnumber);

            Patient patient = BatchOpActionUtil.getPatient(context.getSession(), pnumber);
            if (patient != null) {
                Study study = patient.getStudy();
                studiesByName.put(study.getNameShort(), study);
            }
        }

        return new HashSet<Study>(studiesByName.values());

    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        log.debug("run: entry");

        if (fileData == null) {
            throw new IllegalStateException("file data is null");
        }

        if (pojos == null) {
            throw new IllegalStateException("pojos were not decompressed");
        }

        if (pojos.isEmpty()) {
            throw new IllegalStateException("pojo list is empty");
        }

        log.debug("run: checking for errors, possible event attrs {}",
            pojos.size());

        Set<CeventAttrBatchOpPojoData> pojoDataSet = new HashSet<CeventAttrBatchOpPojoData>(0);

        for (CeventAttrBatchOpInputPojo pojo : pojos) {
            CeventAttrBatchOpPojoData pojoData = getDbInfo(context, pojo);

            if (pojoData != null) {
                pojoDataSet.add(pojoData);
            }
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        log.debug("run: no errors found, adding {} event attrs to database",
            pojoDataSet.size());

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(
            context.getSession(), context.getUser(), fileData);

        for (CeventAttrBatchOpPojoData pojoData : pojoDataSet) {
            addEventAttr(context, batchOp, pojoData);
        }

        log.debug("run: exit");
        return new IdResult(batchOp.getId());
    }

    private CeventAttrBatchOpPojoData getDbInfo(ActionContext context,
        CeventAttrBatchOpInputPojo pojo) {
        CollectionEvent cevent = BatchOpActionUtil.getCollectionEvent(context.getSession(),
            pojo.getPatientNumber(), pojo.getVisitNumber());
        if (cevent == null) {
            errorSet.addError(pojo.getLineNumber(),
                CEVENT_ERROR.format(pojo.getPatientNumber(), pojo.getVisitNumber()));
            return null;
        }

        StudyEventAttr studyEventAttr = BatchOpActionUtil.getStudyEventAttr(context.getSession(),
            cevent.getPatient().getStudy(), pojo.getAttrName());
        if (studyEventAttr == null) {
            errorSet.addError(pojo.getLineNumber(),
                STUDY_EVENT_ATTR_INVALID_ERROR.format(pojo.getAttrName()));
            return null;
        }

        if (studyEventAttr.getActivityStatus() != ActivityStatus.ACTIVE) {
            errorSet.addError(pojo.getLineNumber(),
                STUDY_EVENT_ATTR_LOCKED_ERROR.format(pojo.getAttrName()));
            return null;
        }

        EventAttr eventAttr = BatchOpActionUtil.getEventAttr(context.getSession(),
            cevent, studyEventAttr);
        if (eventAttr != null) {
            errorSet.addError(pojo.getLineNumber(),
                EVENT_ATTR_ALREADY_EXISTS_ERROR.format(pojo.getPatientNumber(), pojo.getVisitNumber()));
            return null;
        }

        try {
            GlobalEventAttr globalEventAttr = studyEventAttr.getGlobalEventAttr();
            EventAttrUtil.validateValue(
                EventAttrTypeEnum.getEventAttrType(globalEventAttr.getEventAttrType().getName()),
                globalEventAttr.getLabel(),
                studyEventAttr.getPermissible(),
                pojo.getAttrValue());
        } catch (LocalizedException e) {
            errorSet.addError(pojo.getLineNumber(), e.getLocalizedString());
            return null;
        }

        CeventAttrBatchOpPojoData pojoData = new CeventAttrBatchOpPojoData(pojo);
        pojoData.setCollectionEvent(cevent);
        pojoData.setStudyEventAttr(studyEventAttr);
        return pojoData;
    }

    private void addEventAttr(ActionContext context,
        BatchOperation batchOp, CeventAttrBatchOpPojoData pojoData) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        if (workingCenterOnServerSide == null) {
            // workingCenterOnServerSide is assigned when isAllowed() is called
            throw new IllegalStateException("workingCenterOnServerSide is null");
        }

        EventAttr eventAttr = pojoData.getCeventEventAttr();
        context.getSession().save(eventAttr);

        log.trace("added event attr: pnumber: {}, visitNumber: {}, attrName: {}, attrValue: {}",
            new Object[] { eventAttr.getCollectionEvent().getPatient().getPnumber(),
                eventAttr.getCollectionEvent().getVisitNumber(),
                eventAttr.getStudyEventAttr().getGlobalEventAttr().getLabel(),
                eventAttr.getValue() });

        BatchOperationEventAttr batchOpPt = new BatchOperationEventAttr();
        batchOpPt.setBatch(batchOp);
        batchOpPt.setEventAttr(eventAttr);
        context.getSession().save(batchOpPt);

    }
}
