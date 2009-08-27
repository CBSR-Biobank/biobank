package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Study;

public class StudyAdapter extends AdapterBase {
    public static final int PATIENTS_NODE_ID = 0;

    private Study study;

    /**
     * if true, enable normal actions of this adapter
     */
    private boolean enableActions = true;

    public StudyAdapter(AdapterBase parent, Study study) {
        this(parent, study, true);
    }

    public StudyAdapter(AdapterBase parent, Study study, boolean enabledActions) {
        super(parent);
        this.setStudy(study);
        this.enableActions = enabledActions;

        if (study.getId() != null) {
            setId(study.getId());
            setName(study.getName());
        }
    }

    public void setStudy(Study study) {
        this.study = study;
    }

    public Study getStudy() {
        return study;
    }

    @Override
    public Integer getId() {
        Assert.isNotNull(study, "study is null");
        return study.getId();
    }

    @Override
    public String getName() {
        Assert.isNotNull(study, "study is null");
        return study.getNameShort();
    }

    @Override
    public String getTitle() {
        return getTitle("Study");
    }

    @Override
    public void performDoubleClick() {
        if (enableActions) {
            openForm(new FormInput(this), StudyViewForm.ID);
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (enableActions) {

            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Edit Study");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    openForm(new FormInput(StudyAdapter.this),
                        StudyEntryForm.ID);
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });

            mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("View Study");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    openForm(new FormInput(StudyAdapter.this), StudyViewForm.ID);
                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

    @Override
    protected Object getModelObject() {
        return study;
    }

}
