package edu.ualberta.med.biobank.rcp;

import org.eclipse.jface.action.ControlContribution;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.widgets.BiobankLabelProvider;

public class SiteCombo extends ControlContribution {

    private SessionAdapter session;
    public ComboViewer comboViewer;

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
                    Site site = (Site) selection.getFirstElement();

                    if (site != null) {
                        if (site.getId() == null)
                            SessionManager.getInstance().setCurrentSite(null);
                        else
                            SessionManager.getInstance().setCurrentSite(site);
                        if (session != null) {
                            session.rebuild();
                            SessionManager.getInstance().getTreeViewer()
                                .expandToLevel(3);
                        }
                    }
                }
            });
        comboViewer.setComparer(new IElementComparer() {
            @Override
            public boolean equals(Object a, Object b) {
                if (a instanceof Site && b instanceof Site) {
                    Integer ida = ((Site) a).getId();
                    Integer idb = ((Site) b).getId();
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

    public void setSelection(Site site) {
        comboViewer.setSelection(new StructuredSelection(site));
    }

}
