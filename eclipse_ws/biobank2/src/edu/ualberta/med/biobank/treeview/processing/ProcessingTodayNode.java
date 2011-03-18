package edu.ualberta.med.biobank.treeview.processing;

import java.util.Collections;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingTodayNode extends
    AbstractTodayNode<ProcessingEventWrapper> {

    public ProcessingTodayNode(AdapterBase parent, int id) {
        super(parent, id);
        setName("Today's processing");
    }

    @Override
    protected boolean isParentTo(ModelWrapper<?> parent, ModelWrapper<?> child) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    protected List<ProcessingEventWrapper> getTodayElements()
        throws ApplicationException {
        // TODO Auto-generated method stub
        return Collections.emptyList();
    }

    @Override
    protected void addChild(ProcessingEventWrapper child) {
        // TODO Auto-generated method stub

    }

    @Override
    protected AdapterBase createChildNode() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        // TODO Auto-generated method stub
        return null;
    }

}
