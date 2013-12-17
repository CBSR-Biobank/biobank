package edu.ualberta.med.biobank.forms.linkassign;

import java.util.Set;

import edu.ualberta.med.biobank.widgets.grids.well.PalletWell;

public interface IDecodePalletManagement {

    void beforeProcessingThreadStart();

    void processDecodeResult() throws Exception;

    void decodeAndProcessError(String errorMsg);

    void postProcessDecodeTubesManually(Set<PalletWell> cells) throws Exception;

    boolean canDecodeTubesManually(PalletWell cell);
}
