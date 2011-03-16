package edu.ualberta.med.biobank.views;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.treeview.AbstractSearchedNode;
import edu.ualberta.med.biobank.treeview.AbstractTodayNode;
import edu.ualberta.med.biobank.treeview.patient.ProcessingEventGroup;

public class ProcessingView extends AbstractTodaySearchAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ProcessingView";

    private static ProcessingView currentInstance;

    private ProcessingEventGroup processingNode;

    public ProcessingView() {
        super();
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        processingNode = new ProcessingEventGroup(rootNode, 2,
            "Processing Events");
        processingNode.setParent(rootNode);
        rootNode.addChild(processingNode);
    }

    @Override
    protected List<? extends ModelWrapper<?>> search(String text)
        throws Exception {
        // FIXME: i do nothing
        return null;
    }

    @Override
    protected void notFound(String text) {

    }

    @Override
    protected AbstractTodayNode<?> createTodayNode() {
        return null;
        // FIXME: i do nothing
    }

    @Override
    protected AbstractSearchedNode createSearchedNode() {
        return null;
        // FIXME: i do nothing
    }

    public static ProcessingView getCurrent() {
        return currentInstance;
    }

    public static void reloadCurrent() {
        if (currentInstance != null)
            currentInstance.reload();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return "";
        // FIXME: i return nothign
    }

    @Override
    protected String getString() {
        return toString();
    }

}
