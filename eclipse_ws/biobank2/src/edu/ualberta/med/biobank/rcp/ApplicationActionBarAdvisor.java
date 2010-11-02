package edu.ualberta.med.biobank.rcp;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IParameter;
import org.eclipse.core.commands.Parameterization;
import org.eclipse.core.commands.ParameterizedCommand;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.logs.BiobankLogger;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ApplicationActionBarAdvisor.class.getName());

    public static final String VIEW_ID_PARM = "org.eclipse.ui.views.showView.viewId";
    public static final String ERROR_LOGS_VIEW = "org.eclipse.pde.runtime.LogView";

    List<Action> helpMenuCustomActions = new ArrayList<Action>();
    public static final String SEND_ERROR_EMAIL_ID = "edu.ualberta.med.biobank.commands.sendErrorMail";
    public static final String EXPORT_ERRORS_LOGS_ID = "edu.ualberta.med.biobank.commands.exportErrorsLogs";

    private IWorkbenchAction aboutAction;

    private IWorkbenchAction resetPerspectiveAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(IWorkbenchWindow window) {
        createCustomAction(window, "Send Error Mail", SEND_ERROR_EMAIL_ID,
            "sendErrorMail");
        createCustomAction(window, "Export Scanner Error Logs",
            EXPORT_ERRORS_LOGS_ID, "exportErrorsLogs");

        createShowErrorLogsAction(window);

        // about action
        aboutAction = ActionFactory.ABOUT.create(window);
        register(aboutAction);

        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        register(resetPerspectiveAction);
    }

    private void createCustomAction(IWorkbenchWindow window, String text,
        final String commandId, String actionId) {
        final IHandlerService handlerService = (IHandlerService) window
            .getService(IHandlerService.class);
        Action action = new Action(text) {
            @Override
            public void run() {
                try {
                    handlerService.executeCommand(commandId, null);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError("Problem with command", e);
                }
            }
        };
        action.setId(actionId);
        register(action);
        helpMenuCustomActions.add(action);
    }

    private void createShowErrorLogsAction(IWorkbenchWindow window) {
        // Show error logs view action
        final IHandlerService handlerService = (IHandlerService) window
            .getService(IHandlerService.class);
        final ICommandService commandService = (ICommandService) window
            .getService(ICommandService.class);
        Command c = commandService
            .getCommand(IWorkbenchCommandConstants.VIEWS_SHOW_VIEW);
        Parameterization[] parms = null;
        try {
            IParameter parmDef = c.getParameter(VIEW_ID_PARM);
            if (parmDef != null) {
                parms = new Parameterization[] { new Parameterization(parmDef,
                    ERROR_LOGS_VIEW) };
            }
        } catch (NotDefinedException nde) {
            logger.debug("Problem while initializing 'Show error logs' action",
                nde);
        }
        final ParameterizedCommand cmd = new ParameterizedCommand(c, parms);
        Action showErrorLogsViewAction = new Action(
            "Show Application Error Logs") {
            @Override
            public void run() {
                try {
                    handlerService.executeCommand(cmd, null);
                } catch (Exception e) {
                    BioBankPlugin.openAsyncError("Problem with command", e);
                }
            }
        };
        showErrorLogsViewAction.setId("showErrorLogs");
        register(showErrorLogsViewAction);
        helpMenuCustomActions.add(showErrorLogsViewAction);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager helpMenu = new MenuManager("&Help",
            IWorkbenchActionConstants.M_HELP);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);

        // this group will also show the "Key Assist" menu. We need to add an
        // activity if we don't want this menu to be displayed, but it is not
        // displayed after the product is exported
        helpMenu.add(new Separator("group.assist"));
        for (Action action : helpMenuCustomActions) {
            helpMenu.add(action);
        }
        helpMenu.add(resetPerspectiveAction);
        helpMenu.add(new Separator());
        helpMenu.add(aboutAction);
    }
}
