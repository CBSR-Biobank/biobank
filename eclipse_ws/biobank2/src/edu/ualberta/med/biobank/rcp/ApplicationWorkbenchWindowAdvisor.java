package edu.ualberta.med.biobank.rcp;

import java.util.Map;

import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.application.ActionBarAdvisor;
import org.eclipse.ui.application.IActionBarConfigurer;
import org.eclipse.ui.application.IWorkbenchWindowConfigurer;
import org.eclipse.ui.application.WorkbenchWindowAdvisor;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.sourceproviders.SessionState;

public class ApplicationWorkbenchWindowAdvisor extends WorkbenchWindowAdvisor {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(ApplicationWorkbenchWindowAdvisor.class.getName());

    private static final String MAIN_TITLE = "BioBank2";

    public ApplicationWorkbenchWindowAdvisor(
        IWorkbenchWindowConfigurer configurer) {
        super(configurer);
    }

    @Override
    public ActionBarAdvisor createActionBarAdvisor(
        IActionBarConfigurer configurer) {
        return new ApplicationActionBarAdvisor(configurer);
    }

    @Override
    public void preWindowOpen() {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        configurer.setInitialSize(new Point(800, 700));
        configurer.setShowCoolBar(true);
        configurer.setShowStatusLine(true);

        /*
         * configurer.setShowPerspectiveBar(true);
         * PlatformUI.getPreferenceStore().setDefault("DOCK_PERSPECTIVE_BAR",
         * "left"); PlatformUI.getPreferenceStore().setDefault(
         * "SHOW_TEXT_ON_PERSPECTIVE_BAR", false);
         */

        configurer.setTitle(MAIN_TITLE);
        configurer.setShowProgressIndicator(true);
    }

    @Override
    public void postWindowOpen() {
        IStatusLineManager statusline = getWindowConfigurer()
            .getActionBarConfigurer().getStatusLineManager();
        statusline.setMessage(null, "Application ready");

        IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        IWorkbenchPage page = window.getActivePage();
        if (page.getPerspective().getId()
            .equals(AliquotManagementPerspective.ID)) {
            // can't start on this perspective: switch to patient perspective
            try {
                workbench.showPerspective(PatientsAdministrationPerspective.ID,
                    workbench.getActiveWorkbenchWindow());
            } catch (WorkbenchException e) {
                logger.error("Error while opening patients perpective", e);
            }
        }
        page.addPartListener(new BiobankPartListener());
        window.addPerspectiveListener(new BiobankPerspectiveListener());

        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        SessionState sessionSourceProvider = (SessionState) service
            .getSourceProvider(SessionState.LOGIN_STATE_SOURCE_NAME);
        sessionSourceProvider
            .addSourceProviderListener(new ISourceProviderListener() {
                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    if (sourceValue != null
                        && sourceValue.equals(SessionState.LOGGED_IN)) {
                        updatedTitle(SessionManager.getServer(), SessionManager
                            .getUser().getLogin());
                    } else {
                        resetTitle();
                    }
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                }
            });
    }

    private void resetTitle() {
        updatedTitle(null, null);
    }

    private void updatedTitle(String server, String username) {
        IWorkbenchWindowConfigurer configurer = getWindowConfigurer();
        String oldTitle = configurer.getTitle();
        String newTitle = MAIN_TITLE;
        if (server != null && username != null) {
            newTitle = MAIN_TITLE + " - " + server + " [" + username + "]";
        }
        if (!newTitle.equals(oldTitle)) {
            configurer.setTitle(newTitle);
        }
    }
}
