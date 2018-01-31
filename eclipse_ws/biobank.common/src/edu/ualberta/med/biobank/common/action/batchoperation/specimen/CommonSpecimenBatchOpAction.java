package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.common.action.ActionContext;
import edu.ualberta.med.biobank.common.action.batchoperation.BatchOpActionUtil;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.util.CompressedReference;

/**
 * This action takes a list of Specimen Batch Operation beans as input, verifies that the data is
 * valid, and if valid saves the data to the database.
 *
 * @author Nelson Loyola
 *
 */
public abstract class CommonSpecimenBatchOpAction<T extends IBatchOpSpecimenInputPojo>
    extends GenericSpecimenPositionBatchOpAction<T> {

    private static final long serialVersionUID = 1L;

    private static Logger log = LoggerFactory.getLogger(CommonSpecimenBatchOpAction.class);

    public CommonSpecimenBatchOpAction(Center workingCenter,
                                       CompressedReference<ArrayList<T>> compressedList,
                                       File inputFile)
        throws NoSuchAlgorithmException, IOException, ClassNotFoundException {
        super(workingCenter, compressedList, inputFile);
    }

    /*
     * Returns a list of studies that existing specimens and patients in the pojo data belong to.
     */
    @Override
    protected Set<Study> getStudiesForValidSpecimens(ActionContext context) {
        Set<Study> studies = super.getStudiesForValidSpecimens(context);
        Set<Patient> existingPatients = new HashSet<Patient>();
        for (T pojo : pojos) {
            String pnumber = pojo.getPatientNumber();
            if ((pnumber == null) || pnumber.isEmpty()) continue;
            Patient patient = BatchOpActionUtil.getPatient(context.getSession(), pnumber);
            if (patient != null) {
                existingPatients.add(patient);
            }
        }

        for (Patient patient : existingPatients) {
            studies.add(patient.getStudy());
        }
        return studies;
    }

    @Override
    protected Set<Specimen> getValidSpecimens(ActionContext context) {
        Set<Specimen> existingSpecimens = new HashSet<Specimen>();

        for (T pojo : pojos) {
            String parentInvId = pojo.getParentInventoryId();
            if ((parentInvId == null) || parentInvId.isEmpty()) continue;
            Specimen specimen = BatchOpActionUtil.getSpecimen(context.getSession(), parentInvId);
            if (specimen != null) {
                existingSpecimens.add(specimen);
            }
        }
        return existingSpecimens;
    }

    /*
     * Creates a new specimen.
     *
     * <p>
     * Adds this specimen to the collection event associated with the parent specimen.
     *
     * @return the new specimen.
     */
    @SuppressWarnings("nls")
    protected Specimen createSpecimen(ActionContext   context,
                                      String          inventoryId,
                                      Specimen        parentSpecimen,
                                      CollectionEvent cevent,
                                      ProcessingEvent pevent,
                                      SpecimenType    specimenType,
                                      boolean         isSourceSpecimen,
                                      Center          currentCenter,
                                      OriginInfo      originInfo,
                                      Date            createdAt,
                                      BigDecimal      volume,
                                      String          commentMessage,
                                      Container       container,
                                      RowColPos       specimenPosition) {

        Specimen specimen = new Specimen();

        if (currentCenter == null) {
            currentCenter = originInfo.getCenter();
        }

        specimen.setInventoryId(inventoryId);
        specimen.setSpecimenType(specimenType);
        specimen.setCurrentCenter(currentCenter);
        specimen.setCollectionEvent(cevent);
        specimen.setOriginInfo(originInfo);
        specimen.setCreatedAt(createdAt);
        specimen.setActivityStatus(ActivityStatus.ACTIVE);

        if ((parentSpecimen == null) && (cevent == null)) {
            throw new IllegalStateException("parent specimen and event are null");
        } else if (cevent == null) {
            cevent = parentSpecimen.getCollectionEvent();
        } else {
            SpecimenActionHelper.setParent(specimen, parentSpecimen);
        }

        if ((commentMessage != null) && !commentMessage.isEmpty()) {
            Comment comment = new Comment();
            comment.setMessage(commentMessage);
            comment.setUser(context.getUser());
            comment.setCreatedAt(new Date());
            specimen.getComments().add(comment);
            context.getSession().save(comment);
        }

        if (isSourceSpecimen) {
            specimen.setOriginalCollectionEvent(cevent);
            cevent.getOriginalSpecimens().add(specimen);

            if (pevent != null) {
                specimen.setProcessingEvent(pevent);
                pevent.getSpecimens().add(specimen);
            }
        } else {
            if (volume != null) {
                specimen.setQuantity(volume);
            } else {
                SpecimenActionHelper.setQuantityFromType(specimen);
            }
        }

        cevent.getAllSpecimens().add(specimen);

        if (container != null) {
            SpecimenActionHelper.createOrChangePosition(specimen, container, specimenPosition);
        }

        log.trace("creating specimen: pt={} invId={} isParent={}",
            new Object[] {
                cevent.getPatient().getPnumber(),
                inventoryId,
                specimen.getOriginalCollectionEvent() != null
            });

        return specimen;
    }

}
