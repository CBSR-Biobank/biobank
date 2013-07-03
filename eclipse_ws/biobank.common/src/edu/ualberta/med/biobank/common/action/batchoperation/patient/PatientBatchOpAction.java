package edu.ualberta.med.biobank.common.action.batchoperation.patient;

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
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationPatient;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * 
 * @author Nelson Loyola
 * 
 */
@SuppressWarnings("nls")
public class PatientBatchOpAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PatientBatchOpAction.class);

    private static final Bundle bundle = new CommonBundle();

    public static final int SIZE_LIMIT = 1000;

    public static final LString CSV_FILE_ERROR =
        bundle.tr("file not loaded").format();

    public static final Tr CSV_STUDY_ERROR =
        bundle.tr("study {0} does not exist");

    private static final LString PATIENT_ALREADY_EXISTS_ERROR =
        bundle.tr("patient already exists").format();

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    private final CompressedReference<ArrayList<PatientBatchOpInputPojo>> compressedList;

    private final FileData fileData;

    private ArrayList<PatientBatchOpInputPojo> pojos = null;

    private final Integer workingCenterId;

    private Center workingCenterOnServerSide;

    public PatientBatchOpAction(Center workingCenter,
        Set<PatientBatchOpInputPojo> inputPojos, File inputFile)
        throws IOException, NoSuchAlgorithmException {

        if (inputPojos.size() > SIZE_LIMIT) {
            throw new IllegalArgumentException("pojo list size exceeds maximum");
        }

        this.workingCenterId = workingCenter.getId();
        this.fileData = FileData.fromFile(inputFile);

        compressedList = new CompressedReference<ArrayList<PatientBatchOpInputPojo>>(
            new ArrayList<PatientBatchOpInputPojo>(inputPojos));
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
            && BatchOpActionUtil.hasPermissionOnStudies(user, getStudiesForValidNames(context));
    }

    /*
     * Returns the studies for the valid names only
     */
    private Set<Study> getStudiesForValidNames(ActionContext context) {
        Set<String> namesNotFound = new HashSet<String>();
        Map<String, Study> studiesByName = new HashMap<String, Study>();

        // the set will yield only the unique names
        for (PatientBatchOpInputPojo pojo : pojos) {
            String studyName = pojo.getStudyName();

            if (studiesByName.containsKey(studyName) || namesNotFound.contains(studyName)) {
                continue;
            }

            Study study = BatchOpActionUtil.getStudy(context.getSession(), studyName);

            if (study == null) {
                namesNotFound.add(studyName);
            } else {
                studiesByName.put(pojo.getStudyName(), study);
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

        Map<String, PatientBatchOpPojoData> pojoDataMap =
            new HashMap<String, PatientBatchOpPojoData>(0);

        for (PatientBatchOpInputPojo pojo : pojos) {
            PatientBatchOpPojoData pojoData = getDbInfo(context, pojo);

            if (pojoData != null) {
                pojoDataMap.put(pojo.getPatientNumber(), pojoData);
            }
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(
            context.getSession(), context.getUser(), fileData);

        for (PatientBatchOpPojoData pojoData : pojoDataMap.values()) {
            addPatient(context, batchOp, pojoData);
        }

        log.debug("run: exit");
        return new IdResult(batchOp.getId());
    }

    private PatientBatchOpPojoData getDbInfo(ActionContext context, PatientBatchOpInputPojo pojo) {
        Patient patient = BatchOpActionUtil.getPatient(context.getSession(), pojo.getPatientNumber());
        if (patient != null) {
            errorSet.addError(pojo.getLineNumber(), PATIENT_ALREADY_EXISTS_ERROR);
            return null;
        }

        Study study = BatchOpActionUtil.getStudy(context.getSession(), pojo.getStudyName());
        if (study == null) {
            errorSet.addError(pojo.getLineNumber(),
                CSV_STUDY_ERROR.format(pojo.getStudyName()));
        }

        PatientBatchOpPojoData pojoData = new PatientBatchOpPojoData(pojo);
        pojoData.setUser(context.getUser());
        pojoData.setStudy(study);
        return pojoData;
    }

    private void addPatient(ActionContext context,
        BatchOperation batchOp, PatientBatchOpPojoData pojoData) {
        if (context == null) {
            throw new NullPointerException("context is null");
        }

        if (workingCenterOnServerSide == null) {
            // workingCenterOnServerSide is assigned when isAllowed() is called
            throw new IllegalStateException("workingCenterOnServerSide is null");
        }

        Patient patient = pojoData.getNewPatient();

        // check if this patient has a comment and if so save it to DB
        if (!patient.getComments().isEmpty()) {
            Comment comment = patient.getComments().iterator().next();
            context.getSession().save(comment);
        }

        context.getSession().save(patient);

        BatchOperationPatient batchOpPt = new BatchOperationPatient();
        batchOpPt.setBatch(batchOp);
        batchOpPt.setPatient(patient);
        context.getSession().save(batchOpPt);
    }
}
