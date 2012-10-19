package edu.ualberta.med.biobank.widgets.grids.cell;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerCell extends AbstractUIWell {

    private Integer row;

    private Integer col;

    private ContainerWrapper container;

    public ContainerCell() {
        //
    }

    public ContainerCell(Integer row, Integer col, ContainerWrapper container) {
        this.row = row;
        this.col = col;
        this.container = container;
    }

    public ContainerCell(Integer row, Integer col) {
        this(row, col, null);
    }

    @Override
    public Integer getRow() {
        return row;
    }

    @Override
    public Integer getCol() {
        return col;
    }

    public ContainerWrapper getContainer() {
        return container;
    }

    public void setContainer(ContainerWrapper container) {
        this.container = container;
    }

}
