package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.widgets.utils.WidgetCreator;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

@Deprecated
public class BasicSiteCombo extends BiobankWidget {

    private ComboViewer comboViewer;

    private boolean canUpdateOnly;

    public BasicSiteCombo(Composite parent, WidgetCreator widgetCreator,
        WritableApplicationService appService, Label label,
        boolean canUpdateOnly, ComboSelectionUpdate csu) {
        super(parent, SWT.NONE);
        this.canUpdateOnly = canUpdateOnly;

        GridLayout l = new GridLayout(2, false);
        l.marginWidth = 0;
        l.horizontalSpacing = 0;
        l.verticalSpacing = 0;
        setLayout(l);
        setLayoutData(new GridData(SWT.FILL, SWT.LEFT, true, false));

        comboViewer = widgetCreator.createComboViewer(this, label, null, null,
            "A site should be selected", true, null, csu);
        comboViewer.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object e) {
                return ((SiteWrapper) e).getNameShort();
            }
        });

        if (appService != null) {
            init(appService);
        }
    }

    public void init(WritableApplicationService appService) {
        try {
            List<SiteWrapper> allSites = SiteWrapper.getSites(appService);
            if (canUpdateOnly) {
                List<SiteWrapper> updateSites = new ArrayList<SiteWrapper>();
                for (SiteWrapper site : allSites)
                    if (SessionManager.getUser().canUpdateSite(site))
                        updateSites.add(site);
                comboViewer.setInput(updateSites);
            } else {
                comboViewer.setInput(allSites);
            }
        } catch (Exception e1) {
            BiobankPlugin.openAsyncError("Failed to load sites", e1);
        }
    }

    public void setSitesList(List<SiteWrapper> sites) {
        comboViewer.setInput(sites);
    }

    /**
     * @param defaultSelection will select a site if there is only one in the
     *            list
     */
    public void setSelectedSite(CenterWrapper<?> centerWrapper,
        boolean defaultSelection) {
        if (comboViewer != null) {
            if (centerWrapper == null) {
                comboViewer.getCombo().deselectAll();
                if (defaultSelection
                    && comboViewer.getCombo().getItemCount() == 1)
                    comboViewer.setSelection(new StructuredSelection(
                        comboViewer.getElementAt(0)));
            } else
                comboViewer
                    .setSelection(new StructuredSelection(centerWrapper));
        }
    }

    public SiteWrapper getSelectedSite() {
        if (comboViewer != null && comboViewer.getSelection() != null) {
            return (SiteWrapper) ((StructuredSelection) comboViewer
                .getSelection()).getFirstElement();
        }
        return null;
    }

    public SiteWrapper getFirstSite() {
        if (comboViewer.getCombo().getItemCount() > 0)
            return (SiteWrapper) comboViewer.getElementAt(0);
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        comboViewer.getControl().setEnabled(enabled);
    }

}
