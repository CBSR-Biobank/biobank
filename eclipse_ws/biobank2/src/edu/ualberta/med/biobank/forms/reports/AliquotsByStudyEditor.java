package edu.ualberta.med.biobank.forms.reports;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class AliquotsByStudyEditor extends ReportsEditor {

    public static String ID = "edu.ualberta.med.biobank.editors.AliquotsByStudyEditor";
    protected DateTimeWidget start;
    protected DateTimeWidget end;
    protected ComboViewer topCombo;

    @Override
    protected int[] getColumnWidths() {
        return new int[] { 100, 100 };
    }

    @Override
    protected void createOptionSection(Composite parent) {
        topCombo = createCustomCombo("Top Container Type", parent);
        start = widgetCreator.createDateTimeWidget(parent,
            "Start Date (Linked)", null, null, null);
        end = widgetCreator.createDateTimeWidget(parent, "End Date (Linked)",
            null, null, null);

    }

    private ComboViewer createCustomCombo(String label, Composite parent) {
        ComboViewer widget = widgetCreator.createComboViewer(parent, label,
            null, null);
        appService = SessionManager.getAppService();
        List<ContainerWrapper> containers = new ArrayList<ContainerWrapper>();
        Set<String> topContainerTypes = new HashSet<String>();
        try {
            // FIXME: uses all sites by default
            List<SiteWrapper> sites = SiteWrapper.getSites(appService);
            for (SiteWrapper site : sites) {
                containers.addAll(site.getTopContainerCollection());
            }
            for (ContainerWrapper c : containers) {
                topContainerTypes.add(c.getContainerType().getNameShort());
            }
        } catch (Exception e) {
            BioBankPlugin.openAsyncError("Error retrieving containers", e);
        }
        widget.setInput(topContainerTypes.toArray(new String[] {}));
        widget.getCombo().select(0);
        return widget;
    }

    @Override
    protected List<Object> getParams() {
        List<Object> params = new ArrayList<Object>();
        params.add(((IStructuredSelection) topCombo.getSelection())
            .getFirstElement());
        if (start.getDate() == null)
            params.add(new Date(0));
        else
            params.add(start.getDate());
        if (end.getDate() == null)
            params.add(new Date());
        else
            params.add(end.getDate());
        return params;
    }

    @Override
    protected String[] getColumnNames() {
        return new String[] { "Study", "Total" };
    }

    @Override
    protected List<String> getParamNames() {
        List<String> paramNames = new ArrayList<String>();
        paramNames.add("Top Container Type");
        paramNames.add("Start Date (Linked)");
        paramNames.add("End Date (Linked)");
        return paramNames;
    }

}
