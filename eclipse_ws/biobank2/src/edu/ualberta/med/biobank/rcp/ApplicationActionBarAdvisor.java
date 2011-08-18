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
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchCommandConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.ActionFactory.IWorkbenchAction;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {

    public static final String STATUS_SERVER_MSG_ID = "biobank.serverMsg"; //$NON-NLS-1$

    public static final String SUPER_ADMIN_MSG_ID = "biobank.superAdminMsg"; //$NON-NLS-1$

    private static final String SHORTCUTS_COMMAND_ID = "org.eclipse.ui.window.showKeyAssist"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger
        .getLogger(ApplicationActionBarAdvisor.class.getName());

    public static final String VIEW_ID_PARM = "org.eclipse.ui.views.showView.viewId"; //$NON-NLS-1$

    public static final String ERROR_LOGS_VIEW = "org.eclipse.pde.runtime.LogView"; //$NON-NLS-1$

    List<Action> helpMenuCustomActions = new ArrayList<Action>();

    public static final String SEND_ERROR_EMAIL_ID = "edu.ualberta.med.biobank.commands.sendErrorMail"; //$NON-NLS-1$

    public static final String EXPORT_ERRORS_LOGS_ID = "edu.ualberta.med.biobank.commands.exportErrorsLogs"; //$NON-NLS-1$

    private IWorkbenchAction resetPerspectiveAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @Override
    protected void makeActions(IWorkbenchWindow window) {
        createCustomAction(window,
            Messages.ApplicationActionBarAdvisor_shortcuts_menu_name,
            SHORTCUTS_COMMAND_ID, "shorcuts", //$NON-NLS-1$ 
            Messages.ApplicationActionBarAdvisor_shortcuts_menu_description);

        createCustomAction(window,
            Messages.ApplicationActionBarAdvisor_errormail_menu_name,
            SEND_ERROR_EMAIL_ID, "sendErrorMail", //$NON-NLS-1$
            Messages.ApplicationActionBarAdvisor_errormail_menu_description);
        createCustomAction(window,
            Messages.ApplicationActionBarAdvisor_exportlogs_menu_label,
            EXPORT_ERRORS_LOGS_ID, "exportErrorsLogs", //$NON-NLS-1$
            Messages.ApplicationActionBarAdvisor_exportlogs_menu_description);

        createShowErrorLogsAction(window);

        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        register(resetPerspectiveAction);
    }

    private void createCustomAction(IWorkbenchWindow window, String text,
        final String commandId, String actionId, String tooltip) {
        final IHandlerService handlerService = (IHandlerService) window
            .getService(IHandlerService.class);
        Action action = new Action(text) {
            @Override
            public void run() {
                try {
                    handlerService.executeCommand(commandId, null);
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            Messages.ApplicationActionBarAdvisor_command_error_title,
                            e);
                }
            }
        };
        action.setId(actionId);
        action.setToolTipText(tooltip);
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
            logger.debug("Problem while initializing 'Show error logs' action", //$NON-NLS-1$
                nde);
        }
        final ParameterizedCommand cmd = new ParameterizedCommand(c, parms);
        Action showErrorLogsViewAction = new Action(
            Messages.ApplicationActionBarAdvisor_applicationlogs_menu_label) {
            @Override
            public void run() {
                try {
                    handlerService.executeCommand(cmd, null);
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            Messages.ApplicationActionBarAdvisor_command_error_title,
                            e);
                }
            }
        };
        showErrorLogsViewAction.setId("showErrorLogs"); //$NON-NLS-1$
        register(showErrorLogsViewAction);
        helpMenuCustomActions.add(showErrorLogsViewAction);
    }

    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager helpMenu = new MenuManager(
            Messages.ApplicationActionBarAdvisor_help_menu_name,
            IWorkbenchActionConstants.M_HELP);
        menuBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
        menuBar.add(helpMenu);

        // this group will also show the "Key Assist" menu. We need to add an
        // activity if we don't want this menu to be displayed, but it is not
        // displayed after the product is exported
        helpMenu.add(new Separator("group.assist")); //$NON-NLS-1$
        for (Action action : helpMenuCustomActions) {
            helpMenu.add(action);
        }
        helpMenu.add(resetPerspectiveAction);
        helpMenu.add(new Separator());
        helpMenu.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
    }

    @Override
    protected void fillStatusLine(IStatusLineManager statusLine) {
        final MsgStatusItem superAdminMsgStatusItem = new MsgStatusItem(
            SUPER_ADMIN_MSG_ID);
        superAdminMsgStatusItem.setIcon(BiobankPlugin.getDefault().getImage(
            BgcPlugin.IMG_ADMIN));
        superAdminMsgStatusItem.setText("Super Administrator Mode"); //$NON-NLS-1$
        superAdminMsgStatusItem.setVisible(false);

        statusLine.add(superAdminMsgStatusItem);

        final MsgStatusItem serverMsgStatusItem = new MsgStatusItem(
            STATUS_SERVER_MSG_ID) {
            @Override
            public Color getBackgroundColor(String text) {
                if (text != null && !text.endsWith("@cbsr.med.ualberta.ca")) { //$NON-NLS-1$
                    return PlatformUI.getWorkbench().getDisplay()
                        .getSystemColor(SWT.COLOR_YELLOW);
                }
                return null;
            }
        };
        serverMsgStatusItem.setText(""); //$NON-NLS-1$
        statusLine.add(serverMsgStatusItem);
    }
}
