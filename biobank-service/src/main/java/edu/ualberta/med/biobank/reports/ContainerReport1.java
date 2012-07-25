package edu.ualberta.med.biobank.reports;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.reports.util.AbstractRowPostProcess;
import edu.ualberta.med.biobank.reports.util.CapacityPostProcess;

public class ContainerReport1 extends AbstractReport {

    private final CapacityPostProcess rowPostProcess;

    // @formatter:off
    @SuppressWarnings("nls")
    private static final String QUERY = 
        "select (select c.label || '(' || c.containerType.nameShort || ')' from " 
        + Container.class.getName()
        + " c where c.id=substr(c2.path, 1, locate('/', c2.path)-1)), " 
        + "sum(c2.containerType.capacity.rowCapacity " 
        + "* c2.containerType.capacity.colCapacity), " 
        + "sum(c2.specimenPositions.size) from " 
        + Container.class.getName()
        + " c2 where c2.containerType.specimenTypes.size > 0 " 
        + " group by substr(c2.path, 1, locate('/', c2.path)-1)"; 
    // @formatter:on 

    public ContainerReport1(BiobankReport report) {
        super(QUERY, report);
        rowPostProcess = new CapacityPostProcess(1, 2);
    }

    @Override
    public AbstractRowPostProcess getRowPostProcess() {
        return rowPostProcess;
    }

}