package edu.ualberta.med.biobank.common.action.batchoperation.specimenPosition;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

/**
 * POJO used by the Specimen Position Assign Batch Operation feature to import specimen information.
 *
 * @author Nelson Loyola
 *
 */
public class PositionBatchOpPojo implements IBatchOpInputPojo {
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

    public String getPalletProductBarcode() {
        return palletProductBarcode;
    }

    public void setPalletProductBarcode(String palletProductBarcode) {
        this.palletProductBarcode = palletProductBarcode;
    }

    public String getRootContainerType() {
        return rootContainerType;
    }

    public void setRootContainerType(String rootContainerType) {
        this.rootContainerType = rootContainerType;
    }

    public String getPalletLabel() {
        return palletLabel;
    }

    public void setPalletLabel(String palletLabel) {
        this.palletLabel = palletLabel;
    }

    public String getPalletPosition() {
        return palletPosition;
    }

    public void setPalletPosition(String palletPosition) {
        this.palletPosition = palletPosition;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public boolean hasCurrentPosition() {
        return currentPalletLabel != null;
    }

    public boolean hasLabelAndPosition() {
        return (rootContainerType != null)
            && (palletLabel != null)
            && (palletPosition != null);
    }

    public boolean hasProductBarcodeAndPosition() {
        return (palletProductBarcode != null) && (palletPosition != null);
    }

}
