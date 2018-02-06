package edu.ualberta.med.biobank.common.action.batchoperation.specimen.position;

import static edu.ualberta.med.biobank.common.action.batchoperation.specimen.position.PositionBatchOpActionErrors.*;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.IdResult;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpInputErrorSet;
import edu.ualberta.med.biobank.common.action.batchoperation.specimen.GenericSpecimenPositionBatchOpAction;
import edu.ualberta.med.biobank.common.action.exception.ActionException;
import edu.ualberta.med.biobank.common.action.exception.BatchOpErrorsException;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.BatchOperationSpecimen;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.util.CompressedReference;

@SuppressWarnings("nls")
public class PositionBatchOpAction extends GenericSpecimenPositionBatchOpAction<PositionBatchOpPojo> {
    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(PositionBatchOpAction.class);

    public PositionBatchOpAction(Center workingCenter,
                                 Set<PositionBatchOpPojo> pojos,
                                 File inputFile)
        throws IOException, NoSuchAlgorithmException, ClassNotFoundException {
        super(workingCenter,
              new CompressedReference<ArrayList<PositionBatchOpPojo>>(
                new ArrayList<PositionBatchOpPojo>(pojos)),
              inputFile);
    }

    @Override
    protected void decompressData() {
        super.decompressData();

        try {
            pojos = compressedList.get();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e);
        }
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

        Set<String> pojoInventoryIds = new HashSet<String>(0);
        BatchOpInputErrorSet errors = new BatchOpInputErrorSet();
        Set<PositionBatchOpPojoInfo> pojoData = new HashSet<PositionBatchOpPojoInfo>(0);

        for (PositionBatchOpPojo pojo : pojos) {
            String inventoryId = pojo.getInventoryId();
            if (pojoInventoryIds.contains(inventoryId)) {
                throw new IllegalStateException("inventory ID found more than once: " + inventoryId);
            }
            pojoInventoryIds.add(inventoryId);

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
    @Override
    protected Set<Specimen> getValidSpecimens(ActionContext context) {
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
        Specimen specimen = BatchOpActionUtil.getSpecimen(session, pojo.getInventoryId());

        if (specimen == null) {
            errors.addError(lineNumber,
                            CSV_SPECIMEN_INVENTORY_INVALID_ERROR.format(pojo.getInventoryId()));
            return Pair.of(errors, null);
        }

        if (pojo.hasCurrentPosition()) {
            SpecimenPosition position = specimen.getSpecimenPosition();
            if (position == null) {
                errors.addError(lineNumber, CSV_SPECIMEN_HAS_NO_POSITION_ERROR);
            } else {
                String label = position.getContainer().getLabel();
                if (!label.equals(pojo.getCurrentPalletLabel())) {
                    errors.addError(lineNumber,
                                    CSV_SPECIMEN_PALLET_LABEL_INVALID_ERROR
                                        .format(pojo.getCurrentPalletLabel()));
                }
            }
        }

        Pair<BatchOpInputErrorSet, SpecimenPositionPojoData> validation =
            validatePositionInfo(session,
                                 pojo,
                                 specimen.getSpecimenType().getName());

        BatchOpInputErrorSet validationErrors = validation.getLeft();
        errors.addAll(validationErrors);

        if (!errors.isEmpty()) {
            return Pair.of(errors, null);
        }

        SpecimenPositionPojoData info = validation.getRight();
        PositionBatchOpPojoInfo pojoData = new PositionBatchOpPojoInfo(pojo);
        pojoData.setSpecimen(info.specimen);
        pojoData.setContainer(info.container);
        pojoData.setPosition(info.specimenPosition);
        return Pair.of(null, pojoData);
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
