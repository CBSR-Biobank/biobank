package edu.ualberta.med.biobank.action;

public class IdResult
    implements ActionResult {
    private static final long serialVersionUID = 1L;

    private final Integer id;

    public IdResult(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }
}
