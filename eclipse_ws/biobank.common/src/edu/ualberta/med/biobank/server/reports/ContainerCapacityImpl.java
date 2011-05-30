package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.CapacityPostProcess;
import edu.ualberta.med.biobank.model.Container;

public class ContainerCapacityImpl extends AbstractReport {

    private CapacityPostProcess rowPostProcess;

    private static final String QUERY = "select (select c.container.label || '(' || c.container.containerType.nameShort || ')' from "
        + Container.class.getName()
        + " c where c.path=substr(path.path, 1, locate('/', path.path)-1)), "
        + "sum(c.containerType.capacity.rowCapacity "
        + "* c.containerType.capacity.colCapacity), "
        + "sum(c.specimenPositionCollection.size) from "
        + Container.class.getName()
        + " path where c.containerType.specimenTypeCollection.size > 0 "
        + " group by substr(path.path, 1, locate('/', path.path)-1)";

    public ContainerCapacityImpl(BiobankReport report) {
        super(QUERY, report);
        rowPostProcess = new CapacityPostProcess(1, 2);
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return rowPostProcess;
    }

}