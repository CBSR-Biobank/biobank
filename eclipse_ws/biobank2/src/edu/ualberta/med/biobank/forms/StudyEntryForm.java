package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.core.databinding.AggregateValidationStatus;
import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.databinding.observable.ChangeEvent;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.value.IObservableValue;
import org.eclipse.core.databinding.observable.value.WritableValue;
import org.eclipse.core.databinding.validation.IValidator;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.databinding.swt.SWTObservables;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.helpers.StudyInformationHelper;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Sdata;
import edu.ualberta.med.biobank.model.SdataType;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.Worksheet;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SessionAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import edu.ualberta.med.biobank.widgets.MultiSelect;
import edu.ualberta.med.biobank.widgets.SdataWidget;

@SuppressWarnings("serial")
public class StudyEntryForm extends EditorPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.StudyEntryForm";
	
	private static final String NEW_STUDY_OK_MESSAGE 
		= "Creating a new study.";
	private static final String STUDY_OK_MESSAGE = "Editing an existing study.";

	public static final String[] ORDERED_FIELDS = new String[] {
		"name",
		"nameShort",
	};
	
	public static final HashMap<String, FieldInfo> FIELDS = 
		new HashMap<String, FieldInfo>() {{
			put("name", new FieldInfo("Name", Text.class,  
					NonEmptyString.class, "Study name cannot be blank"));
			put("nameShort", new FieldInfo("Short Name", Text.class,  
					NonEmptyString.class, "Study short name cannot be blank"));
		}
	};
	
	private HashMap<String, Control> controls;
		
	private HashMap<String, ControlDecoration> fieldDecorators;
		
	private boolean dirty = false;

	private FormToolkit toolkit;
	
	private ScrolledForm form;
	
	private MultiSelect clinicsMultiSelect;
	
	private MultiSelect sdataMultiSelect;
	
	private StudyAdapter studyAdapter;
	
	private Study study;
	
	private List<Clinic> allClinics;
	
	private List<SdataType> allSdataTypes;
	
	private Button submit;
        
    private HashMap<String, SdataWidget> sdataWidgets;
	
	private KeyListener keyListener = new KeyListener() {
		@Override
		public void keyPressed(KeyEvent e) {
			if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
				setDirty(true);
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {			
		}
	};
	
	public StudyEntryForm() {
		super();
		controls = new HashMap<String, Control>();
		fieldDecorators = new HashMap<String, ControlDecoration>();
		sdataWidgets = new HashMap<String, SdataWidget>();
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		setDirty(false);
		saveSettings();
	}

	@Override
	public void doSaveAs() {		
	}

	@Override
	public void dispose() {	
		if (!dirty && (study.getId() == null)) {
			// remove temporary node
			Node groupNode = studyAdapter.getParent();
			groupNode.removeChild(studyAdapter);
		}
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input");
		
		setSite(site);
		setInput(input);
		setDirty(false);
		
		Node node = ((NodeInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");
		
		Assert.isTrue((node instanceof StudyAdapter), 
				"Invalid editor input: object of type "
				+ node.getClass().getName());
		
		studyAdapter = (StudyAdapter) node;
		study = studyAdapter.getStudy();
		study.setWorksheet(new Worksheet());
		
		if (study.getId() == null) {
			setPartName("New Study");
		}
		else {
			setPartName("Study" + study.getName());
		}
	}

	@Override
	public boolean isDirty() {
		return dirty;
	}

	private void setDirty(boolean d) {
		dirty = d;
		firePropertyChange(ISaveablePart.PROP_DIRTY);
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);	
		
		form.setText("Study Information");
		//toolkit.decorateFormHeading(form);
		form.setMessage(getOkMessage(), IMessageProvider.NONE);
		
		GridLayout layout = new GridLayout(1, false);
		form.getBody().setLayout(layout);
		
		Composite sbody = toolkit.createComposite(form.getBody());
		layout = new GridLayout(2, false);
		layout.horizontalSpacing = 10;
		sbody.setLayout(layout);
		sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		toolkit.paintBordersFor(sbody);
		
		for (String key : ORDERED_FIELDS) {
			FieldInfo fi = FIELDS.get(key);
			
			if (fi.widgetClass == Text.class) {
				Text text = FormUtils.createLabelledText(toolkit, 
						sbody, fi.label + " :", 100, null);
				controls.put(key, text);
				text.addKeyListener(keyListener);
				
				if (fi.validatorClass != null) {
					fieldDecorators.put(key, 
							FormUtils.createDecorator(text, fi.errMsg));
				}
			}
			else if (fi.widgetClass == Combo.class) {
				toolkit.createLabel(sbody, fi.label + " :", SWT.LEFT);
				Combo combo = new Combo(sbody, SWT.READ_ONLY);
				if (key.equals("province")) {
					combo.setItems(AddressFieldsConstants.PROVINCES);
				}
				combo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
				toolkit.adapt(combo, true, true);
				controls.put(key, combo);
				
				combo.addSelectionListener(new SelectionAdapter() {
					public void widgetSelected(SelectionEvent e) {
						setDirty(true);
					}
				});
			}
			else {
				assert false : fi.widgetClass;
			}
		}

		Section section = toolkit.createSection(form.getBody(), 
		        Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
		section.setText("Clinic Selection");
		//section.setFont(FormUtils.getSectionFont());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		clinicsMultiSelect = new MultiSelect(section, SWT.NONE, 
				"Selected Clinics", "Available Clinics", 100);
		section.setClient(clinicsMultiSelect);
		clinicsMultiSelect.adaptToToolkit(toolkit);
		
		StudyInformationHelper helper = new StudyInformationHelper(
				studyAdapter.getAppService(), study);
		
		BusyIndicator.showWhile(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getShell().getDisplay(), helper);
		
		allClinics = helper.getAllClinics();
        allSdataTypes = helper.getSdataTypes();

		HashMap<Integer, String> availClinics = new HashMap<Integer, String>();
		for (Clinic clinic : allClinics) {
			availClinics.put(clinic.getId(), clinic.getName());
		}
		clinicsMultiSelect.addAvailable(availClinics);

        section = toolkit.createSection(form.getBody(), 
                Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Study Information Selection");
        //section.setFont(FormUtils.getSectionFont());
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

        sbody = toolkit.createComposite(section);
        section.setClient(sbody);        
        layout = new GridLayout(1, false);
        layout.verticalSpacing = 10;
        sbody.setLayout(layout);
        sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(sbody);
        
        for (SdataType sdataType : allSdataTypes) {
            SdataWidget w = new SdataWidget(sbody, SWT.NONE, sdataType); 
            w.adaptToToolkit(toolkit);
            sdataWidgets.put(sdataType.getType(), w);
        }		


        sbody = toolkit.createComposite(form.getBody());
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        sbody.setLayout(layout);
        sbody.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(sbody);

        submit = toolkit.createButton(sbody, "Submit", SWT.PUSH);
        submit.addSelectionListener(new SelectionAdapter() {
            public void widgetSelected(SelectionEvent e) {
                saveSettings();
            }
        });
        
        bindValues();
	}
	
    private void bindValues() {
    	DataBindingContext dbc = new DataBindingContext();
		for (String key : FIELDS.keySet()) {
			FieldInfo fi = FIELDS.get(key);
			UpdateValueStrategy uvs = null;

			if (fi.widgetClass == Text.class) {				
				if (fi.validatorClass != null) {
					try {
						Class<?>[] types = new Class[] { String.class, ControlDecoration.class };				
						Constructor<?> cons = fi.validatorClass.getConstructor(types);
						Object[] args = new Object[] { fi.errMsg, fieldDecorators.get(key) };
						uvs = new UpdateValueStrategy();
						uvs.setAfterConvertValidator((IValidator) cons.newInstance(args));
					} 
					catch (NoSuchMethodException e) {
                        throw new RuntimeException(e);
                    }
                    catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    } 
                    catch (IllegalArgumentException e) {
                        throw new RuntimeException(e);
                    } 
                    catch (InstantiationException e) {
                        throw new RuntimeException(e);
                    } 
                    catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
				}
				
				if (key.equals("worksheet")) {			
					dbc.bindValue(SWTObservables.observeText(controls.get(key), 
							SWT.Modify),
							PojoObservables.observeValue(study.getWorksheet(), "name"), 
							uvs, null);
				}
				else {				
					dbc.bindValue(SWTObservables.observeText(controls.get(key), 
							SWT.Modify),
							PojoObservables.observeValue(study, key), uvs, null);
				}
			}
			else if (fi.widgetClass == Combo.class) {
		    	dbc.bindValue(SWTObservables.observeSelection(controls.get(key)),
		    			PojoObservables.observeValue(study, "province"), null, null);
			}
			else {
				Assert.isTrue(false, "Invalid class " + fi.widgetClass.getName());
			}
		}       
		
		IObservableValue statusObservable = new WritableValue();
		statusObservable.addChangeListener(new IChangeListener() {
			public void handleChange(ChangeEvent event) {
				IObservableValue validationStatus 
					= (IObservableValue) event.getSource(); 
				handleStatusChanged((IStatus) validationStatus.getValue());
			}
		}); 
		
		dbc.bindValue(statusObservable, new AggregateValidationStatus(
                dbc.getBindings(), AggregateValidationStatus.MAX_SEVERITY),
                null, null); 
    }
	
	private String getOkMessage() {
		if (study.getId() == null) {
			return NEW_STUDY_OK_MESSAGE;
		}
		return STUDY_OK_MESSAGE;
	}
    
    protected void handleStatusChanged(IStatus status) {
		if (status.getSeverity() == IStatus.OK) {
			form.setMessage(getOkMessage(), IMessageProvider.NONE);
	    	submit.setEnabled(true);
		}
		else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
	    	submit.setEnabled(false);
		}		
    }
    
    private void saveSettings() {
    	// get the selected clinics from widget
    	List<Integer> selClinicIds = clinicsMultiSelect.getSelected();
    	List<Clinic> selClinics = new ArrayList<Clinic>();
    	for (Clinic clinic : allClinics) {
    		if (selClinicIds.indexOf(clinic.getId()) >= 0) {
    			selClinics.add(clinic);
    		}
    		
    	}
    	Assert.isTrue(selClinics.size() == selClinicIds.size(), 
    			"problem with clinic selections");
		study.setClinicCollection(selClinics);
    	
    	for (String key : sdataWidgets.keySet()) {
    	    SdataWidget w = sdataWidgets.get(key);
    	    System.out.println("sdataWidget " + key + ": " + w.getResult());
    	}
    	
		getSite().getPage().closeEditor(StudyEntryForm.this, false);    	
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}
	
	public Study getStudy() {
		return study;
	}
	
	public SessionAdapter getSessionAdapter() {
		SessionAdapter sessionAdapter = null;
		
		int sessions = SessionManager.getInstance().getSessionCount();
		if (sessions == 1) {
			sessionAdapter = SessionManager.getInstance().getSessionAdapter(0);
		}
		else {
			Assert.isTrue(false, "not implemented yet");
		}
		return sessionAdapter;
	}
}
