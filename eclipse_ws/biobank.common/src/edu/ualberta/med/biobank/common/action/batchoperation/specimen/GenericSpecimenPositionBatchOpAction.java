package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.Action;
import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * Base class for specimen batch operation actions that allow importing of specimen position
 * information.
 *
 * @author nelson
 *
 * @param <T> The pojo that represents one row in the CSV file processed by the derived action.
 */
@SuppressWarnings("nls")
public abstract class GenericSpecimenPositionBatchOpAction<T extends IBatchOpSpecimenPositionPojo>
    implements Action<IdResult> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(GenericSpecimenPositionBatchOpAction.class);

    public static final int SIZE_LIMIT = 1000;

    protected final Integer workingCenterId;

    protected final CompressedReference<ArrayList<T>> compressedList;

    protected Center workingCenterOnServerSide;

    protected final FileData fileData;

    protected ArrayList<T> pojos = null;

    public static class SpecimenPositionPojoData {
        public Specimen specimen;
        public RowColPos specimenPosition;
        public Container container;
    }

    public GenericSpecimenPositionBatchOpAction(Center workingCenter,
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
            && BatchOpActionUtil.hasPermissionOnStudies(user, getStudiesForValidSpecimens(context));

    }

    protected Set<Study> getStudiesForValidSpecimens(ActionContext context) {
        Set<Specimen> specimens = getValidSpecimens(context);
        Set<Study> studies = new HashSet<Study>();
        for (Specimen specimen : specimens) {
            studies.add(specimen.getCollectionEvent().getPatient().getStudy());
        }
        return studies;
    }

    protected abstract Set<Specimen> getValidSpecimens(ActionContext context);

    protected void decompressData() {
        if (compressedList == null) {
            throw new IllegalStateException("compressed list is null");
        }
    }

    protected Pair<BatchOpInputErrorSet, SpecimenPositionPojoData>
    validatePositionInfo(Session session,
                         IBatchOpSpecimenPositionPojo pojo,
                         String specimenTypeName) {
        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();
        int lineNumber = pojo.getLineNumber();
        Specimen specimen = BatchOpActionUtil.getSpecimen(session, pojo.getInventoryId());
        Container container;
        String palletPosition = pojo.getPalletPosition();
        String barcode = pojo.getPalletProductBarcode();
        String label = pojo.getPalletLabel();
        String rootContainerType = pojo.getRootContainerType();
        boolean hasPosition = (palletPosition != null) && !palletPosition.isEmpty();
        boolean hasProductBarcode = (barcode != null) && !barcode.isEmpty();
        boolean hasLabel = (label != null) && !label.isEmpty();
        boolean hasRootContainerType = (rootContainerType != null) && !rootContainerType.isEmpty();

        if (!hasPosition && !hasProductBarcode && !hasLabel) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_POS_INFO_INALID_ERROR);
        }

        if (hasPosition && !hasProductBarcode && !hasLabel) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_POS_ERROR);
        }

        if (hasLabel && !hasProductBarcode && !hasPosition) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_POS_ERROR);
        }

        if (hasProductBarcode && !hasLabel && !hasPosition) {
            errors.addError(pojo.getLineNumber(), CSV_PROD_BARCODE_NO_POS_ERROR);
        }

        if (hasLabel && hasPosition && !hasRootContainerType) {
            errors.addError(pojo.getLineNumber(), CSV_PALLET_LABEL_NO_CTYPE_ERROR);
        }

        if (!errors.isEmpty()) {
            return Pair.of(errors, null);
        }

        if (hasLabel) {
            String rootContainerTypeNameShort = pojo.getRootContainerType();
            container = BatchOpActionUtil.getContainer(session,
                                                       label,
                                                       rootContainerTypeNameShort);
            if (container == null) {
                errors.addError(lineNumber,
                                CSV_CONTAINER_LABEL_ROOT_CONTAINER_TYPE_ERROR.format(label,
                                                                                     rootContainerTypeNameShort));
            }
        } else {
            container = BatchOpActionUtil.getContainerByBarcode(session, barcode);
            if (container == null) {
                errors.addError(lineNumber,
                                CSV_CONTAINER_BARCODE_ERROR.format(barcode));
            }
        }

        if (!errors.isEmpty()) {
            return Pair.of(errors, null);
        }

        SpecimenType specimenType = BatchOpActionUtil.getSpecimenType(session, specimenTypeName);
        if (!container.getContainerType().getSpecimenTypes().contains(specimenType)) {
            errors.addError(lineNumber, CSV_CONTAINER_SPC_TYPE_ERROR.format(specimenType.getName()));
        }

        try {
            RowColPos position = container.getPositionFromLabelingScheme(palletPosition);

            // is container position empty?
            if (!container.isPositionFree(position)) {
                LString message =
                    (hasLabel) ? CSV_LABEL_POS_OCCUPIED_ERROR.format(position, label)
                              : CSV_CONTAINER_POS_OCCUPIED_ERROR.format(position, barcode);
                errors.addError(lineNumber, message);
            }

            if (errors.isEmpty()) {
                SpecimenPositionPojoData pojoData = new SpecimenPositionPojoData();
                pojoData.specimen = specimen;
                pojoData.container = container;
                pojoData.specimenPosition = position;
                return Pair.of(null, pojoData);
            }
        } catch (IllegalArgumentException e) {
            errors.addError(lineNumber, CSV_SPECIMEN_LABEL_ERROR.format(palletPosition,
                                                                        container.getLabel()));
        } catch (Exception e) {
            throw new IllegalStateException("failed to assign position");
        }
        return Pair.of(errors, null);
    }

}
