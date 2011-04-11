package edu.ualberta.med.biobank.common.scanprocess;

import java.util.List;
import java.util.Map;

import edu.ualberta.med.biobank.common.util.RowColPos;

public class ReceiveDispatchResult extends ScanProcessResult {

    private static final long serialVersionUID = 1L;
    private List<Integer> extraSpecimens;

    public ReceiveDispatchResult() {
        super();
    }

    public void setResult(Map<RowColPos, Cell> cells, CellStatus status,
        List<Integer> extraSpecimens) {
        setResult(cells, status);
        this.extraSpecimens = extraSpecimens;
    }

    public List<Integer> getExtraSpecimens() {
        return extraSpecimens;
    }
}
