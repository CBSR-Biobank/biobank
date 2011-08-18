package edu.ualberta.med.biobank.treeview.admin;

import java.util.Collection;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;

public class StudyMasterGroup extends AbstractStudyGroup {

    public StudyMasterGroup(SessionAdapter parent, int id) {
        super(parent, id, Messages.StudyMasterGroup_studies_node_label);
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (SessionManager.canCreate(StudyWrapper.class)) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(Messages.StudyMasterGroup_add_label);
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addStudy();
                }
            });
        }
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return StudyWrapper.getAllStudies(getAppService());
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (int) StudyWrapper.getCount(getAppService());
    }

    public void addStudy() {
        StudyWrapper study = new StudyWrapper(SessionManager.getAppService());
        StudyAdapter adapter = new StudyAdapter(this, study);
        adapter.openEntryForm();
    }

}
