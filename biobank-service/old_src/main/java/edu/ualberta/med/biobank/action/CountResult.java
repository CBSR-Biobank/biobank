package edu.ualberta.med.biobank.action;

public class CountResult implements ActionResult {
    private static final long serialVersionUID = 1L;
    private final Long count;

    public CountResult(Long count) {
        this.count = count;
    }

    public Long getCount() {
        return count;
    }

    public boolean notZero() {
        return count != null && count != 0;
    }
}
