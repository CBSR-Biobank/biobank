package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.SiteGetHelper;
import edu.ualberta.med.biobank.model.Address;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class SiteViewForm extends AddressViewForm {	
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteViewForm";
	
	private SiteAdapter siteAdapter;
	
	private Site site;
	
	private TableViewer studyTableViewer;
    private TableViewer clinicTableViewer;
	
	
	Label name;

	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {		
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void init(IEditorSite editorSite, IEditorInput input)
			throws PartInitException {
		super.init(editorSite, input);
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input"); 
		
		Node node = ((NodeInput) input).getNode();
		Assert.notNull(node, "Null editor input");

		if (node instanceof SiteAdapter) {
			siteAdapter = (SiteAdapter) node;
			site = siteAdapter.getSite();
			address = site.getAddress();
			setPartName("Site " + site.getName());
		}
		else {
			Assert.isTrue(false, "Invalid editor input: object of type "
				+ node.getClass().getName());
		}
	}
    
    // We don't want to modify the Site object we already have in memory.
    // Therefore, we need to get a new one from the ORM
    private void loadSite() {
        Assert.isTrue((site.getId() != null) && (site.getId() != 0),
            "site not in database");

        SiteGetHelper helper = new SiteGetHelper(
            siteAdapter.getAppService(), site.getId(), SiteGetHelper.LOAD_ALL);

        BusyIndicator.showWhile(
            PlatformUI.getWorkbench().getActiveWorkbenchWindow()
            .getShell().getDisplay(), helper);

        site = helper.getResult();
    }

	@Override
	public void createPartControl(Composite parent) {
		loadSite();
		address = site.getAddress();
		toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);  

		if (site.getName() != null) {
			form.setText("BioBank Site: " + site.getName());
		}
		
		form.getBody().setLayout(new GridLayout(1, false));
		form.getBody().setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		createAddressSection();
		createStudySection();
		createClinicSection();
		createButtons();
        
        bindValues();
	}
	
	private void createAddressSection() {        
        Section section = toolkit.createSection(form.getBody(), 
            Section.TWISTIE | Section.TITLE_BAR); // | Section.EXPANDED);
        section.setText("Address");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        Composite sbody;
        sbody = toolkit.createComposite(section);
        section.setClient(sbody);
        
        sbody.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(sbody);     
        createAddressArea(sbody);
	}
	
    private void createStudySection() {        
        Section section = toolkit.createSection(form.getBody(), 
            Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Studies");
        section.setLayout(new GridLayout(1, false));
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 300;
        section.setLayoutData(gd);   
        Composite sbody;
        sbody = toolkit.createComposite(section);
        section.setClient(sbody);        
        sbody.setLayout(new GridLayout(2, false));
        gd = new GridData();
        gd.heightHint = 300;
        sbody.setLayoutData(gd);
        toolkit.paintBordersFor(sbody);
        
        studyTableViewer = new TableViewer(sbody, SWT.MULTI | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION);
        studyTableViewer.setLabelProvider(new StudyLabelProvider());
        studyTableViewer.setContentProvider(new StudyContentProvider());
        
        //Table table = toolkit.createTable(sbody, SWT.NONE);
        Table table = studyTableViewer.getTable();
        table.setLayout(new TableLayout());
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 300;
        table.setLayoutData(gd);
        table.setFont(sbody.getFont());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        String [] colNames = new String[] {"Name", "Short Name", "Num. Patients"};
        for (String name : colNames) {
            TableColumn col = new TableColumn(table, SWT.NONE);
            col.setText(name);
            col.setResizable(true);
            //col.setWidth(100);
        }
        studyTableViewer.setColumnProperties(colNames);
        
        // hack required here because site.getStudyCollection().toArray(new Study[0])
        // returns Object[].        
        int count = 0;
        Collection<Study> studies = site.getStudyCollection();
        Study [] arr = new Study [studies.size()];
        Iterator<Study> it = studies.iterator();
        while (it.hasNext()) {
            arr[count] = it.next();
            ++count;
        }        
        studyTableViewer.setInput(arr);
        
        for (int i = 0, n = table.getColumnCount(); i < n; i++) {
            table.getColumn(i).pack();
        }

        studyTableViewer.addDoubleClickListener(new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                Object element = ((StructuredSelection)selection).getFirstElement();
                StudyAdapter node = new StudyAdapter(null, (Study) element);
                SessionManager.getInstance().openStudyViewForm(node);
            }
        });
    }
    
    private void createClinicSection() {        
        Section section = toolkit.createSection(form.getBody(), 
            Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Clinics");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));        
        Composite sbody;
        sbody = toolkit.createComposite(section);
        section.setClient(sbody);
        
        sbody.setLayout(new GridLayout(2, false));
        toolkit.paintBordersFor(sbody);   
        
        Table table = toolkit.createTable(sbody, SWT.NONE);
        table.setLayoutData(new GridData(GridData.VERTICAL_ALIGN_BEGINNING | GridData.FILL_HORIZONTAL));
        clinicTableViewer = new TableViewer(table);
        clinicTableViewer.setLabelProvider(new LabelProvider());
        clinicTableViewer.setContentProvider(new ClinicContentProvider());
        
        String[] titles = {"Name", "Num Studies"};
        for (int i = 0; i < titles.length; i++) {
            TableColumn column = new TableColumn(table, SWT.NONE, i);
            column.setText (titles [i]);
        }
        table.setLinesVisible(true);
        table.setHeaderVisible(true);
        
        Collection<Clinic> studies = site.getClinicCollection();
        for (Object obj : studies.toArray(new Object[studies.size()])) {
            Clinic clinic = (Clinic) obj;
            TableItem item = new TableItem(table, 0);
            item.setText(0, clinic.getName());
        }
        
        for (int i = 0; i < titles.length; i++) {
            table.getColumn(i).pack();
        }
    }
	
	private void createButtons() {      
        Composite sbody;
        
		sbody = toolkit.createComposite(form.getBody());
		sbody.setLayout(new GridLayout(4, false));
		toolkit.paintBordersFor(sbody);

		final Button edit = toolkit.createButton(sbody, "Edit Site Info", SWT.PUSH);
		edit.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				getSite().getPage().closeEditor(SiteViewForm.this, false);
				try {
					getSite().getPage().openEditor(new NodeInput(siteAdapter), 
							SiteEntryForm.ID, true);
				}
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button study = toolkit.createButton(sbody, "Add Study", SWT.PUSH);
		study.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {				
				try {
					Study study = new Study();
					study.setSite(site);
                    study.setNameShort("New Study");
					Node studiesNode = siteAdapter.getStudiesGroupNode();
					StudyAdapter studyAdapter = new StudyAdapter(studiesNode, study);
					studiesNode.addChild(studyAdapter);
                    study.setNameShort("");
					getSite().getPage().openEditor(new NodeInput(studyAdapter), 
							StudyEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button clinic = toolkit.createButton(sbody, "Add Clinic", SWT.PUSH);
		clinic.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				try {
					Clinic clinic = new Clinic();
					clinic.setName("New Clinic");
					clinic.setAddress(new Address());
					Node clinicsNode = siteAdapter.getClinicGroupNode();
					ClinicAdapter clinicAdapter = new ClinicAdapter(clinicsNode, clinic);
					clinicsNode.addChild(clinicAdapter);
                    clinic.setName("");
					getSite().getPage().openEditor(new NodeInput(clinicAdapter), 
							ClinicEntryForm.ID, true);
				} 
				catch (PartInitException exp) {
					exp.printStackTrace();				
				}
			}
		});

		final Button storageType = toolkit.createButton(sbody, "Add Storage Type", SWT.PUSH);
		storageType.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
			}
		});
	}
    
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();    	
    	super.bindValues(dbc);
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}

}

class StudyContentProvider implements IStructuredContentProvider {   
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return (Study[])inputElement;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        
    }

}

class StudyLabelProvider extends LabelProvider implements ITableLabelProvider {

    @Override
    public Image getColumnImage(Object element, int columnIndex) {
        return null;
    }

    @Override
    public String getColumnText(Object element, int columnIndex) {
        final Study study = (Study) element;
        switch (columnIndex) {
            case 0: return study.getName();
            case 1: return study.getNameShort();
        }
        return "";
    }
    
    public boolean isLabelProperty(Object element, String property) {
        return false;
    }
}

class ClinicContentProvider implements IStructuredContentProvider {   
    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
     */
    public Object[] getElements(Object inputElement) {
        return (Clinic[])inputElement;
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#dispose()
     */
    public void dispose() {
        
    }

    /* (non-Javadoc)
     * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
     */
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        
    }

}

