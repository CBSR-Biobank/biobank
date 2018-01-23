package edu.ualberta.med.biobank.common.action.batchoperation.specimenPosition;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.SpecimenBatchOpActionErrors.*;
import static edu.ualberta.med.biobank.common.action.batchoperation.specimenPosition.PositionBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.i18n.LString;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.FileData;
import edu.ualberta.med.biobank.model.PermissionEnum;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.User;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.CompressedReference;

@SuppressWarnings("nls")
public class PositionBatchOpAction implements Action<IdResult> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PositionBatchOpAction.class);

    public static final int SIZE_LIMIT = 1000;

    private final Integer workingCenterId;

    private final FileData fileData;

    private final CompressedReference<ArrayList<PositionBatchOpPojo>> compressedList;

    private ArrayList<PositionBatchOpPojo> pojos = null;

    private Center workingCenterOnServerSide;

    public PositionBatchOpAction(Center workingCenter,
                                 Set<PositionBatchOpPojo> pojos,
                                 File inputFile)
                                                throws IOException, NoSuchAlgorithmException {

        if (pojos.size() > SIZE_LIMIT) {
            throw new IllegalArgumentException("pojo list size exceeds maximum");
        }

        this.workingCenterId = workingCenter.getId();
        this.fileData = FileData.fromFile(inputFile);

        compressedList = new CompressedReference<ArrayList<PositionBatchOpPojo>>(
            new ArrayList<PositionBatchOpPojo>(pojos));
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

        Set<Study> studies =
            BatchOpActionUtil.getStudiesForSpecimens(getValidSpecimens(context, pojos));
        return PermissionEnum.BATCH_OPERATIONS.isAllowed(user, workingCenterOnServerSide)
               && BatchOpActionUtil.hasPermissionOnStudies(user, studies);
    }

    @Override
    public IdResult run(ActionContext context) throws ActionException {
        log.debug("run: entry");
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

        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();
        Set<PositionBatchOpPojoInfo> pojoData = new HashSet<PositionBatchOpPojoInfo>(0);

        for (PositionBatchOpPojo pojo : pojos) {
            Pair<BatchOpInputErrorSet, PositionBatchOpPojoInfo> pojoInfoMaybe =
                getDbInfo(context, pojo);

            errors.addAll(pojoInfoMaybe.getLeft());
            pojoData.add(pojoInfoMaybe.getRight());
        }

        if (!errors.isEmpty()) {
            throw new BatchOpErrorsException(errors.getErrors());
        }

        BatchOperation batchOp = BatchOpActionUtil.createBatchOperation(context.getSession(),
                                                                        context.getUser(),
                                                                        fileData);

        for (PositionBatchOpPojoInfo pojoInfo : pojoData) {
            updatePostition(context, pojoInfo, batchOp);
        }

        log.debug("run: exit");
        return new IdResult(batchOp.getId());
    }

    /*
     * Returns all the specimens that correspond to the inventory IDs. If an inventory ID does not
     * exist, an error is returned in the errorSet.
     */
    private Set<Specimen> getValidSpecimens(ActionContext context, List<PositionBatchOpPojo> pojos) {
        Set<Specimen> specimens = new HashSet<Specimen>();

        for (PositionBatchOpPojo pojo : pojos) {
            String inventoryId = pojo.getInventoryId();
            if ((inventoryId == null) || inventoryId.isEmpty()) continue;
            Specimen specimen = BatchOpActionUtil.getSpecimen(context.getSession(), inventoryId);
            if (specimen != null) {
                specimens.add(specimen);
            }
        }
        return specimens;
    }

    private Pair<BatchOpInputErrorSet, PositionBatchOpPojoInfo> getDbInfo(ActionContext context,
                                                                          PositionBatchOpPojo pojo) {
        Session session = context.getSession();
        int lineNumber = pojo.getLineNumber();
        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();
        Container container;
        Specimen specimen = BatchOpActionUtil.getSpecimen(session, pojo.getInventoryId());

        if (specimen == null) {
            errors.addError(lineNumber, CSV_SPECIMEN_INVENTORY_INVALID_ERROR);
        }

        if (pojo.hasCurrentPosition()) {
            SpecimenPosition position = specimen.getSpecimenPosition();
            if (position == null) {
                errors.addError(lineNumber, CSV_SPECIMEN_HAS_NO_POSITION_ERROR);
            } else {
                String label = position.getContainer().getLabel();
                if (!label.equals(pojo.getCurrentPalletLabel())) {
                    errors.addError(lineNumber,
                                    CSV_SPECIMEN_PALLET_LABEL_INVALID_ERROR.format(label));
                }
            }
        }

        if (!pojo.hasLabelAndPosition() && !pojo.hasProductBarcodeAndPosition()) {
            if (!pojo.hasLabelAndPosition()) {
                errors.addError(lineNumber, CSV_LABEL_AND_POSITION_INVALID_ERROR);
            } else {
                errors.addError(lineNumber, CSV_BARCODE_AND_LABEEL_INFO_INVALID_ERROR);
            }
        }

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
            container = BatchOpActionUtil.getContainer(session, label);
            if (container == null) {
                errors.addError(lineNumber, CSV_CONTAINER_LABEL_ERROR.format(label));
            }
        } else {
            container = BatchOpActionUtil.getContainerByBarcode(session, barcode);
            if (container == null) {
                errors.addError(lineNumber,
                                CSV_CONTAINER_BARCODE_ERROR.format(barcode));
            }
        }

        SpecimenType specimenType = specimen.getSpecimenType();
        if (!container.getContainerType().getSpecimenTypes().contains(specimenType)) {
            log.info("getDbInfo: specimen with bad specimen type: inventoryId: "
                + specimen.getInventoryId()
                + ", specimen type: " + specimen.getSpecimenType().getName());
            errors.addError(lineNumber, CSV_CONTAINER_SPC_TYPE_ERROR.format(specimenType.getName()));
        }

        try {
            RowColPos position = container.getPositionFromLabelingScheme(palletPosition);

            // is container position empty?
            if (!container.isPositionFree(position)) {
                LString message = (hasLabel)
                    ? CSV_LABEL_POS_OCCUPIED_ERROR.format(position, label)
                    : CSV_CONTAINER_POS_OCCUPIED_ERROR.format(position, barcode);
                errors.addError(lineNumber, message);
            }

            if (errors.isEmpty()) {
                PositionBatchOpPojoInfo pojoData = new PositionBatchOpPojoInfo(pojo);
                pojoData.setSpecimen(specimen);
                pojoData.setContainer(container);
                pojoData.setPosition(position);
                return Pair.of(null, pojoData);
            }
        } catch (Exception e) {
            LString message = (hasLabel)
                ? CSV_LABEL_POS_OCCUPIED_ERROR.format(palletPosition, label)
                : CSV_CONTAINER_POS_OCCUPIED_ERROR.format(palletPosition, barcode);
            errors.addError(lineNumber, message);
        }
        return Pair.of(errors, null);
    }

    private void updatePostition(ActionContext           context,
                                 PositionBatchOpPojoInfo pojoInfo,
                                 BatchOperation          batchOp) {
        Session session = context.getSession();
        PositionBatchOpPojo pojo = pojoInfo.getPojo();
        Specimen specimen = pojoInfo.getSpecimen();
        Container container = pojoInfo.getContainer();

        specimen.setCurrentCenter(container.getSite());
        SpecimenActionHelper.setPosition(context,
                                         specimen,
                                         pojoInfo.getPosition(),
                                         container.getId());

        if ((pojo.getComment() != null) && !pojo.getComment().isEmpty()) {
            Comment comment = new Comment();
            comment.setMessage(pojo.getComment());
            comment.setUser(context.getUser());
            comment.setCreatedAt(new Date());
            specimen.getComments().add(comment);
            session.save(comment);
        }
        session.saveOrUpdate(specimen);

        BatchOperationSpecimen batchOpSpc = new BatchOperationSpecimen();
        batchOpSpc.setBatch(batchOp);
        batchOpSpc.setSpecimen(specimen);
        session.save(batchOpSpc);

        log.debug("updatePostition: specimen position added: inventoryId: "
            + specimen.getInventoryId()
            + ", container: " + container.getPositionString()
            + ", position: " + specimen.getSpecimenPosition().getPositionString());
    }

}
