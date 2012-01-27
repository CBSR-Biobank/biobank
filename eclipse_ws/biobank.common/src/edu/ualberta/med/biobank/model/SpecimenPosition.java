package edu.ualberta.med.biobank.model;

public class SpecimenPosition extends AbstractPosition {
    private static final long serialVersionUID = 1L;

    private Container container;
    private Specimen specimen;

    public Container getContainer() {
        return container;
    }

    public void setContainer(Container container) {
        this.container = container;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }
}