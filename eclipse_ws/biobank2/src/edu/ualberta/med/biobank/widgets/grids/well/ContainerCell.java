package edu.ualberta.med.biobank.widgets.grids.well;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerCell extends AbstractUIWell {

    private ContainerWrapper container = null;

    public ContainerCell(Integer row, Integer col) {
        super(row, col);
    }

    public ContainerCell(Integer row, Integer col, ContainerWrapper container) {
        super(row, col);
        this.container = container;
    }

    public ContainerWrapper getContainer() {
        return container;
    }

    public void setContainer(ContainerWrapper container) {
        this.container = container;
    }

}
