package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.AdaptorBase;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class StudyAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addStudy";

    public Object execute(ExecutionEvent event) throws ExecutionException {

        SessionAdapter sessionAdapter = SessionManager.getInstance()
            .getSessionSingle();
        SiteAdapter siteAdapter = (SiteAdapter) sessionAdapter.getChild(0);

        IWorkbenchPage activePage = PlatformUI.getWorkbench()
            .getActiveWorkbenchWindow().getActivePage().getActivePart()
            .getSite().getPage();

        try {
            Study study = new Study();
            AdaptorBase studiesNode = siteAdapter.getStudiesGroupNode();
            StudyAdapter studyAdapter = new StudyAdapter(studiesNode, study);
            activePage.openEditor(new FormInput(studyAdapter),
                StudyEntryForm.ID, true);
        } catch (PartInitException exp) {
            exp.printStackTrace();
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
