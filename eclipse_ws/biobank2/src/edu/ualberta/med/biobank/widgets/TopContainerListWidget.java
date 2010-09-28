package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class TopContainerListWidget extends BiobankWidget {

    private class NameFilter extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, Object top, Object child) {
            if (filterText.equals(""))
                return true;
            return filterText.startsWith(((ContainerWrapper) child).getLabel());
        }
    }

    private ComboViewer siteCombo;
    private ListViewer topContainers;
    private String filterText;
    private Boolean enabled;

    public TopContainerListWidget(Composite parent, int style) {
        super(parent, SWT.BORDER);
        setLayout(new GridLayout(2, false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        setLayoutData(gd);
        filterText = "";
        enabled = true;
        Label l = new Label(this, SWT.NONE);
        l.setText("Site:");
        final BiobankApplicationService appService = (BiobankApplicationService) SessionManager
            .getAppService();
        siteCombo = new ComboViewer(this, SWT.NONE);
        siteCombo.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object e) {
                return ((SiteWrapper) e).getNameShort();
            }
        });
        siteCombo.setContentProvider(new ArrayContentProvider());
        try {
            siteCombo.setInput(SessionManager.getInstance().getSiteCombo()
                .getInput());
        } catch (Exception e1) {
            BioBankPlugin.openAsyncError("Failed to load sites", e1);
        }
        siteCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (siteCombo.getSelection() != null) {
                    List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
                    try {
                        SiteWrapper s = (SiteWrapper) ((IStructuredSelection) siteCombo
                            .getSelection()).getFirstElement();
                        if (s.getId() == -9999) {
                            List<SiteWrapper> sites = SiteWrapper
                                .getSites(appService);
                            for (SiteWrapper site : sites) {
                                containers.addAll(site
                                    .getTopContainerCollection());
                            }
                        } else
                            containers.addAll(s.getTopContainerCollection());
                    } catch (Exception e) {
                        BioBankPlugin.openAsyncError(
                            "Error retrieving containers", e);
                    }
                    topContainers.setInput(containers);
                    filterBy(filterText);
                    getShell().layout(true, true);
                }
            }
        });
        Label l2 = new Label(this, SWT.NONE);
        l2.setText("Top Containers:");
        topContainers = new ListViewer(this, SWT.MULTI | SWT.BORDER);
        topContainers.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ContainerWrapper) element).getLabel()
                    + "("
                    + ((ContainerWrapper) element).getContainerType()
                        .getNameShort() + ")";
            }
        });
        topContainers.setContentProvider(new ArrayContentProvider());
        topContainers.getList().setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true));

        siteCombo.setSelection(new StructuredSelection(SessionManager
            .getInstance().getCurrentSite()));
    }

    public List<Integer> getSelectedContainers() {
        List<Integer> containerList = new ArrayList<Integer>();
        IStructuredSelection selections = (IStructuredSelection) topContainers
            .getSelection();
        Iterator<?> it = selections.iterator();
        while (it.hasNext()) {
            containerList.add(((ContainerWrapper) it.next()).getId());
        }
        if (containerList.size() == 0) {
            Iterator<?> it2 = ((List<?>) topContainers.getInput()).iterator();
            while (it2.hasNext()) {
                containerList.add(((ContainerWrapper) it2.next()).getId());
            }
        }
        return containerList;
    }

    public void filterBy(String text) {
        filterText = text;
        topContainers.addFilter(new NameFilter());
        topContainers.setSelection(null);
        if (topContainers.getList().getItemCount() != 0) {
            setEnabled(true);
        } else
            setEnabled(false);
    }

    @Override
    public void setEnabled(boolean b) {
        enabled = b;
        topContainers.getList().setEnabled(b);
        if (!b)
            topContainers.setSelection(null);
        notifyListeners();
    }

    @Override
    public boolean getEnabled() {
        return enabled;
    }
}
