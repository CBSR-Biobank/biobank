package edu.ualberta.med.biobank.forms.linkassign;

import java.util.Set;

import edu.ualberta.med.biobank.widgets.grids.well.SpecimenCell;

public interface IDecodePalletManagement {

    void beforeProcessingThreadStart();

    void processDecodeResult() throws Exception;

    void decodeAndProcessError(String errorMsg);

    void postProcessDecodeTubesManually(Set<SpecimenCell> cells) throws Exception;

    boolean canDecodeTubesManually(SpecimenCell cell);
}
