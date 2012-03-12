package edu.ualberta.med.biobank.treeview.admin;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.info.ResearchGroupAdapterInfo;
import edu.ualberta.med.biobank.common.permission.researchGroup.ResearchGroupCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.ResearchGroup;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AbstractNewAdapterBase;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ResearchGroupMasterGroup extends AbstractNewAdapterBase {

    private Map<Integer, ResearchGroup> rgs;
    private Boolean createAllowed;

    public ResearchGroupMasterGroup(SessionAdapter sessionAdapter, int id) {
        super(sessionAdapter, id,
            Messages.ResearchGroupMasterGroup_all_rgroups_label, null, false);
        try {
            rgs = ResearchGroupWrapper.getAllResearchGroups(SessionManager
                .getAppService());
            for (Integer rgId : rgs.keySet())
                this.addChild(new ResearchGroupAdapter(this,
                    new ResearchGroupAdapterInfo(rgId, rgs.get(rgId)
                        .getNameShort())));
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Unable to retrieve research groups", e);
        }
        try {
            this.createAllowed = SessionManager.getAppService().isAllowed(
                new ResearchGroupCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }

    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.ResearchGroupMasterGroup_add_rgroup_menu);
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
        return null;
    }

    @Override
    protected AbstractAdapterBase createChildNode(Object child) {
        return null;
    }

    @Override
    protected Map<Integer, ?> getChildrenObjects() throws Exception {
        HashMap<Integer, ResearchGroupAdapterInfo> map =
            new HashMap<Integer, ResearchGroupAdapterInfo>();
        for (Integer rgId : rgs.keySet())
            map.put(rgId, new ResearchGroupAdapterInfo(rgId, rgs.get(rgId)
                .getNameShort()));
        return map;
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
        this.rgs = (Map<Integer, ResearchGroup>) value;
    }

    @Override
    public int compareTo(AbstractAdapterBase o) {
        // TODO Auto-generated method stub
        return 0;
    }

}
