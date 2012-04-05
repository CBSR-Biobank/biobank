package edu.ualberta.med.biobank.model.type;


/**
 * Never remove one of these enum. Use deprecated if it should not be used
 * anymore.
 */
public enum DispatchSpecimenState implements ItemState {
    NONE(0, "Ok"), RECEIVED(1, 
        "Received"), MISSING(2, 
        "Missing"), EXTRA(3, 
        "Extra"); 

    private Integer id;
    private String label;

    private DispatchSpecimenState(Integer id, String label) {
        this.id = id;
        this.label = label;
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
    public boolean isEquals(Integer state) {
        return id.equals(state);
    }

    @Override
    public String toString() {
        return getLabel();
    }

    public static DispatchSpecimenState getState(Integer state) {
        for (DispatchSpecimenState das : values()) {
            if (das.isEquals(state)) {
                return das;
            }
        }
        return null;
    }
}
