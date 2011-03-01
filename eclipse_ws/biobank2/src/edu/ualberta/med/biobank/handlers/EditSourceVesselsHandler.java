package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;
import edu.ualberta.med.biobank.forms.SourceVesselEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.admin.SessionAdapter;

public class EditSourceVesselsHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.editSourceVessels";

    @Override
    public Object execute(ExecutionEvent event) throws ExecutionException {
        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSession();
        Assert.isNotNull(sessionAdapter);
        try {
            PlatformUI
                .getWorkbench()
                .getActiveWorkbenchWindow()
                .getActivePage()
                .openEditor(new FormInput(sessionAdapter),
                    SourceVesselEntryForm.ID, false, 0);
        } catch (Exception e) {
            throw new ExecutionException("Could not execute handler.", e);
        }
        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.canCreate(SpecimenTypeWrapper.class, null)
            || SessionManager.canUpdate(SpecimenTypeWrapper.class, null) || SessionManager
            .canDelete(SpecimenTypeWrapper.class, null))
            && (SessionManager.getInstance().getSession() != null);
    }
}
