package edu.ualberta.med.biobank.model.type;


/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum RequestSpecimenState implements ItemState {
    AVAILABLE_STATE(0, "Available"), 
    PULLED_STATE(1, "Pulled"), 
    UNAVAILABLE_STATE(2, "Unavailable"), 
    DISPATCHED_STATE(3, "Dispatched"); 

    private Integer id;
    private String label;

    private RequestSpecimenState(Integer id, String label) {
        this.id = id;
        this.label = label;
    }

    public static RequestSpecimenState getState(Integer state) {
        for (RequestSpecimenState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String toString() {
        return getLabel();
    }

    @Override
    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

}
