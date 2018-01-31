package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import java.math.BigDecimal;
import java.util.Date;

/**
 * POJO used by the Grandchild Specimen Batch Operation feature to import specimen information.
 *
 * @author Nelson Loyola
 *
 */
public class GrandchildSpecimenBatchOpInputPojo implements
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
    private String originCenter;
    private String currentCenter;
    private String palletProductBarcode;
    private String rootContainerType;
    private String palletLabel;
    private String palletPosition;
    private String comment;

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

    public String getOriginCenter() {
        return originCenter;
    }

    public void setOriginCenter(String originCenter) {
        this.originCenter = originCenter;
    }

    public String getCurrentCenter() {
        return currentCenter;
    }

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

    @Override
    public boolean hasLabelAndPosition() {
        return (rootContainerType != null)
            && (palletLabel != null)
            && (palletPosition != null);
    }

    @Override
    public boolean hasProductBarcodeAndPosition() {
        return (palletProductBarcode != null) && (palletPosition != null);
    }

}
