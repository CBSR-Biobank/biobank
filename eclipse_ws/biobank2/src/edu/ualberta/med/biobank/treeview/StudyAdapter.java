package edu.ualberta.med.biobank.treeview;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.StudyViewForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Study;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;

public class StudyAdapter extends AdapterBase {

    public static final int PATIENTS_NODE_ID = 0;

    /**
     * if true, enable normal actions of this adapter
     */
    private boolean enableActions = true;

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper,
        boolean enabledActions) {
        super(parent, studyWrapper);
        this.enableActions = enabledActions;

        if (studyWrapper.getId() != null) {
            setId(studyWrapper.getId());
            setName(studyWrapper.getName());
        }
    }

    public StudyAdapter(AdapterBase parent, StudyWrapper studyWrapper) {
        this(parent, studyWrapper, true);
    }

    public void setStudy(Study study) {
        object = study;
    }

    public Study getStudy() {
        return (Study) object;
    }

    @Override
    public String getName() {
        Study study = getStudy();
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
            mi = new MenuItem(menu, SWT.PUSH);
            mi.setText("Delete Study");
            mi.addSelectionListener(new SelectionListener() {
                public void widgetSelected(SelectionEvent event) {
                    Boolean confirm = MessageDialog.openConfirm(PlatformUI
                        .getWorkbench().getActiveWorkbenchWindow().getShell(),
                        "Confirm Delete",
                        "Are you sure you want to delete this study?");

                    if (confirm) {
                        delete();
                    }

                }

                public void widgetDefaultSelected(SelectionEvent e) {
                }
            });
        }
    }

    @Override
    public void delete() {
        // FIXME when wrapper is used : remove this method to use the
        // parent one
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            Study study = getStudy();
            SDKQuery query = new DeleteExampleQuery(study);

            public void run() {
                if (study.getPatientCollection().size() > 0) {
                    BioBankPlugin.openError("Error", "Unable to delete study "
                        + study.getName()
                        + ". All defined patients must be removed first.");
                } else
                    try {
                        getAppService().executeQuery(query);
                        StudyAdapter.this.getParent().removeChild(
                            StudyAdapter.this);
                    } catch (ApplicationException e) {
                        BioBankPlugin.openAsyncError("Delete error", e);
                    }

            }
        });
    }

    @Override
    public void loadChildren(boolean updateNode) {
    }

    @Override
    public AdapterBase accept(NodeSearchVisitor visitor) {
        return visitor.visit(this);
    }

}
