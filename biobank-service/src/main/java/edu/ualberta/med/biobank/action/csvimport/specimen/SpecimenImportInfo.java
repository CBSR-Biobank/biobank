package edu.ualberta.med.biobank.action.csvimport.specimen;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.action.csvimport.IImportInfo;
import edu.ualberta.med.biobank.action.specimen.SpecimenActionHelper;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Center;
import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;
import edu.ualberta.med.biobank.model.util.RowColPos;

/**
 * 
 * @author loyola
 * 
 */
@SuppressWarnings("nls")
public class SpecimenImportInfo implements IImportInfo {

    private static Logger log = LoggerFactory
        .getLogger(SpecimenImportInfo.class.getName());

    private final SpecimenCsvInfo csvInfo;
    private SpecimenImportInfo parentInfo;
    private Patient patient;
    private CollectionEvent cevent;
    private ProcessingEvent pevent;
    private Specimen parentSpecimen;
    private Center originCenter;
    private Center currentCenter;
    private SpecimenType specimenType;
    private Container container;
    private RowColPos specimenPos;
    private Specimen specimen;

    SpecimenImportInfo(SpecimenCsvInfo csvInfo) {
        this.csvInfo = csvInfo;
    }

    @Override
    public int getCsvLineNumber() {
        return csvInfo.getLineNumber();
    }

    public SpecimenCsvInfo getCsvInfo() {
        return csvInfo;
    }

    SpecimenImportInfo getParentInfo() {
        return parentInfo;
    }

    void setParentInfo(SpecimenImportInfo parentInfo) {
        if (parentInfo == null) {
            throw new IllegalStateException("parentInfo is null");
        }
        this.parentInfo = parentInfo;
        log.trace("setting parent info for specimen {} to {}",
            csvInfo.getInventoryId(), parentInfo.csvInfo.getInventoryId());
    }

    Patient getPatient() {
        return patient;
    }

    void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectionEvent getCevent() {
        return cevent;
    }

    void setCevent(CollectionEvent cevent) {
        this.cevent = cevent;
    }

    ProcessingEvent getPevent() {
        return pevent;
    }

    void setPevent(ProcessingEvent pevent) {
        this.pevent = pevent;
    }

    Specimen getParentSpecimen() {
        return parentSpecimen;
    }

    void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
        this.pevent = parentSpecimen.getProcessingEvent();
    }

    String getParentInventoryId() {
        return csvInfo.getParentInventoryId();
    }

    Center getOriginCenter() {
        return originCenter;
    }

    void setOriginCenter(Center originCenter) {
        this.originCenter = originCenter;
    }

    Center getCurrentCenter() {
        return currentCenter;
    }

    void setCurrentCenter(Center currentCenter) {
        this.currentCenter = currentCenter;
    }

    SpecimenType getSpecimenType() {
        return specimenType;
    }

    void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    Container getContainer() {
        return container;
    }

    void setContainer(Container container) {
        this.container = container;
    }

    RowColPos getSpecimenPos() {
        return specimenPos;
    }

    void setSpecimenPos(RowColPos specimenPos) {
        this.specimenPos = specimenPos;
    }

    boolean isSourceSpecimen() {
        return csvInfo.getSourceSpecimen();
    }

    boolean isAliquotedSpecimen() {
        return !csvInfo.getSourceSpecimen();
    }

    boolean hasWorksheet() {
        return (csvInfo.getWorksheet() != null)
            && !csvInfo.getWorksheet().isEmpty();
    }

    boolean hasPosition() {
        return (csvInfo.getPalletLabel() != null)
            && !csvInfo.getPalletLabel().isEmpty()
            && (csvInfo.getPalletPosition() != null)
            && !csvInfo.getPalletPosition().isEmpty();

    }

    CollectionEvent createCollectionEvent() {
        cevent = new CollectionEvent();
        cevent.setPatient(patient);
        cevent.setVisitNumber(csvInfo.getVisitNumber());
        cevent.setActivityStatus(ActivityStatus.ACTIVE);
        patient.getCollectionEvents().add(cevent);

        log.trace("created collection event: pt={} v#={} invId={}",
            new Object[] {
                csvInfo.getPatientNumber(),
                csvInfo.getVisitNumber(),
                csvInfo.getInventoryId()
            });

        return cevent;
    }

    ProcessingEvent createProcessingEvent() {
        if (parentSpecimen != null) {
            throw new IllegalStateException(
                "this specimen has a parent specimen and cannot have a processing event");
        }
        pevent = new ProcessingEvent();
        pevent.setWorksheet(csvInfo.getWorksheet());
        pevent.setCreatedAt(new Date());
        pevent.setCenter(currentCenter);
        pevent.setActivityStatus(ActivityStatus.ACTIVE);
        specimen.setProcessingEvent(pevent);

        log.trace("created processing event: worksheet={} parentSpc={}",
            csvInfo.getWorksheet(), csvInfo.getInventoryId());

        return getPevent();
    }

    Specimen getNewSpecimen() {
        if (cevent == null) {
            throw new IllegalStateException(
                "specimen does not have a collection event");
        }

        if ((csvInfo.getParentInventoryId() != null)
            && !csvInfo.getParentInventoryId().isEmpty()
            && (parentSpecimen == null)) {
            throw new IllegalStateException(
                "parent specimen for specimen with " + csvInfo.getInventoryId()
                    + " has not be created yet");
        }

        // add the specimen to the collection event
        OriginInfo oi = new OriginInfo();
        oi.setCenter(originCenter);

        specimen = new Specimen();
        specimen.setInventoryId(csvInfo.getInventoryId());
        specimen.setSpecimenType(specimenType);
        specimen.setCurrentCenter(currentCenter);
        specimen.setCollectionEvent(cevent);
        specimen.setOriginInfo(oi);
        specimen.setCreatedAt(csvInfo.getCreatedAt());
        specimen.setActivityStatus(ActivityStatus.ACTIVE);

        if (isSourceSpecimen()) {
            specimen.setOriginalCollectionEvent(cevent);
            cevent.getOriginalSpecimens().add(specimen);
        } else {
            ProcessingEvent pevent;

            if (parentSpecimen != null) {
                pevent = parentSpecimen.getProcessingEvent();
            } else {
                if (parentInfo.pevent == null) {
                    throw new IllegalStateException(
                        "parent specimen pevent is null");
                }
                pevent = parentInfo.pevent;
            }
            pevent.getSpecimens().add(specimen);
            SpecimenActionHelper.setParent(specimen, parentSpecimen);
            SpecimenActionHelper.setQuantityFromType(specimen);
        }
        cevent.getAllSpecimens().add(specimen);

        if (container != null) {
            SpecimenActionHelper.createOrChangePosition(specimen, container,
                specimenPos);
        }

        log.trace("creating specimen: pt={} v#={} invId={} isParent={}",
            new Object[] {
                csvInfo.getPatientNumber(),
                csvInfo.getVisitNumber(),
                csvInfo.getInventoryId(),
                specimen.getOriginalCollectionEvent() != null
            });

        return specimen;
    }
}
