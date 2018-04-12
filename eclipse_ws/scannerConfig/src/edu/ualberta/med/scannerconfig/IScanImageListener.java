package edu.ualberta.med.scannerconfig;


public interface IScanImageListener {

    /**
     * Called when a new image is present.
     */
    void imageAvailable(BarcodeImage image);

    /**
     * Called when the image is no longer available.
     */
    void imageDeleted();
}
