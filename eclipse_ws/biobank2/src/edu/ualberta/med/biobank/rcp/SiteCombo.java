package edu.ualberta.med.biobank.rcp;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IElementComparer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.menus.WorkbenchWindowControlContribution;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SiteCombo extends WorkbenchWindowControlContribution {

    private SessionAdapter session;
    public ComboViewer comboViewer;

    public SiteCombo() {
        super("Site Selection");
        SessionManager.getInstance().setSiteCombo(this);
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
        comboViewer = new ComboViewer(resizedComboPanel, SWT.NONE
            | SWT.DROP_DOWN | SWT.READ_ONLY);

        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new BiobankLabelProvider());

        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                    SiteWrapper siteWrapper = (SiteWrapper) selection
                        .getFirstElement();

                    if (siteWrapper != null) {
                        if (siteWrapper.getId() == null)
                            SessionManager.getInstance().setCurrentSite(null);
                        else
                            SessionManager.getInstance().setCurrentSite(
                                siteWrapper);
                        if (session != null)
                            session.rebuild();
                        SessionManager.getInstance().getTreeViewer()
                            .expandToLevel(3);
                    }
                }
            });
        comboViewer.setComparer(new IElementComparer() {
            @Override
            public boolean equals(Object a, Object b) {
                if (a instanceof SiteWrapper && b instanceof SiteWrapper) {
                    Integer ida = ((SiteWrapper) a).getId();
                    Integer idb = ((SiteWrapper) b).getId();
                    if (((ida == null) && (idb == null)) || ida.equals(idb))
                        return true;
                }
                return false;
            }

            @Override
            public int hashCode(Object element) {
                return element.hashCode();
            }

        });
        comboViewer.setComparator(new ViewerComparator());
        GridData gd = new GridData();
        gd.widthHint = 155;
        Combo combo = comboViewer.getCombo();
        combo.setLayoutData(gd);
        combo.setTextLimit(50);

        return resizedComboPanel;
    }

    public void setEnabled(boolean enabled) {
        comboViewer.getCombo().setEnabled(enabled);
    }

    public void setSelection(SiteWrapper siteWrapper) {
        comboViewer.setSelection(new StructuredSelection(siteWrapper));
    }

}
