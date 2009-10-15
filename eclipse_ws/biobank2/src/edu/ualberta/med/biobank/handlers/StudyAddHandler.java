package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Assert;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class StudyAddHandler extends AbstractHandler {
    public static final String ID = "edu.ualberta.med.biobank.commands.addStudy";

    public Object execute(ExecutionEvent event) throws ExecutionException {
        SiteWrapper site = SessionManager.getInstance().getCurrentSiteWrapper();
        SiteAdapter siteAdapter = (SiteAdapter) SessionManager.getInstance()
            .searchNode(site);
        Assert.isNotNull(siteAdapter);
        StudyWrapper study = new StudyWrapper(SessionManager.getAppService());
        study.setSite(site);
        StudyAdapter studyNode = new StudyAdapter(siteAdapter
            .getStudiesGroupNode(), study);

        FormInput input = new FormInput(studyNode);
        try {
            HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
                .openEditor(input, StudyEntryForm.ID, true);
        } catch (Exception exp) {
            throw new ExecutionException("Error opening form "
                + StudyEntryForm.ID, exp);
        }

        return null;
    }

    @Override
    public boolean isEnabled() {
        return (SessionManager.getInstance().getSession() != null);
    }
}
