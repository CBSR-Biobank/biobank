package edu.ualberta.med.biobank.forms;

import java.util.Collection;
import java.util.Iterator;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;
import edu.ualberta.med.biobank.widgets.BiobankCollectionTable;

public class FormUtils {
    
    public static Label createLabelledField(FormToolkit toolkit, Composite parent, 
            String label) {
        toolkit.createLabel(parent, label, SWT.LEFT);
        Label field = toolkit.createLabel(parent, "", SWT.SINGLE);
        field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return field;
    }
	
	public static Text createLabelledText(FormToolkit toolkit, Composite parent, 
			String labelTxt, int limit, String tip) {
		toolkit.createLabel(parent, labelTxt, SWT.LEFT);
        Text text  = toolkit.createText(parent, "", SWT.SINGLE);
        if (limit > 0) {
            text.setTextLimit(limit);
        }
        if (tip != null) {
            text.setToolTipText(tip);
        }
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return text;
    }
    
    public static ControlDecoration createDecorator(Label label, String message) {
		ControlDecoration controlDecoration = new ControlDecoration(label,
				SWT.RIGHT | SWT.TOP);
		controlDecoration.setDescriptionText(message);
		FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
				.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
		controlDecoration.setImage(fieldDecoration.getImage());
		return controlDecoration;
	}
    
    public static Font getSectionFont() {
    	return new Font (null, "sans-serif", 9, SWT.BOLD);
    }
    
    public static Font getHeadingFont() {
    	return new Font (null, "sans-serif", 8, SWT.BOLD);
    }

    
    public static void createClinicSection(FormToolkit toolkit, Composite parent,
            Collection<Clinic> clinics) {        
        Section section = toolkit.createSection(parent, 
            Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Clinics");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));      
        
        // hack required here because site.getStudyCollection().toArray(new Study[0])
        // returns Object[].        
        int count = 0;
        Clinic [] arr = new Clinic [clinics.size()];
        Iterator<Clinic> it = clinics.iterator();
        while (it.hasNext()) {
            arr[count] = it.next();
            ++count;
        }      
        
        String[] headings = {"Name", "Num Studies"};        
        BiobankCollectionTable comp = 
            new BiobankCollectionTable(section, SWT.NONE, headings, arr);
        section.setClient(comp);
        comp.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(comp);
        comp.getTableViewer().addDoubleClickListener(
                getBiobankCollectionDoubleClickListener());
    }
    
    public static IDoubleClickListener getBiobankCollectionDoubleClickListener() {
        return new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                Object element = ((StructuredSelection)selection).getFirstElement();
                
                if (element instanceof Study) {
                    StudyAdapter node = new StudyAdapter(null, (Study) element);
                    SessionManager.getInstance().openStudyViewForm(node);
                }
                else if (element instanceof Clinic) {
                    ClinicAdapter node = new ClinicAdapter(null, (Clinic) element);
                    SessionManager.getInstance().openClinicViewForm(node);
                }
                else {
                    Assert.isTrue(false, "invalid type for element: " 
                            + element.getClass().getName());
                }
            }
        };
    }
}
