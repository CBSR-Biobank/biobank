package edu.ualberta.med.biobank.common.action;

public class BooleanResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final boolean bool;

    public BooleanResult(boolean bool) {
        this.bool = bool;
    }

    public boolean isTrue() {
        return bool;
    }
}
