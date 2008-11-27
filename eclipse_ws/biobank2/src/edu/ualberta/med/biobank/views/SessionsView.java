package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyGroup;
import edu.ualberta.med.biobank.model.IStudiesListener;

public class SessionsView extends ViewPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.views.sessions";

	private TreeViewer treeViewer;
	
	private StudyGroup studyGroup;
	
	public SessionsView() {
		super();
	}

	@Override
	public void createPartControl(Composite parent) {
		studyGroup = new StudyGroup();
		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		//treeViewer.setInput(session.getRoot());
		studyGroup.addStudiesListener(new IStudiesListener() {
			public void studiesChanged(StudyGroup studyGroup, Study study) {
				treeViewer.refresh();
			}
		});
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
