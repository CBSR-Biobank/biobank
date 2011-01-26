package edu.ualberta.med.biobank.tools.modelumlparser;

public class ClassAssociation {
    private ModelClass toClass;
    private String assocName;
    private String multiplicity;

    public ClassAssociation(ModelClass toClass, String assocName,
        String multiplicity) {
        this.toClass = toClass;
        this.assocName = assocName;
        this.multiplicity = multiplicity;
    }

    public ModelClass getToClass() {
        return toClass;
    }

    public void setToClass(ModelClass toClass) {
        this.toClass = toClass;
    }

    public String getAssocName() {
        return assocName;
    }

    public void setAssocName(String assocName) {
        this.assocName = assocName;
    }

    public String getMultiplicity() {
        return multiplicity;
    }

    public void setMultiplicity(String multiplicity) {
        this.multiplicity = multiplicity;
    }

}
