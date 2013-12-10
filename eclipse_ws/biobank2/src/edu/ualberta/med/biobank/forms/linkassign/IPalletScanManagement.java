package edu.ualberta.med.biobank.forms.linkassign;

import java.util.Map;
import java.util.Set;

import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;
import edu.ualberta.med.scannerconfig.PlateDimensions;

public interface IPalletScanManagement {

    void beforeScanThreadStart();

    void beforeScan();

    void processScanResult() throws Exception;

    void afterScanBeforeMerge();

    void afterSuccessfulScan(Map<RowColPos, PalletWell> wells);

    void afterScanAndProcess();

    void scanAndProcessError(String errorMsg);

    void postprocessScanTubesManually(Set<PalletWell> cells) throws Exception;

    boolean canScanTubesManually(PalletWell cell);

    Set<PlateDimensions> getValidPlateDimensions();
}
