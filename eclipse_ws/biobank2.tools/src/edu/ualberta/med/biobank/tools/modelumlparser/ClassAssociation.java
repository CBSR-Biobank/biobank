package edu.ualberta.med.biobank.tools.modelumlparser;

public class ClassAssociation {

    private ModelClass toClass;
    private String assocName;
    private ClassAssociationType assocType;
    private ClassAssociation inverse;

    public ClassAssociation(ModelClass toClass, String assocName,
        ClassAssociationType assocType) {
        this.toClass = toClass;
        this.assocName = assocName;
        this.assocType = assocType;
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

    public ClassAssociationType getAssociationType() {
        return assocType;
    }

    public void setAssociationType(ClassAssociationType assocType) {
        this.assocType = assocType;
    }

    public ClassAssociation getInverse() {
        return inverse;
    }

    public void setInverse(ClassAssociation inverse) {
        this.inverse = inverse;
    }
}
