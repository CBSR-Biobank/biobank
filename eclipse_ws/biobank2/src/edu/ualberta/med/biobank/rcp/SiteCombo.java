package edu.ualberta.med.biobank.rcp;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISourceProvider;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;
import org.eclipse.ui.services.ISourceProviderService;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.sourceproviders.SiteSelectionState;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SiteCombo extends WorkbenchWindowControlContribution {
    private static final String DISPATCH_SHIPMENTS_STATUS_MSG = "{0} dispatch shipments to receive";
    private ComboViewer comboViewer;

    public SiteCombo() {
        super("Site Selection");
        SessionManager.getInstance().setSiteCombo(this);
    }

    public SiteCombo(String str) {
        super(str);
    }

    public void setInput(List<SiteWrapper> sites) {
        comboViewer.setInput(sites);
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
        comboViewer = new ComboViewer(resizedComboPanel, SWT.NONE
            | SWT.DROP_DOWN | SWT.READ_ONLY);

        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());
        comboViewer.setComparator(new ViewerComparator());
        GridData gd = new GridData();
        gd.widthHint = 155;
        Combo combo = comboViewer.getCombo();
        combo.setLayoutData(gd);
        combo.setTextLimit(50);

        IWorkbenchWindow window = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow();
        ISourceProviderService service = (ISourceProviderService) window
            .getService(ISourceProviderService.class);
        ISourceProvider siteSelectionStateSourceProvider = service
            .getSourceProvider(SiteSelectionState.SITE_SELECTION_ID);
        siteSelectionStateSourceProvider
            .addSourceProviderListener(new ISourceProviderListener() {
                @Override
                public void sourceChanged(int sourcePriority,
                    @SuppressWarnings("rawtypes") Map sourceValuesByName) {
                    updateStatusLineMessage(PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage()
                        .getActivePart().getSite());
                }

                @Override
                public void sourceChanged(int sourcePriority,
                    String sourceName, Object sourceValue) {
                    IWorkbenchPage wPage = PlatformUI.getWorkbench()
                        .getActiveWorkbenchWindow().getActivePage();
                    if (wPage != null) {
                        IWorkbenchPart wPart = wPage.getActivePart();
                        if (wPart != null) {
                            updateStatusLineMessage(wPart.getSite());
                        }
                    }
                }
            });

        return resizedComboPanel;
    }

    public void updateStatusLineMessage(IWorkbenchPartSite wbSite) {
        if (wbSite instanceof IViewSite) {
            String message = "";

            if (!SessionManager.getInstance().isAllSitesSelected()) {
                Object o = ((StructuredSelection) comboViewer.getSelection())
                    .getFirstElement();
                if (o instanceof SiteWrapper) {
                    SiteWrapper site = (SiteWrapper) o;
                    int numPending = site
                        .getInTransitReceiveDispatchShipmentCollection().size();

                    message = MessageFormat.format(
                        DISPATCH_SHIPMENTS_STATUS_MSG, numPending == 0 ? "No"
                            : numPending);
                }
            }

            ((IViewSite) wbSite).getActionBars().getStatusLineManager()
                .setMessage(message);
        }
    }

    public void addSelectionChangedListener(ISelectionChangedListener listener) {
        comboViewer.addSelectionChangedListener(listener);
    }

    public void setEnabled(boolean enabled) {
        comboViewer.getCombo().setEnabled(enabled);
    }

    public void setSelection(SiteWrapper siteWrapper) {
        comboViewer.setSelection(new StructuredSelection(siteWrapper));
    }

    public ISelection getSelection() {
        return comboViewer.getSelection();
    }

    public Object getInput() {
        return comboViewer.getInput();
    }

}
