package edu.ualberta.med.biobank.tools.modelumlparser;

public class Generalization {
    private ModelClass parentClass;
    private ModelClass childClass;

    Generalization(ModelClass parentClass, ModelClass childClass) {
        this.parentClass = parentClass;
        this.childClass = childClass;
    }

    public ModelClass getParentClass() {
        return parentClass;
    }

    public void setParentClass(ModelClass parentClass) {
        this.parentClass = parentClass;
    }

    public ModelClass getChildClass() {
        return childClass;
    }

    public void setChildClass(ModelClass childClass) {
        this.childClass = childClass;
    }

}
