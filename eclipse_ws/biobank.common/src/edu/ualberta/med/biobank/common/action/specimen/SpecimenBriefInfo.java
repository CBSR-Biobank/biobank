package edu.ualberta.med.biobank.common.action.specimen;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import edu.ualberta.med.biobank.common.action.ActionResult;
import edu.ualberta.med.biobank.model.BatchOperation;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Specimen;

public class SpecimenBriefInfo implements ActionResult {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private Stack<Container> parents = new Stack<Container>();
    private Set<BatchOperation> batchOperations = new HashSet<BatchOperation>();

    public SpecimenBriefInfo() {
    }

    public SpecimenBriefInfo(Specimen specimen,
                             Stack<Container> parents,
                             Set<BatchOperation> batchOperations) {
        this.specimen = specimen;
        this.parents = parents;
        this.batchOperations = batchOperations;
    }

    public Specimen getSpecimen() {
        return specimen;
    }

    public Stack<Container> getParents() {
        return parents;
    }

    public Set<BatchOperation> getBatchOperations() {
        return batchOperations;
    }
}