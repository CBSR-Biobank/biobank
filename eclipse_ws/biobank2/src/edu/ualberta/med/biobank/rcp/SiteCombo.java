package edu.ualberta.med.biobank.rcp;

import java.util.List;

import org.eclipse.jface.action.ControlContribution;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SessionAdapter;

public class SiteCombo extends ControlContribution {

    private List<Site> sites;
    private SessionAdapter session;
    public Combo combo;

    public SiteCombo() {
        super("Site Selection");
    }

    public SiteCombo(String str) {
        super(str);

    }

    public void setSession(SessionAdapter session) {
        this.session = session;
    }

    @Override
    protected Control createControl(Composite parent) {
        Composite resizedComboPanel = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(2, false);
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        // layout.horizontalSpacing = 5;
        resizedComboPanel.setLayout(layout);
        Label siteLabel = new Label(resizedComboPanel, SWT.NONE);
        siteLabel.setText("Working Site: ");
        combo = new Combo(resizedComboPanel, SWT.NONE | SWT.DROP_DOWN
            | SWT.READ_ONLY);

        combo.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent e) {
                if (combo.getSelectionIndex() <= sites.size()) {
                    if (combo.getSelectionIndex() == 0)
                        SessionManager.getInstance().setCurrentSite(null);
                    else if (combo.getSelectionIndex() > 0)
                        SessionManager.getInstance().setCurrentSite(
                            sites.get(combo.getSelectionIndex() - 1));
                    if (session != null)
                        session.rebuild();
                }
            }
        });
        GridData gd = new GridData();
        gd.widthHint = 155;
        combo.setLayoutData(gd);
        combo.setTextLimit(50);
        combo.select(0);
        return resizedComboPanel;
    }

    public void loadChildren(List<Site> updatedSites) {
        sites = updatedSites;
        for (Site site : sites) {
            SessionManager.getLogger().trace(
                "updateSites: Site " + site.getId() + ": " + site.getName());
        }
        combo.removeAll();
        combo.add("All Sites");
        for (Site site : sites) {
            combo.add(site.getName());
        }
        combo.select(0);
    }

    public void setValue(int index) {
        combo.select(index);
    }

    public void setEnabled(boolean enabled) {
        combo.setEnabled(enabled);
    }

    public void addChild(Site site) {
        sites.add(site);
        loadChildren(sites);
    }

}
