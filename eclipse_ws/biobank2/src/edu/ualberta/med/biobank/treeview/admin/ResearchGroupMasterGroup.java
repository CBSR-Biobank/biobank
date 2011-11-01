package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ResearchGroupWrapper;
import edu.ualberta.med.biobank.treeview.AbstractResearchGroupGroup;

public class ResearchGroupMasterGroup extends AbstractResearchGroupGroup {

    public ResearchGroupMasterGroup(SessionAdapter sessionAdapter, int id) {
        super(sessionAdapter, id,
            Messages.ResearchGroupMasterGroup_all_rgroups_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(ResearchGroupWrapper.class)) {
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

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return ResearchGroupWrapper.getAllResearchGroups(SessionManager
            .getAppService());
    }

    public void addResearchGroup() {
        ResearchGroupWrapper researchGroup = new ResearchGroupWrapper(
            SessionManager.getAppService());
        ResearchGroupAdapter adapter = new ResearchGroupAdapter(this,
            researchGroup);
        adapter.openEntryForm();
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (int) ResearchGroupWrapper.getCount(SessionManager
            .getAppService());
    }

}
