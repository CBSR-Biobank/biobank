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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.gui.common.BiobankGuiCommonPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class TopContainerListWidget {

    private SelectionListener listener;

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

    @SuppressWarnings("unchecked")
    public TopContainerListWidget(final Composite parent, FormToolkit toolkit) {
        filterText = "";
        enabled = true;
        toolkit.createLabel(parent, "Site:");
        final BiobankApplicationService appService = SessionManager
            .getAppService();
        siteCombo = new ComboViewer(parent, SWT.NONE);
        siteCombo.setLabelProvider(new BiobankLabelProvider() {
            @Override
            public String getText(Object e) {
                return ((SiteWrapper) e).getNameShort();
            }
        });
        siteCombo.setContentProvider(new ArrayContentProvider());
        try {
            List<SiteWrapper> sites = SiteWrapper.getSites(appService);
            SiteWrapper allsites = new SiteWrapper(appService);
            allsites.setNameShort("All Sites");
            sites.add(allsites);
            siteCombo.setInput(sites);
            siteCombo.getCombo().select(0);
        } catch (Exception e1) {
            BiobankGuiCommonPlugin.openAsyncError("Failed to load sites", e1);
        }
        siteCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                if (siteCombo.getSelection() != null) {
                    List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
                    try {
                        SiteWrapper s = (SiteWrapper) ((IStructuredSelection) siteCombo
                            .getSelection()).getFirstElement();
                        if (s.getNameShort().equals("All Sites")) {
                            List<SiteWrapper> sites = SiteWrapper
                                .getSites(appService);
                            for (SiteWrapper site : sites) {
                                containers.addAll(site
                                    .getTopContainerCollection());
                            }
                        } else
                            containers.addAll(s.getTopContainerCollection());
                    } catch (Exception e) {
                        BiobankGuiCommonPlugin.openAsyncError(
                            "Error retrieving containers", e);
                    }
                    topContainers.setInput(containers);
                    filterBy(filterText);
                    parent.getShell().layout(true, true);
                }
            }
        });
        siteCombo.getCombo().setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true));
        toolkit.createLabel(parent, "Top Containers\n(select one or more):");
        topContainers = new ListViewer(parent, SWT.MULTI | SWT.BORDER);
        topContainers.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                return ((ContainerWrapper) element).getLabel()
                    + "("
                    + ((ContainerWrapper) element).getContainerType()
                        .getNameShort() + ") ("
                    + ((ContainerWrapper) element).getSite().getNameShort()
                    + ")";
            }
        });
        topContainers.setContentProvider(new ArrayContentProvider());
        topContainers.getList().setLayoutData(
            new GridData(SWT.FILL, SWT.FILL, true, true));

        siteCombo.setSelection(new StructuredSelection(
            ((List<SiteWrapper>) siteCombo.getInput()).get(0)));
    }

    public List<Integer> getSelectedContainerIds() {
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

    public void setEnabled(boolean b) {
        enabled = b;
        topContainers.getList().setEnabled(b);
        if (!b)
            topContainers.setSelection(null);
        notifyListeners();
    }

    private void notifyListeners() {
        if (listener != null) {
            Event e1 = new Event();
            e1.widget = siteCombo.getCombo();
            SelectionEvent e = new SelectionEvent(e1);
            listener.widgetSelected(e);
        }
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void addSelectionChangedListener(SelectionListener l) {
        listener = l;
    }

    public List<String> getSelectedContainerNames() {
        List<String> containerList = new ArrayList<String>();
        IStructuredSelection selections = (IStructuredSelection) topContainers
            .getSelection();
        Iterator<?> it = selections.iterator();
        while (it.hasNext()) {
            ContainerWrapper c = (ContainerWrapper) it.next();
            containerList.add(c.getFullInfoLabel());
        }
        if (containerList.size() == 0) {
            Iterator<?> it2 = ((List<?>) topContainers.getInput()).iterator();
            while (it2.hasNext()) {
                containerList.add(((ContainerWrapper) it2.next())
                    .getFullInfoLabel());
            }
        }
        return containerList;
    }
}
