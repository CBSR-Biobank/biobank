package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;

import edu.ualberta.med.biobank.BioBankPlugin;

public class DecodePlateHandler extends AbstractHandler implements IHandler {

    protected int plateId;

    public DecodePlateHandler(int plateId) {
        this.plateId = plateId;
    }

    public Object execute(ExecutionEvent event) throws ExecutionException {
        BioBankPlugin.openError("Decode Plate", "Not implemented yet");
        return null;
    }

}
