package edu.ualberta.med.biobank.common.action.batchoperation.specimen;

import edu.ualberta.med.biobank.common.action.batchoperation.IBatchOpInputPojo;

public interface IBatchOpSpecimenPositionPojo extends IBatchOpInputPojo {

    String getInventoryId();

    String getPalletProductBarcode();

    void setPalletProductBarcode(String barcode);

    String getRootContainerType();

    void setRootContainerType(String position);

    String getPalletLabel();

    void setPalletLabel(String position);

    String getPalletPosition();

    void setPalletPosition(String position);

    String getComment();

    public void setComment(String comment);

    boolean hasLabelAndPosition();

    boolean hasProductBarcodeAndPosition();

}
