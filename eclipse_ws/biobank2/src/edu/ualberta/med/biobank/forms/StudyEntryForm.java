package edu.ualberta.med.biobank.forms;

import java.util.HashMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
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

import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

@SuppressWarnings("serial")
public class StudyEntryForm extends EditorPart {
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
		//layout.marginHeight = 10;
		//layout.marginWidth = 6;
		//layout.horizontalSpacing = 20;
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
		
	}
	
	private String getOkMessage() {
		if (study.getId() == null) {
			return NEW_STUDY_OK_MESSAGE;
		}
		return STUDY_OK_MESSAGE;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
