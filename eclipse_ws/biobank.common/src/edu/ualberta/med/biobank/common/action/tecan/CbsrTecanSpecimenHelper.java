package edu.ualberta.med.biobank.common.action.tecan;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.ualberta.med.biobank.model.CollectionEvent;
import edu.ualberta.med.biobank.model.OriginInfo;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.SpecimenType;

public class CbsrTecanSpecimenHelper {

    private static Logger log = LoggerFactory
        .getLogger(CbsrTecanSpecimenHelper.class.getName());

    private CbsrTecanCsvRow csvRow;
    private Patient patient;
    private CollectionEvent cevent;
    private ProcessingEvent pevent;
    private Specimen parentSpecimen;
    private OriginInfo originInfo;
    private SpecimenType specimenType;
    private Specimen specimen;

    public CbsrTecanSpecimenHelper(CbsrTecanCsvRow csvRow) {
        this.csvRow = csvRow;
    }

    public CbsrTecanCsvRow getCsvRow() {
        return csvRow;
    }

    public void setCsvRow(CbsrTecanCsvRow csvRow) {
        this.csvRow = csvRow;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public CollectionEvent getCevent() {
        return cevent;
    }

    public void setCevent(CollectionEvent cevent) {
        this.cevent = cevent;
    }

    public ProcessingEvent getPevent() {
        return pevent;
    }

    public void setPevent(ProcessingEvent pevent) {
        this.pevent = pevent;
    }

    public Specimen getParentSpecimen() {
        return parentSpecimen;
    }

    public void setParentSpecimen(Specimen parentSpecimen) {
        this.parentSpecimen = parentSpecimen;
    }

    public OriginInfo getOriginInfo() {
        return originInfo;
    }

    public void setOriginInfo(OriginInfo originInfo) {
        this.originInfo = originInfo;
    }

    public SpecimenType getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(SpecimenType specimenType) {
        this.specimenType = specimenType;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

}
