package edu.ualberta.med.biobank.handler;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.ui.handlers.HandlerUtil;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.SiteEntryForm;
import edu.ualberta.med.biobank.forms.NodeInput;
import edu.ualberta.med.biobank.model.SdataType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StudyAddHandler extends AbstractHandler {
	public static final String ID = "edu.ualberta.med.biobank.commands.addSite";
	public static final String PARM_EDITOR = "edu.ualberta.med.biobank.commandsParameter.site";
	
	private SiteAdapter siteAdapter;
	private List<SdataType> sdataTypes;
	private ExecutionEvent executionEvent;

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Object param = event.getObjectParameterForExecution(PARM_EDITOR);
		if (!(param instanceof SiteAdapter)) {
			throw new ExecutionException("Invalid parameter: " + siteAdapter);
		}
		
		siteAdapter = (SiteAdapter) param;
		
		getStudyDataTypes();		
		return null;
	}
	
	private void getStudyDataTypes() {	
		
		final Job job = new Job("Querying ") {
			protected IStatus run(IProgressMonitor monitor) {
				SdataType sdataType = new SdataType();		
				try {
					sdataTypes = siteAdapter.getAppService().search(SdataType.class, sdataType);
					return Status.OK_STATUS;
				} catch (ApplicationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return Status.CANCEL_STATUS;
			}
		};
		
		job.addJobChangeListener(new JobChangeAdapter() {
			public void done(IJobChangeEvent event) {
				if (!event.getResult().isOK()) {
					//postError("Job did not complete successfully");
					return;
				}
				
				Study study = new Study();
				StudyAdapter studyAdapter = new StudyAdapter(siteAdapter, study);
				NodeInput input = new NodeInput(studyAdapter);
				
				try {
					HandlerUtil.getActiveWorkbenchWindowChecked(executionEvent).getActivePage()
					.openEditor(input, SiteEntryForm.ID, true);
				}
				catch (Exception exp) {
					exp.printStackTrace();
				}
			}
		});
		job.setSystem(true);		
		job.schedule(); // start as soon as possible
	}
	
	public boolean isEnabled() {
		return (BioBankPlugin.getDefault().getSessionCount() > 0);
	}
}
