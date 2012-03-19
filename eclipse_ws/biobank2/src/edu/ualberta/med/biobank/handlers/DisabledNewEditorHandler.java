package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

/* This class is required to disable the "New Editor" command on editor tabs
 *  DO NOT REMOVE
 */

public class DisabledNewEditorHandler extends AbstractHandler {

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        return null;
    }
}