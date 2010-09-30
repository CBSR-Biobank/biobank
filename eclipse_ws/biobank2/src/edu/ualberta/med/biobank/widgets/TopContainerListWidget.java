package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.server.applicationservice.BiobankApplicationService;

public class TopContainerListWidget {

    private class NameFilter extends ViewerFilter {
        @Override
        public boolean select(Viewer viewer, Object top, Object child) {
            if (filterText.equals(""))
                return true;
            return filterText.startsWith(((ContainerWrapper) child).getLabel());
        }
    }

    private ListViewer topContainers;
    private String filterText;
    private boolean enabled;

    public TopContainerListWidget(Composite parent, int style) {
        filterText = "";
        enabled = true;
        topContainers = new ListViewer(parent, SWT.MULTI | SWT.BORDER | style);
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
        BiobankApplicationService appService = SessionManager.getAppService();
        List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
        try {
            // FIXME: uses all sites by default
            List<SiteWrapper> sites = SiteWrapper.getSites(appService);
            for (SiteWrapper site : sites) {
                containers.addAll(site.getTopContainerCollection());
            }
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error retrieving containers", e);
        }
        topContainers.setInput(containers);
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

    public void setEnabled(boolean b) {
        enabled = b;
        topContainers.getList().setEnabled(b);
        if (!b)
            topContainers.setSelection(null);
    }

    public boolean getEnabled() {
        return enabled;
    }
}
