package edu.ualberta.med.biobank.handler;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.forms.NodeInput;
import edu.ualberta.med.biobank.forms.StudyEntryForm;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class StudyAddHandler extends AbstractHandler {
	public static final String ID = "edu.ualberta.med.biobank.commands.addStudy";
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		
		Study study = new Study();
		StudyAdapter studyNode = new StudyAdapter(null, study);
		
		NodeInput input = new NodeInput(studyNode);
		try {
			HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage()
			.openEditor(input, StudyEntryForm.ID, true);
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		
		return null;
	}
	
	public boolean isEnabled() {
		return (SessionManager.getInstance().getSessionCount() > 0);
	}
}
