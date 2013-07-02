package edu.ualberta.med.biobank.common.action.batchoperation.eventattr;

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
import edu.ualberta.med.biobank.common.action.batchoperation.patient.PatientBatchOpPojoData;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.i18n.Bundle;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.i18n.Tr;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.Center;
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
public class CollectionEventBatchOpAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CollectionEventBatchOpAction.class);

    private static final Bundle bundle = new CommonBundle();

    public static final int SIZE_LIMIT = 1000;

    public static final Tr CSV_STUDY_ERROR =
        bundle.tr("patient {0} does not exist");

    private static final LString EVENT_ATTR_ALREADY_EXISTS_ERROR =
        bundle.tr("patient already exists").format();

    private final CompressedReference<ArrayList<CeventAttrBatchOpInputPojo>> compressedList;

    private final FileData fileData;

    private final Integer workingCenterId;

    private Center workingCenterOnServerSide;

    private ArrayList<CeventAttrBatchOpInputPojo> pojos = null;

    private final BatchOpInputErrorSet errorSet = new BatchOpInputErrorSet();

    public CollectionEventBatchOpAction(Center workingCenter,
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

        Map<String, CeventAttrBatchOpInputPojo> pojoDataMap =
            new HashMap<String, CeventAttrBatchOpInputPojo>(0);

        for (CeventAttrBatchOpInputPojo pojo : pojos) {
            CeventAttrBatchOpPojoData pojoData = getDbInfo(context, pojo);

            if (pojoData != null) {
                pojoDataMap.put(pojo.getPatientNumber(), pojoData);
            }
        }

        if (!errorSet.isEmpty()) {
            throw new BatchOpErrorsException(errorSet.getErrors());
        }

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(
            context.getSession(), context.getUser(), fileData);

        for (CeventAttrBatchOpInputPojo pojoData : pojoDataMap.values()) {
            addPatient(context, batchOp, pojoData);
        }

        log.debug("run: exit");
        return new IdResult(batchOp.getId());
    }

    private CeventAttrBatchOpPojoData getDbInfo(ActionContext context,
        CeventAttrBatchOpInputPojo pojo) {
        Patient spc = BatchOpActionUtil.getPatient(context.getSession(), pojo.getPatientNumber());

        PatientBatchOpPojoData pojoData = new PatientBatchOpPojoData(pojo);
        pojoData.setUser(context.getUser());
        pojoData.setStudy(study);
        return pojoData;
    }

}
