package edu.ualberta.med.biobank.forms;

import java.lang.reflect.Constructor;
import java.util.HashMap;

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
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

@SuppressWarnings("serial")
public class StudyEntryForm extends EditorPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.StudyViewForm";
	
	private static final String NEW_STUDY_OK_MESSAGE 
		= "Create a new study.";
	private static final String STUDY_OK_MESSAGE = "Edit a study.";

	public static final String[]  ORDERED_FIELDS = new String[] {
		"name",
		"shortName"
	};
	
	public static final HashMap<String, FieldInfo> FIELDS = 
		new HashMap<String, FieldInfo>() {{
			put("name", new FieldInfo("Name", Text.class,  null,  null));
			put("shortName", new FieldInfo("Short Name", Text.class,  null,  null));
		}
	};
	
	private HashMap<String, Control> controls;
		
	private HashMap<String, ControlDecoration> fieldDecorators;
		
	private boolean dirty = false;

	private FormToolkit toolkit;
	
	private Form form;
	
	private Node node;
	
	private Study study;
	
	private Button submit;
	
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
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		if ( !(input instanceof NodeInput)) 
			throw new PartInitException("Invalid editor input");
		
		setSite(site);
		setInput(input);
		setDirty(false);
		
		node = ((NodeInput) input).getNode();
		Assert.isNotNull(node, "Null editor input");
		
		Assert.isTrue((node instanceof SiteAdapter), 
				"Invalid editor input: object of type "
				+ node.getClass().getName());
		
		StudyAdapter studyNode = (StudyAdapter) node;
		study = studyNode.getStudy();
		
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
		form = toolkit.createForm(parent);	
		
		form.setText("Study Information");
		toolkit.decorateFormHeading(form);
		form.setMessage(getOkMessage());
		
		GridLayout layout = new GridLayout(2, false);
		form.getBody().setLayout(layout);
		
		Section section = toolkit.createSection(form.getBody(), Section.TITLE_BAR);
		section.setText("Address");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		Composite sbody = toolkit.createComposite(section);
		section.setClient(sbody);
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		sbody.setLayout(layout);
		toolkit.paintBordersFor(sbody);
		
		for (String key : AddressFieldsConstants.ORDERED_FIELDS) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);
			
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

		sbody = toolkit.createComposite(form.getBody());
		layout = new GridLayout();
		layout.horizontalSpacing = 10;
		layout.numColumns = 2;
		sbody.setLayout(layout);
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
		for (String key : AddressFieldsConstants.FIELDS.keySet()) {
			FieldInfo fi = AddressFieldsConstants.FIELDS.get(key);
			UpdateValueStrategy uvs = null;

			if (fi.widgetClass == Text.class) {				
				if (fi.validatorClass != null) {
					try {
						Class<?>[] types = new Class[] { String.class, ControlDecoration.class };				
						Constructor<?> cons = fi.validatorClass.getConstructor(types);
						Object[] args = new Object[] { fi.errMsg, fieldDecorators.get(key) };
						uvs = new UpdateValueStrategy();
						uvs.setAfterConvertValidator((IValidator) cons.newInstance(args));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				
				dbc.bindValue(SWTObservables.observeText(controls.get(key), SWT.Modify),
						PojoObservables.observeValue(study, key), uvs, null);
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
			form.setMessage(getOkMessage());
	    	submit.setEnabled(true);
		}
		else {
			form.setMessage(status.getMessage(), IMessageProvider.ERROR);
	    	submit.setEnabled(false);
		}		
    }
    
    private void saveSettings() {
		String sessionName;
		
		if (node instanceof StudyAdapter) {
			sessionName = node.getParent().getParent().getName();
		}
		else {
			sessionName = node.getParent().getName();
		}
		
		try {
			if (study.getId() == null) {
				BioBankPlugin.getDefault().createObject(sessionName, study);
			}
			else {
				BioBankPlugin.getDefault().updateObject(sessionName, study);
			}
		}
		catch (Exception exp) {
			exp.printStackTrace();
		}
		getSite().getPage().closeEditor(StudyEntryForm.this, false);    	
    }

	@Override
	public void setFocus() {
		form.setFocus();
	}

}
