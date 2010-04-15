package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

public class DecodePlateHandler extends AbstractHandler implements IHandler {

    protected int plateId;

    public DecodePlateHandler(int plateId) {
        this.plateId = plateId;
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        return null;
    }

}
