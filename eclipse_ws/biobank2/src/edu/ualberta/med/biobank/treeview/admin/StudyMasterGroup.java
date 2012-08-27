package edu.ualberta.med.biobank.treeview.admin;

import java.util.List;

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
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction;
import edu.ualberta.med.biobank.common.action.study.StudyGetAllAction.StudiesInfo;
import edu.ualberta.med.biobank.common.permission.study.StudyCreatePermission;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AbstractStudyGroup;

public class StudyMasterGroup extends AbstractStudyGroup {
    private static final I18n i18n = I18nFactory
        .getI18n(StudyMasterGroup.class);

    @SuppressWarnings("unused")
    private static BgcLogger LOGGER = BgcLogger
        .getLogger(StudyMasterGroup.class.getName());

    private StudiesInfo studiesInfo = null;

    private final Boolean createAllowed;

    @SuppressWarnings("nls")
    public StudyMasterGroup(SessionAdapter parent, int id) {
        super(parent, id, i18n.tr("All Studies"));

        this.createAllowed = isAllowed(new StudyCreatePermission());
    }

    @SuppressWarnings("nls")
    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        if (createAllowed) {
            MenuItem mi = new MenuItem(menu, SWT.PUSH);
            mi.setText(
                // menu item label.
                i18n.tr("Add Study"));
            mi.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent event) {
                    addStudy();
                }
            });
        }
    }

    @Override
    protected List<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        studiesInfo = SessionManager.getAppService().doAction(
            new StudyGetAllAction());

        return ModelWrapper.wrapModelCollection(SessionManager.getAppService(),
            studiesInfo.getStudies(), StudyWrapper.class);
    }

    public void addStudy() {
        // commenting out MVP for now
        // eventBus.fireEvent(new StudyCreateEvent());

        StudyWrapper study = new StudyWrapper(SessionManager.getAppService());
        StudyAdapter adapter = new StudyAdapter(this, study);
        adapter.openEntryForm();
    }

}
