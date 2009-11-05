package edu.ualberta.med.biobank.forms;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

import edu.ualberta.med.biobank.model.ClinicStudyInfo;
import edu.ualberta.med.biobank.model.StudyContactAndPatientInfo;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.BiobankCollectionModel;

/**
 * Static methods for constructing the forms that allow the user to edit / view
 * the information stored in the ORM model objects.
 */
public class FormUtils {

    public static Label createLabelledField(FormToolkit toolkit,
        Composite parent, String label) {
        toolkit.createLabel(parent, label, SWT.LEFT);
        Label field = toolkit.createLabel(parent, "", SWT.SINGLE);
        field.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return field;
    }

    public static Text createLabelledText(FormToolkit toolkit,
        Composite parent, String labelTxt, int limit, String tip) {
        toolkit.createLabel(parent, labelTxt, SWT.LEFT);
        Text text = toolkit.createText(parent, "", SWT.SINGLE);
        if (limit > 0) {
            text.setTextLimit(limit);
        }
        if (tip != null) {
            text.setToolTipText(tip);
        }
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return text;
    }

    public static ControlDecoration createDecorator(Control control,
        String message) {
        ControlDecoration controlDecoration = new ControlDecoration(control,
            SWT.RIGHT | SWT.TOP);
        controlDecoration.setDescriptionText(message);
        FieldDecoration fieldDecoration = FieldDecorationRegistry.getDefault()
            .getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
        controlDecoration.setImage(fieldDecoration.getImage());
        return controlDecoration;
    }

    public static Font getHeadingFont() {
        return new Font(null, "sans-serif", 8, SWT.BOLD);
    }

    /**
     * Double click listener for tables used in view forms.
     * 
     */
    public static IDoubleClickListener getBiobankCollectionDoubleClickListener() {
        return new IDoubleClickListener() {
            public void doubleClick(DoubleClickEvent event) {
                Object selection = event.getSelection();
                Object element = ((StructuredSelection) selection)
                    .getFirstElement();
                if (element instanceof AdapterBase) {
                    ((AdapterBase) element).performDoubleClick();
                } else if (element instanceof BiobankCollectionModel) {
                    BiobankCollectionModel item = (BiobankCollectionModel) element;
                    if (item.o != null) {
                        if (item.o instanceof AdapterBase) {
                            ((AdapterBase) item.o).performDoubleClick();
                        } else if (item.o instanceof ClinicStudyInfo) {
                            ((ClinicStudyInfo) item.o).performDoubleClick();
                        } else if (item.o instanceof StudyContactAndPatientInfo) {
                            ((StudyContactAndPatientInfo) item.o)
                                .performDoubleClick();
                        }
                    }
                }
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

    public static void setCheckBoxValue(Button button, Boolean value) {
        if (value != null) {
            button.setSelection(value.booleanValue());
        }
    }
}
