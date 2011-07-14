package edu.ualberta.med.biobank.server.reports;

import edu.ualberta.med.biobank.common.reports.BiobankReport;
import edu.ualberta.med.biobank.common.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.common.util.CapacityPostProcess;
import edu.ualberta.med.biobank.model.Container;

public class ContainerCapacityImpl extends AbstractReport {

    private CapacityPostProcess rowPostProcess;

    // @formatter:off
    private static final String QUERY = 
        "select (select c.label || '(' || c.containerType.nameShort || ')' from " //$NON-NLS-1$
        + Container.class.getName()
        + " c where c.id=substr(c2.path, 1, locate('/', c2.path)-1)), " //$NON-NLS-1$
        + "sum(c2.containerType.capacity.rowCapacity " //$NON-NLS-1$
        + "* c2.containerType.capacity.colCapacity), " //$NON-NLS-1$
        + "sum(c2.specimenPositionCollection.size) from " //$NON-NLS-1$
        + Container.class.getName()
        + " c2 where c2.containerType.specimenTypeCollection.size > 0 " //$NON-NLS-1$
        + " group by substr(c2.path, 1, locate('/', c2.path)-1)"; //$NON-NLS-1$
    // @formatter:on

    public ContainerCapacityImpl(BiobankReport report) {
        super(QUERY, report);
        rowPostProcess = new CapacityPostProcess(1, 2);
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return rowPostProcess;
    }

}