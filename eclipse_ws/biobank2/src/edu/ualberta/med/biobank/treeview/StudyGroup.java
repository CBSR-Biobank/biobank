package edu.ualberta.med.biobank.treeview;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.listeners.AdapterChangedEvent;

public class StudyGroup extends AdapterBase {

    public StudyGroup(SiteAdapter parent, int id) {
        super(parent, id, "Studies", true);
    }

    public void openViewForm() {
        Assert.isTrue(false, "should not be called");
    }

    @Override
    protected String getNameInternal() {
        return null;
    }

    @Override
    public void executeDoubleClick() {
        performExpand();
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        MenuItem mi = new MenuItem(menu, SWT.PUSH);
        mi.setText("Add Study");
        mi.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent event) {
                StudyWrapper study = new StudyWrapper(parent.getAppService());
                study.setSite(getParentFromClass(SiteAdapter.class)
                    .getWrapper());
                StudyAdapter adapter = new StudyAdapter(StudyGroup.this, study);
                openForm(new FormInput(adapter), StudyEntryForm.ID);
            }
        });
    }

    @Override
    public String getTitle() {
        return null;
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected AdapterBase createChildNode() {
        return new StudyAdapter(this, null);
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        Assert.isTrue(child instanceof StudyWrapper);
        return new StudyAdapter(this, (StudyWrapper) child);
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        SiteWrapper currentSite = ((SiteAdapter) getParent()).getWrapper();
        Assert.isNotNull(currentSite, "null site");
        // read from database again
        currentSite.reload();
        return currentSite.getStudyCollection();
    }

    @Override
    public void notifyListeners(AdapterChangedEvent event) {
        getParent().notifyListeners(event);
    }
}
