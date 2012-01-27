package edu.ualberta.med.biobank.model;

public class RequestSpecimen extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer state;
    private String claimedBy;
    private Specimen specimen;
    private Request request;

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getClaimedBy() {
        return claimedBy;
    }

    public void setClaimedBy(String claimedBy) {
        this.claimedBy = claimedBy;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    public Request getRequest() {
        return request;
    }

    public void setRequest(Request request) {
        this.request = request;
    }
}
