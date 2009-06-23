package edu.ualberta.med.biobank.forms;

import java.util.Collection;

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
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.Node;
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
    
    public static ControlDecoration createDecorator(Control control, String message) {
		ControlDecoration controlDecoration = new ControlDecoration(control,
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

    
    public static BiobankCollectionTable createClinicSection(FormToolkit toolkit, Composite parent,
            Node clinicGroupParent, Collection<Clinic> clinics) {        
        Section section = toolkit.createSection(parent, 
            Section.TWISTIE | Section.TITLE_BAR | Section.EXPANDED);
        section.setText("Clinics");
        section.setLayout(new GridLayout(1, false));
        section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        
        String[] headings = {"Name", "Num Studies"};        
        BiobankCollectionTable comp = 
            new BiobankCollectionTable(section, SWT.NONE, headings, getClinicsAdapters(clinicGroupParent,
    				clinics));
        section.setClient(comp);
        comp.adaptToToolkit(toolkit);   
        toolkit.paintBordersFor(comp);
        comp.getTableViewer().addDoubleClickListener(
                getBiobankCollectionDoubleClickListener());
        return comp;
    }

	public static ClinicAdapter[] getClinicsAdapters(Node clinicGroupParent,
			Collection<Clinic> clinics) {
		ClinicAdapter [] clinicAdapters = new ClinicAdapter [clinics.size()];   
        
        int count = 0;
        for (Clinic clinic : clinics) {
            clinicAdapters[count] = new ClinicAdapter(clinicGroupParent, clinic);
            ++count;
        }
		return clinicAdapters;
	}
	
    
    /**
     * Double click listener for tables used in view forms.
     * 
     */
    public static IDoubleClickListener getBiobankCollectionDoubleClickListener() {
        return new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                Object element = ((StructuredSelection)selection).getFirstElement();
                ((Node) element).performDoubleClick();
            }
        };
    }
    
    public static void setTextValue(Label label, String value) {
    	if (value != null) {
    		label.setText(value);
    	}
    }
    
    public static void setTextValue(Label label, Object value) {
    	if (value != null) {
    		setTextValue(label, value.toString());
    	}
    }
}
