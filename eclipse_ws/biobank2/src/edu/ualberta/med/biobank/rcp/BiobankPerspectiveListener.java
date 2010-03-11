package edu.ualberta.med.biobank.rcp;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

import edu.ualberta.med.biobank.handlers.SearchHandler;
import edu.ualberta.med.biobank.views.SearchView;

public class BiobankPerspectiveListener extends PerspectiveAdapter {

    @Override
    public void perspectiveDeactivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        // page.closeAllEditors(true);
    }

    @Override
    public void perspectiveActivated(IWorkbenchPage page,
        IPerspectiveDescriptor perspective) {
        IViewPart searchView = page.findView(SearchView.ID);
        ICommandService commandService = (ICommandService) PlatformUI
            .getWorkbench().getService(ICommandService.class);
        Command command = commandService
            .getCommand(SearchHandler.SEARCH_COMMAND_ID);
        State state = command.getState(RegistryToggleState.STATE_ID);
        // set the search toggle button to the correct state when switching to
        // another perspective
        state.setValue(searchView != null && page.isPartVisible(searchView));
    }
}
