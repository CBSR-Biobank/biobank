package edu.ualberta.med.biobank;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;
import org.osgi.service.prefs.BackingStoreException;
import org.osgi.service.prefs.Preferences;

import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.rcp.Application;
import edu.ualberta.med.biobank.rcp.SiteCombo;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public class SiteManager {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(SiteManager.class.getName());

    private static final String SITE_PREF_NODE = "Site";

    private static final String LAST_SERVER_PREF = "lastServer";

    private static final String LAST_SITE_PREF = "lastSite";

    private WritableApplicationService appService;

    private String sessionName;

    private SiteWrapper currentSite;

    private SiteWrapper allSitesWrapper;

    private SiteCombo siteCombo;

    private List<SiteWrapper> currentSites;

    protected void init(WritableApplicationService appService,
        String sessionName) {
        this.appService = appService;
        this.sessionName = sessionName;
        currentSites = new ArrayList<SiteWrapper>();
        allSitesWrapper = new SiteWrapper(appService) {
            @Override
            public Integer getId() {
                return -9999;
            }

            @Override
            public String getName() {
                return "All Sites";
            }

            @Override
            public String getNameShort() {
                return "All Sites";
            }
        };
    }

    /*
     * selects the site the user was working with the last time he / she logged
     * out if logged into same server and same site exists
     */
    public void getCurrentSite(String serverName, Collection<SiteWrapper> sites) {
        if (currentSite != null)
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
                currentSite = site;
        }
    }

    public void setCurrentSite(SiteWrapper site) {
        try {
            currentSite = site;
            Integer saveVal = -1;
            if ((site != null) && (site.getId() != null))
                saveVal = site.getId();
            Preferences prefs =
                new InstanceScope().getNode(Application.PLUGIN_ID);
            Preferences prefNode = prefs.node(SITE_PREF_NODE);
            prefNode.put(LAST_SERVER_PREF, sessionName);
            prefNode.putInt(LAST_SITE_PREF, saveVal);
            prefs.flush();
            setSiteSelectionState(site);
            setEnabled(true);
            SessionManager.getInstance().updateSession();
        } catch (BackingStoreException e) {
            logger.error("Could not save site preferences", e);
        }
    }

    private void setSiteSelectionState(SiteWrapper site) {
        IWorkbenchWindow window =
            PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        ISourceProviderService service =
            (ISourceProviderService) window
                .getService(ISourceProviderService.class);
        SiteSelectionState siteSelectionStateSourceProvider =
            (SiteSelectionState) service
                .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        siteSelectionStateSourceProvider.setSiteSelection(site);
    }

    public void updateSites(Collection<SiteWrapper> sites) {
        Assert.isNotNull(sites, "sites collection is null");
        if (currentSite == null)
            currentSite = allSitesWrapper;
        logger.debug("site selected: " + currentSite.getName());

        currentSites.clear();
        currentSites.add(0, allSitesWrapper);
        for (SiteWrapper site : sites) {
            currentSites.add(site);
        }
        siteCombo.setInput(currentSites);
        siteCombo.setSelection(currentSite);
    }

    public void updateSites(boolean async) {
        if (async) {
            Display.getDefault().syncExec(new Runnable() {
                @Override
                public void run() {
                    updateSites(getSites());
                }
            });

        } else {
            updateSites(getSites());
        }
    }

    private List<SiteWrapper> getSites() {
        List<SiteWrapper> sites = null;
        try {
            sites = SiteWrapper.getSites(appService);
        } catch (Exception e) {
            logger.error("Cannot update Sites", e);
        }
        return sites;
    }

    public void updateSites() {
        updateSites(false);
    }

    public SiteWrapper getCurrentSite() {
        return currentSite;
    }

    public void setSiteCombo(SiteCombo combo) {
        Assert.isNotNull(combo, "site combo is null");
        if (siteCombo != combo) {
            siteCombo = combo;
            siteCombo
                .addSelectionChangedListener(new ISelectionChangedListener() {
                    @Override
                    public void selectionChanged(SelectionChangedEvent event) {
                        IStructuredSelection selection =
                            (IStructuredSelection) event.getSelection();
                        SiteWrapper siteWrapper =
                            (SiteWrapper) selection.getFirstElement();

                        if (siteWrapper == null)
                            return;

                        currentSite = siteWrapper;
                        setCurrentSite(currentSite);
                        closeAllSitesEditor();
                        // SessionManager.getInstance().rebuildSession();
                    }
                });
        }
    }

    public SiteCombo getSiteCombo() {
        Assert.isNotNull(siteCombo, "site manager is null");
        return siteCombo;
    }

    public void setEnabled(boolean enabled) {
        Assert.isNotNull(siteCombo, "site manager is null");
        if (!enabled) {
            currentSites = new ArrayList<SiteWrapper>();
            siteCombo.setInput(currentSites);
            setSiteSelectionState(null);
        }
        siteCombo.setEnabled(enabled);
    }

    public void lockSite() {
        siteCombo.setEnabled(false);
    }

    public void unlockSite() {
        siteCombo.setEnabled(true);
    }

    public boolean isAllSitesSelected() {
        if (currentSite == null) {
            return true;
        }
        return allSitesWrapper == currentSite;
    }

    protected void closeAllSitesEditor() {
        SiteAdapter sa = new SiteAdapter(null, allSitesWrapper);
        AdapterBase.closeEditor(new FormInput(sa));
    }

}
