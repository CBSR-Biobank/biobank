package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;

import edu.ualberta.med.biobank.treeview.dispatch.OutgoingNode;
import edu.ualberta.med.biobank.views.DispatchAdministrationView;

public class DispatchAddHandler extends AbstractHandler {

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        OutgoingNode node = DispatchAdministrationView.getCurrent()
            .getOutgoingNode();
        Assert.isNotNull(node);
        node.addDispatch();
        return null;
    }

}