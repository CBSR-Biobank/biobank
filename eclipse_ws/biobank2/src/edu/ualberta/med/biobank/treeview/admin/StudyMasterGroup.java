package edu.ualberta.med.biobank.treeview.admin;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction.StudiesInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyMasterGroup extends AbstractStudyGroup {

    private static BgcLogger LOGGER = BgcLogger
        .getLogger(StudyMasterGroup.class.getName());

    private StudiesInfo studiesInfo = null;

    private Boolean createAllowed;

    public StudyMasterGroup(SessionAdapter parent, int id) {
        super(parent, id, Messages.StudyMasterGroup_studies_node_label);
        try {
            this.createAllowed = SessionManager.getAppService().isAllowed(
                new StudyCreatePermission());
        } catch (ApplicationException e) {
            BgcPlugin.openAsyncError("Error", "Unable to retrieve permissions");
        }
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
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
    public void performExpand() {
        try {
            studiesInfo = SessionManager.getAppService().doAction(
                new StudyGetAllAction());
            super.performExpand();
        } catch (ApplicationException e) {
            // TODO: open an error dialog here?
            LOGGER.error("BioBankFormBase.createPartControl Error", e); //$NON-NLS-1$            
        }
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        List<StudyWrapper> result = new ArrayList<StudyWrapper>();

        if (studiesInfo != null) {
            // return results only if this node has been expanded
            for (Study study : studiesInfo.getStudies()) {
                StudyWrapper wrapper =
                    new StudyWrapper(SessionManager.getAppService(), study);
                result.add(wrapper);
            }
        }

        return result;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return (int) StudyWrapper.getCount(SessionManager.getAppService());
    }

    public void addStudy() {
        // commenting out MVP for now
        // eventBus.fireEvent(new StudyCreateEvent());

        StudyWrapper study = new StudyWrapper(SessionManager.getAppService());
        StudyAdapter adapter = new StudyAdapter(this, study);
        adapter.openEntryForm();
    }

}
