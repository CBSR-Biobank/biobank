package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes a list of Specimen Batch Operation beans as input, verifies that the data is
 * valid, and if valid saves the data to the database.
 *
 * @author Nelson Loyola
 *
 */
@SuppressWarnings("nls")
public abstract class CommonSpecimenBatchOpAction<T extends IBatchOpSpecimenInputPojo>
    implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(SpecimenBatchOpAction.class);

    public static final int SIZE_LIMIT = 1000;

    private final Integer workingCenterId;

    protected final CompressedReference<ArrayList<T>> compressedList;

    protected Center workingCenterOnServerSide;

    protected final FileData fileData;

    protected ArrayList<T> pojos = null;

    public CommonSpecimenBatchOpAction(Center workingCenter,
                                       CompressedReference<ArrayList<T>> compressedList,
                                       File inputFile)
        throws NoSuchAlgorithmException, IOException, ClassNotFoundException {

        if (compressedList.get().isEmpty()) {
            throw new IllegalArgumentException("pojo list is empty");
        }

        int size = compressedList.get().size();
        if (size > SIZE_LIMIT) {
            throw new IllegalArgumentException(
                "Number of rows in file greater than maximum: " + size);
        }

        this.workingCenterId = workingCenter.getId();
        this.compressedList = compressedList;
        this.fileData = FileData.fromFile(inputFile);
        log.debug("constructor exit");
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
            && hasPermissionOnStudies(user, getStudies(context));

    }

    protected void decompressData() {
        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }
    }

    protected boolean hasPermissionOnStudies(User user, Set<Study> studies) {
        for (Study study : studies) {
            if (!PermissionEnum.BATCH_OPERATIONS.isAllowed(user, study)) {
                return false;
            }
        }
        return true;
    }

    /*
     * Returns a list of studies that existing specimens and patients in the pojo data belong to.
     */
    protected Set<Study> getStudies(ActionContext context) {
        Set<Specimen> existingSpecimens = new HashSet<Specimen>();
        Set<Patient> existingPatients = new HashSet<Patient>();

        for (T pojo : pojos) {
            String parentInvId = pojo.getParentInventoryId();
            if ((parentInvId == null) || parentInvId.isEmpty()) continue;
            Specimen specimen = BatchOpActionUtil.getSpecimen(context.getSession(), parentInvId);
            if (specimen != null) {
                existingSpecimens.add(specimen);
            }
        }

        for (T pojo : pojos) {
            String pnumber = pojo.getPatientNumber();
            if ((pnumber == null) || pnumber.isEmpty()) continue;
            Patient patient = BatchOpActionUtil.getPatient(context.getSession(), pnumber);
            if (patient != null) {
                existingPatients.add(patient);
            }
        }

        // get all collection events
        for (Specimen specimen : existingSpecimens) {
            specimen.getCollectionEvent();
        }

        // get all patients from specimens
        for (Specimen specimen : existingSpecimens) {
            specimen.getCollectionEvent().getPatient();
        }

        Set<Study> studies = new HashSet<Study>();
        for (Specimen specimen : existingSpecimens) {
            studies.add(specimen.getCollectionEvent().getPatient().getStudy());
        }
        for (Patient patient : existingPatients) {
            studies.add(patient.getStudy());
        }
        return studies;
    }

}
