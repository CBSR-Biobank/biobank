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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;

public class ApplicationActionBarAdvisor extends ActionBarAdvisor {
    private static final I18n i18n = I18nFactory
        .getI18n(ApplicationActionBarAdvisor.class);

    @SuppressWarnings("nls")
    public static final String STATUS_SERVER_MSG_ID = "biobank.serverMsg";

    @SuppressWarnings("nls")
    public static final String SUPER_ADMIN_MSG_ID = "biobank.superAdminMsg";

    @SuppressWarnings("nls")
    private static final String SHORTCUTS_COMMAND_ID =
        "org.eclipse.ui.window.showKeyAssist";

    private static BgcLogger logger = BgcLogger
        .getLogger(ApplicationActionBarAdvisor.class.getName());

    @SuppressWarnings("nls")
    public static final String VIEW_ID_PARM =
        "org.eclipse.ui.views.showView.viewId";

    @SuppressWarnings("nls")
    public static final String ERROR_LOGS_VIEW =
        "org.eclipse.pde.runtime.LogView";

    List<Action> helpMenuCustomActions = new ArrayList<Action>();

    @SuppressWarnings("nls")
    public static final String SEND_ERROR_EMAIL_ID =
        "edu.ualberta.med.biobank.commands.sendErrorMail";

    @SuppressWarnings("nls")
    public static final String EXPORT_ERRORS_LOGS_ID =
        "edu.ualberta.med.biobank.commands.exportErrorsLogs";

    private IWorkbenchAction resetPerspectiveAction;

    public ApplicationActionBarAdvisor(IActionBarConfigurer configurer) {
        super(configurer);
    }

    @SuppressWarnings("nls")
    @Override
    protected void makeActions(IWorkbenchWindow window) {
        createCustomAction(window,
            i18n.tr("Keyboard Shortcuts..."),
            SHORTCUTS_COMMAND_ID,
            "shorcuts",
            // tooltip.
            i18n.tr("Show shorcuts for the current view"));

        createCustomAction(window,
            i18n.tr("Send Error Mail"),
            SEND_ERROR_EMAIL_ID, "sendErrorMail",
            // tooltip.
            i18n.tr("Report a problem to developpers"));
        createCustomAction(window,
            i18n.tr("Export Errors Logs"),
            EXPORT_ERRORS_LOGS_ID,
            "exportErrorsLogs",
            // tooltip.
            i18n.tr("Export a zip with useful logs data for developers"));

        createShowErrorLogsAction(window);

        resetPerspectiveAction = ActionFactory.RESET_PERSPECTIVE.create(window);
        register(resetPerspectiveAction);
    }

    private void createCustomAction(IWorkbenchWindow window, String text,
        final String commandId, String actionId, String tooltip) {
        final IHandlerService handlerService = (IHandlerService) window
            .getService(IHandlerService.class);
        Action action = new Action(text) {
            @SuppressWarnings("nls")
            @Override
            public void run() {
                try {
                    handlerService.executeCommand(commandId, null);
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            // dialog message.
                            i18n.tr("Problem with command"),
                            e);
                }
            }
        };
        action.setId(actionId);
        action.setToolTipText(tooltip);
        register(action);
        helpMenuCustomActions.add(action);
    }

    @SuppressWarnings("nls")
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
            i18n.tr("Show Application Error Logs")) {
            @Override
            public void run() {
                try {
                    handlerService.executeCommand(cmd, null);
                } catch (Exception e) {
                    BgcPlugin
                        .openAsyncError(
                            // dialog title.
                            i18n.tr("Problem with command"),
                            e);
                }
            }
        };
        showErrorLogsViewAction.setId("showErrorLogs");
        register(showErrorLogsViewAction);
        helpMenuCustomActions.add(showErrorLogsViewAction);
    }

    @SuppressWarnings("nls")
    @Override
    protected void fillMenuBar(IMenuManager menuBar) {
        MenuManager helpMenu = new MenuManager(
            // menu text.
            i18n.tr("&Help"),
            "biobank.help");

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
    }

    @SuppressWarnings("nls")
    @Override
    protected void fillStatusLine(IStatusLineManager statusLine) {
        final MsgStatusItem superAdminMsgStatusItem = new MsgStatusItem(
            SUPER_ADMIN_MSG_ID);
        superAdminMsgStatusItem.setIcon(BiobankPlugin.getDefault().getImage(
            BgcPlugin.IMG_ADMIN));
        superAdminMsgStatusItem.setText(
            // menu item text.
            i18n.tr("Super Administrator Mode"));
        superAdminMsgStatusItem.setVisible(false);

        statusLine.add(superAdminMsgStatusItem);

        final MsgStatusItem serverMsgStatusItem = new MsgStatusItem(
            STATUS_SERVER_MSG_ID) {
            @Override
            public Color getBackgroundColor(String text) {
                if (text != null && !text.endsWith("@cbsr.med.ualberta.ca")) {
                    return PlatformUI.getWorkbench().getDisplay()
                        .getSystemColor(SWT.COLOR_YELLOW);
                }
                return null;
            }
        };
        serverMsgStatusItem.setText(StringUtil.EMPTY_STRING);
        statusLine.add(serverMsgStatusItem);
    }
}
