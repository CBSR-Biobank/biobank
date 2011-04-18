package edu.ualberta.med.biobank.widgets.grids.cell;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerCell extends AbstractUICell {

    private Integer row;

    private Integer col;

    private ContainerWrapper container;

    private UICellStatus status;

    public ContainerCell() {
    }

    public ContainerCell(Integer row, Integer col, ContainerWrapper container) {
        this.row = row;
        this.col = col;
        this.container = container;
    }

    public ContainerCell(Integer row, Integer col) {
        this(row, col, null);
    }

    public UICellStatus getStatus() {
        return status;
    }

    public void setStatus(UICellStatus status) {
        this.status = status;
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
