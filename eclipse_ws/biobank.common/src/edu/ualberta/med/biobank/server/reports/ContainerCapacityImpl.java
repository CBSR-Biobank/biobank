package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.CapacityPostProcess;
import edu.ualberta.med.biobank.model.ContainerPath;

public class ContainerCapacityImpl extends AbstractReport {

    private CapacityPostProcess rowPostProcess;

    private static final String QUERY = "select (select c.container.label || '(' || c.container.containerType.nameShort || ')' from "
        + ContainerPath.class.getName()
        + " c where c.path=substr(path.path, 1, locate('/', path.path)-1)), "
        + "sum(path.container.containerType.capacity.rowCapacity * path.container.containerType.capacity.colCapacity), sum(path.container.specimenPositionCollection.size) from "
        + ContainerPath.class.getName()
        + " path where path.container.containerType.specimenTypeCollection.size > 0 "
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