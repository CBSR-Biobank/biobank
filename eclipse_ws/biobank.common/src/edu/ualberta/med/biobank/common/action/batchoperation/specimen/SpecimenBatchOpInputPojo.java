package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.math.BigDecimal;
import java.util.Date;

/**
 * POJO used by the Specimen Batch Operation feature to import specimen information. The information
 * can come from the following sources:
 *
 * <ol>
 * <li>A legacy import of specimen data</li>
 * <li>A TECAN output file. This can be a TECAN output file from OHS or CBSR.</li>
 * </ol>
 *
 * @author Nelson Loyola
 *
 */
public class SpecimenBatchOpInputPojo implements
                                     IBatchOpSpecimenInputPojo,
                                     IBatchOpSpecimenPositionPojo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private String inventoryId;
    private String parentInventoryId;
    private BigDecimal volume;
    private String specimenType;
    private Date createdAt;
    private String patientNumber;
    private Integer visitNumber;
    private String waybill;
    private Boolean sourceSpecimen = false;
    private String worksheet;
    private String originCenter;
    private String currentCenter;
    private String palletProductBarcode;
    private String rootContainerType;
    private String palletLabel;
    private String palletPosition;
    private String comment;
    private String plateErrors;
    private String samplEerrors;
    private BigDecimal concentrationAbs;
    private BigDecimal concentrationFluor;
    private BigDecimal od260Over280;
    private BigDecimal od260Over230;
    private BigDecimal aliquotYield;

    @Override
    public int getLineNumber() {
        return lineNumber;
    }

    @Override
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public String getInventoryId() {
        return inventoryId;
    }

    @Override
    public void setInventoryId(String inventoryId) {
        this.inventoryId = inventoryId;
    }

    @Override
    public String getParentInventoryId() {
        return parentInventoryId;
    }

    public void setParentInventoryId(String parentInventoryID) {
        this.parentInventoryId = parentInventoryID;
    }

    public BigDecimal getVolume() {
        return volume;
    }

    public void setVolume(BigDecimal volume) {
        this.volume = volume;
    }

    public String getSpecimenType() {
        return specimenType;
    }

    public void setSpecimenType(String specimenType) {
        this.specimenType = specimenType;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createAt) {
        this.createdAt = createAt;
    }

    @Override
    public String getPatientNumber() {
        return patientNumber;
    }

    public void setPatientNumber(String patientNumber) {
        this.patientNumber = patientNumber;
    }

    public Integer getVisitNumber() {
        return visitNumber;
    }

    public void setVisitNumber(Integer visitNumber) {
        this.visitNumber = visitNumber;
    }

    public String getWaybill() {
        return waybill;
    }

    public void setWaybill(String waybill) {
        this.waybill = waybill;
    }

    public Boolean getSourceSpecimen() {
        return sourceSpecimen;
    }

    public void setSourceSpecimen(Boolean sourceSpecimen) {
        this.sourceSpecimen = sourceSpecimen;
    }

    public String getWorksheet() {
        return worksheet;
    }

    public void setWorksheet(String worksheet) {
        this.worksheet = worksheet;
    }

    public String getOriginCenter() {
        return originCenter;
    }

    @Override
    public void setOriginCenter(String originCenter) {
        this.originCenter = originCenter;
    }

    public String getCurrentCenter() {
        return currentCenter;
    }

    @Override
    public void setCurrentCenter(String currentCenter) {
        this.currentCenter = currentCenter;
    }

    @Override
    public String getPalletProductBarcode() {
        return palletProductBarcode;
    }

    @Override
    public void setPalletProductBarcode(String palletProductBarcode) {
        this.palletProductBarcode = palletProductBarcode;
    }

    @Override
    public String getRootContainerType() {
        return rootContainerType;
    }

    @Override
    public void setRootContainerType(String rootContainerType) {
        this.rootContainerType = rootContainerType;
    }

    @Override
    public String getPalletLabel() {
        return palletLabel;
    }

    @Override
    public void setPalletLabel(String palletLabel) {
        this.palletLabel = palletLabel;
    }

    @Override
    public String getPalletPosition() {
        return palletPosition;
    }

    @Override
    public void setPalletPosition(String palletPosition) {
        this.palletPosition = palletPosition;
    }

    @Override
    public String getComment() {
        return comment;
    }

    @Override
    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPlateErrors() {
        return plateErrors;
    }

    public void setPlateErrors(String plateErrors) {
        this.plateErrors = plateErrors;
    }

    public String getSamplEerrors() {
        return samplEerrors;
    }

    public void setSamplEerrors(String samplEerrors) {
        this.samplEerrors = samplEerrors;
    }

    public BigDecimal getConcentrationAbs() {
        return concentrationAbs;
    }

    public void setConcentrationAbs(BigDecimal concentrationAbs) {
        this.concentrationAbs = concentrationAbs;
    }

    public BigDecimal getConcentrationFluor() {
        return concentrationFluor;
    }

    public void setConcentrationFluor(BigDecimal concentrationFluor) {
        this.concentrationFluor = concentrationFluor;
    }

    public BigDecimal getOd260Over280() {
        return od260Over280;
    }

    public void setOd260Over280(BigDecimal od260Over280) {
        this.od260Over280 = od260Over280;
    }

    public BigDecimal getOd260Over230() {
        return od260Over230;
    }

    public void setOd260Over230(BigDecimal od260Over230) {
        this.od260Over230 = od260Over230;
    }

    public BigDecimal getAliquotYield() {
        return aliquotYield;
    }

    public void setAliquotYield(BigDecimal aliquotYield) {
        this.aliquotYield = aliquotYield;
    }

    public boolean hasPatientAndCollectionEvent() {
        return (patientNumber != null) && (visitNumber != null);
    }

    @Override
    public boolean hasPalletPosition() {
        return (palletPosition != null) && !palletPosition.isEmpty();
    }

    @Override
    public boolean hasProductBarcodeAndPosition() {
        return (palletProductBarcode != null) && !palletProductBarcode.isEmpty()
            && (palletPosition != null) && !palletPosition.isEmpty();
    }

    @Override
    public boolean hasLabelAndPosition() {
        return (rootContainerType != null) && !rootContainerType.isEmpty()
            && (palletLabel != null) && !palletLabel.isEmpty()
            && (palletPosition != null) && !palletPosition.isEmpty();
    }

    public boolean hasProductBarcodeAndLabel() {
        return (palletProductBarcode != null)  && !palletProductBarcode.isEmpty()
            && (palletLabel != null) && !palletLabel.isEmpty();
    }

    @Override
    public boolean hasPositionInfo() {
        return (rootContainerType != null) && !rootContainerType.isEmpty()
            || (palletLabel != null) && !palletLabel.isEmpty()
            || (palletProductBarcode != null) && !palletProductBarcode.isEmpty()
            || (palletPosition != null) && !palletPosition.isEmpty();
    }

}
