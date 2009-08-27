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
import org.springframework.remoting.RemoteAccessException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Site;

public class SiteCombo extends ControlContribution {

    private List<Site> sites;
    public Combo combo;

    public SiteCombo(String str) {
        super(str);
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
                if (combo.getSelectionIndex() < sites.size()) {
                    SessionManager.getInstance().setCurrentSite(
                        sites.get(combo.getSelectionIndex()));
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
        try {
            // read from database again
            this.sites = updatedSites;
            for (Site site : sites) {
                SessionManager.getLogger()
                    .trace(
                        "updateSites: Site " + site.getId() + ": "
                            + site.getName());
            }
        } catch (final RemoteAccessException exp) {
            BioBankPlugin.openRemoteAccessErrorMessage();
        } catch (Exception e) {
            SessionManager.getLogger().error(
                "Error while loading sites for SiteCombo");
        }
        for (Site site : sites) {
            combo.add(site.getName());
        }
        combo.select(0);

    }

    public void setValue(int index) {
        combo.select(index);
    }

}
