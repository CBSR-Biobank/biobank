package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

public class ContainerCell extends Cell {

    private Integer row;

    private Integer col;

    private ContainerWrapper container;

    private CellStatus status;

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

    public CellStatus getStatus() {
        return status;
    }

    public void setStatus(CellStatus status) {
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
