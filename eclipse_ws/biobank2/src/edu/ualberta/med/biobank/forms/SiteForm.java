package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISaveablePart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;

import edu.ualberta.med.biobank.model.SiteInput;
import edu.ualberta.med.biobank.model.SiteNode;

public class SiteForm extends EditorPart {
	public static final String ID =
	      "edu.ualberta.med.biobank.forms.SiteForm";

	private SiteNode siteNode;
	
	private boolean dirty = false;

	private FormToolkit toolkit;
	
	private ScrolledForm form;
	
	protected Combo session;
	private Text name;

	@Override
	public void doSave(IProgressMonitor monitor) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		setDirty(false);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);	
		SiteInput sinput = (SiteInput) input;	
		siteNode = (SiteNode) sinput.getAdapter(SiteNode.class);
		setPartName("Site " + sinput.getName());
		setDirty(false);
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
		KeyListener keyListener = new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				if ((e.keyCode & SWT.MODIFIER_MASK) == 0) {
					setDirty(true);
				}
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				// nothing
			}
		};
		
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);	
		
		form.setText("BioBank Site Information");
		
		Composite contents = form.getBody();
		
		GridLayout layout = new GridLayout(2, false);
		contents.setLayout(layout);
		contents.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
				
		name = createLabelledText(contents, "Name:", 0, null);
		name.setText(siteNode.getSite().getName());
		name.addKeyListener(keyListener);
	}
	
	protected Text createLabelledText(Composite parent, String label, int limit, String tip) {
		toolkit.createLabel(parent, label, SWT.LEFT);
        Text text  = toolkit.createText(parent, siteNode.getSite().getName());
        if (limit > 0) {
            text.setTextLimit(limit);
        }
        if (tip != null) {
            text.setToolTipText(tip);
        }
        text.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
        return text;
    }

	@Override
	public void setFocus() {
		form.setFocus();		
	}

}
