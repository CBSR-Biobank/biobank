package edu.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.rcp.Application;
import edu.ualberta.med.biobank.rcp.SiteCombo;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SiteManager {

    private static Logger LOGGER = Logger
        .getLogger(SiteManager.class.getName());

    private static final String SITE_PREF_NODE = "Site";

    private static final String LAST_SERVER_PREF = "lastServer";

    private static final String LAST_SITE_PREF = "lastSite";

    private WritableApplicationService appService;

    private String sessionName;

    private SiteWrapper currentSiteWrapper;

    private SiteWrapper allSiteWrapper;

    private SiteCombo siteCombo;

    private List<SiteWrapper> currentSiteWrappers;

    public SiteManager(WritableApplicationService appService, String sessionName) {
        this.appService = appService;
        this.sessionName = sessionName;
        currentSiteWrappers = new ArrayList<SiteWrapper>();
        allSiteWrapper = new SiteWrapper(appService, new Site());
        allSiteWrapper.setName("All Sites");
    }

    /*
     * selects the site the user was working with the last time he / she logged
     * out if logged into same server and same site exists
     */
    public void getCurrentSite(String serverName, Collection<SiteWrapper> sites) {
        if (currentSiteWrapper != null)
            return;

        Preferences prefs = new InstanceScope().getNode(Application.PLUGIN_ID);
        Preferences prefNode = prefs.node(SITE_PREF_NODE);
        String lastServer = prefNode.get(LAST_SERVER_PREF, "");

        if (!lastServer.equals(serverName))
            return;

        Integer siteId = prefNode.getInt(LAST_SITE_PREF, -1);

        if (siteId.equals(-1))
            return;

        for (SiteWrapper site : sites) {
            if (site.getId().equals(siteId))
                currentSiteWrapper = site;
        }
    }

    public void setCurrentSite(SiteWrapper siteWrapper) {
        try {
            currentSiteWrapper = siteWrapper;
            Integer saveVal = -1;
            if ((siteWrapper != null) && (siteWrapper.getId() != null))
                saveVal = siteWrapper.getId();
            Preferences prefs = new InstanceScope()
                .getNode(Application.PLUGIN_ID);
            Preferences prefNode = prefs.node(SITE_PREF_NODE);
            prefNode.put(LAST_SERVER_PREF, sessionName);
            prefNode.putInt(LAST_SITE_PREF, saveVal);
            prefs.flush();
            IWorkbenchWindow window = PlatformUI.getWorkbench()
                .getActiveWorkbenchWindow();
            ISourceProviderService service = (ISourceProviderService) window
                .getService(ISourceProviderService.class);
            SiteSelectionState siteSelectionStateSourceProvider = (SiteSelectionState) service
                .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
            siteSelectionStateSourceProvider.setSiteSelection(siteWrapper);
            setEnabled(true);
            SessionManager.getInstance().updateSession();
        } catch (BackingStoreException e) {
            LOGGER.error("Could not save site preferences", e);
        }
    }

    public void updateSites(Collection<SiteWrapper> sites) {
        Assert.isNotNull(sites, "sites collection is null");

        if (currentSiteWrapper == null)
            currentSiteWrapper = allSiteWrapper;
        LOGGER.debug("site selected: " + currentSiteWrapper.getName());

        currentSiteWrappers.clear();
        currentSiteWrappers.add(0, allSiteWrapper);
        for (SiteWrapper site : sites) {
            currentSiteWrappers.add(site);
        }
        siteCombo.setInput(currentSiteWrappers);
        siteCombo.setSelection(currentSiteWrapper);
    }

    public void updateSites() {
        try {
            updateSites(ModelUtils.getSites(appService, null));
        } catch (Exception e) {
            LOGGER.error("Cannot update Sites", e);
        }
    }

    public SiteWrapper getCurrentSiteWrapper() {
        return currentSiteWrapper;
    }

    public void setSiteCombo(SiteCombo combo) {
        Assert.isNotNull(combo, "site combo is null");
        siteCombo = combo;
        siteCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                IStructuredSelection selection = (IStructuredSelection) event
                    .getSelection();
                SiteWrapper siteWrapper = (SiteWrapper) selection
                    .getFirstElement();

                if (siteWrapper == null)
                    return;

                currentSiteWrapper = siteWrapper;
                setCurrentSite(currentSiteWrapper);
                SessionManager.getInstance().getSession().rebuild();
                TreeViewer tv = SessionManager.getInstance().getTreeViewer();
                if (tv != null) {
                    // can be null if we start on the
                    // PatientAdministrationPerspective
                    tv.expandToLevel(3);
                }
            }
        });
    }

    public SiteCombo getSiteCombo() {
        Assert.isNotNull(siteCombo, "site manager is null");
        return siteCombo;
    }

    public void setEnabled(boolean enabled) {
        Assert.isNotNull(siteCombo, "site manager is null");
        if (!enabled) {
            currentSiteWrappers = new ArrayList<SiteWrapper>();
            siteCombo.setInput(currentSiteWrappers);
        }
        siteCombo.setEnabled(enabled);
    }

}
