package edu.ualberta.med.biobank.views;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.Platform;
import edu.ualberta.med.biobank.Activator;
import edu.ualberta.med.biobank.model.BioBankGroup;
import edu.ualberta.med.biobank.model.BioBank;
import edu.ualberta.med.biobank.model.WsObject;
import edu.ualberta.med.biobank.model.IBioBankGroupListener;
import edu.ualberta.med.biobank.WsObjectAdapterFactory;
import gov.nih.nci.system.applicationservice.ApplicationService;

public class SessionsView extends ViewPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.views.sessions";

	private TreeViewer treeViewer;
	
	private BioBankGroup bioBankGroup;

	private IAdapterFactory adapterFactory = new WsObjectAdapterFactory();
	
	public SessionsView() {
		super();
		Activator.getDefault().setSessionView(this);
	}

	@Override
	public void createPartControl(Composite parent) {
		bioBankGroup = new BioBankGroup();
		bioBankGroup.setName("root");
		
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.MULTI
				| SWT.V_SCROLL);
		getSite().setSelectionProvider(treeViewer);
		Platform.getAdapterManager().registerAdapters(adapterFactory, WsObject.class);
		treeViewer.setLabelProvider(new WorkbenchLabelProvider());
		treeViewer.setContentProvider(new BaseWorkbenchContentProvider());
		treeViewer.setInput(bioBankGroup);
		bioBankGroup.addListener(new IBioBankGroupListener() {
			public void bioBankChanged(BioBankGroup group, BioBank bioBank) {
				treeViewer.refresh();
			}
		});
	}
	
	public void dispose() {
		Platform.getAdapterManager().unregisterAdapters(adapterFactory);
		super.dispose();
	}
	
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}
	
	public void addSession(ApplicationService appService, String name) {
		BioBank bioBank = new BioBank(appService, name);
		bioBank.setParent(bioBankGroup);
		bioBankGroup.addBioBank(bioBank);
	}

}
