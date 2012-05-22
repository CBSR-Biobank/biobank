package edu.ualberta.med.biobank.treeview.admin;

import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupAdapterInfo;
import edu.ualberta.med.biobank.common.action.researchGroup.ResearchGroupGetAllAction;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupCreatePermission;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ResearchGroupMasterGroup extends AbstractNewAdapterBase {
    private static final I18n i18n = I18nFactory
        .getI18n(ResearchGroupMasterGroup.class);

    private Boolean createAllowed;
    private Map<Integer, ResearchGroupAdapterInfo> rgs;

    @SuppressWarnings("nls")
    public ResearchGroupMasterGroup(SessionAdapter sessionAdapter, int id) {
        super(sessionAdapter, id,
            i18n.tr("All Research Groups"), null, false);
        try {
            this.createAllowed = SessionManager.getAppService().isAllowed(
                new ResearchGroupCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }

    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Add Research Group"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addResearchGroup();
                }
            });
        }
    }

    public void addResearchGroup() {
        ResearchGroupAdapter adapter = new ResearchGroupAdapter(this,
            new ResearchGroupAdapterInfo(null, null));
        adapter.openEntryForm();
    }

    @Override
    protected String getLabelInternal() {
        return null;
    }

    @Override
    public String getTooltipTextInternal() {
        return null;
    }

    @Override
    protected void runDelete() throws Exception {
    }

    @Override
    protected AbstractNewAdapterBase createChildNode() {
        return new ResearchGroupAdapter(this, null);
    }

    @Override
    protected AbstractAdapterBase createChildNode(Object child) {
        return new ResearchGroupAdapter(this, (ResearchGroupAdapterInfo) child);
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        if (rgs == null)
            rgs =
                SessionManager.getAppService()
                    .doAction(
                        new ResearchGroupGetAllAction()).getMap();
        return rgs;
    }

    @Override
    public String getViewFormId() {
        return null;
    }

    @Override
    public String getEntryFormId() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setValue(Object value) {
        this.rgs = (Map<Integer, ResearchGroupAdapterInfo>) value;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
