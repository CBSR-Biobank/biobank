package edu.ualberta.med.biobank.common.action.batchoperation.specimen.position;

import edu.ualberta.med.biobank.common.action.batchoperation.specimen.IBatchOpSpecimenPositionPojo;

/**
 * POJO used by the Specimen Position Assign Batch Operation feature to import specimen information.
 *
 * @author Nelson Loyola
 *
 */
public class PositionBatchOpPojo implements IBatchOpSpecimenPositionPojo {
    private static final long serialVersionUID = 1L;

    private int lineNumber;
    private String inventoryId;
    private String currentPalletLabel;
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

    public String getCurrentPalletLabel() {
        return currentPalletLabel;
    }

    public void setCurrentPalletLabel(String currentPalletLabel) {
        this.currentPalletLabel = currentPalletLabel;
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

    public boolean hasCurrentPosition() {
        return currentPalletLabel != null;
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
