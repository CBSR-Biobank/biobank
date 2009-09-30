package edu.ualberta.med.biobank.widgets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class QueryBarWidget {

    private List<Field> fields;
    private List<String> stringOps;
    private List<String> numberOps;
    private List<String> collectionOps;

    private ComboViewer whereCombo;
    private ComboViewer operatorCombo;
    private Text searchField;

    private Composite parent;
    private Label where;

    public QueryBarWidget(Composite parent) {

        this.parent = parent;

        fields = new ArrayList<Field>();
        stringOps = new ArrayList<String>();
        numberOps = new ArrayList<String>();
        collectionOps = new ArrayList<String>();

        // operators

        stringOps.add("=");
        stringOps.add("contains");

        numberOps.add("=");
        numberOps.add("<=");
        numberOps.add(">=");
        numberOps.add("<");
        numberOps.add(">");

        collectionOps.add("=");
        collectionOps.add("<=");
        collectionOps.add(">=");
        collectionOps.add("<");
        collectionOps.add(">");

        addNewQueryBar();

    }

    private void addNewQueryBar() {
        // variable
        where = new Label(parent, SWT.None);
        where.setText("Where: ");
        whereCombo = createCombo(parent, null);
        whereCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateOperatorCombo();
            }
        });

        // operator
        operatorCombo = createCombo(parent, null);
        // value
        searchField = new Text(parent, SWT.BORDER);

        GridData gd = new GridData();
        gd.widthHint = 100;
        searchField.setLayoutData(gd);

    }

    private void updateOperatorCombo() {
        try {
            IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
                .getSelection();
            Field whereField = (Field) whereSelection.getFirstElement();
            Class<?> type = whereField.getType();
            if (type.equals(String.class))
                operatorCombo.setInput(stringOps);
            else if (type.equals(Integer.class) || type.equals(Double.class))
                operatorCombo.setInput(numberOps);
            else if (type.equals(Collection.class))
                operatorCombo.setInput(collectionOps);
            else {
                operatorCombo.setInput(null);
                throw new Exception(
                    "Field does not have a corresponding operator set.");
            }
            operatorCombo.getCombo().select(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public void updateWhereCombo(List<Field> fieldList) {
        this.fields = fieldList;
        whereCombo.setInput(fields);
        if (fields.size() > 0)
            whereCombo.setSelection(new StructuredSelection(fields.get(0)));
    }

    public String getOperator() {
        IStructuredSelection operatorSelection = (IStructuredSelection) operatorCombo
            .getSelection();
        return (String) operatorSelection.getFirstElement();
    }

    public Field getWhere() {
        IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
            .getSelection();
        return (Field) whereSelection.getFirstElement();
    }

    public String getValue() {
        return searchField.getText();
    }

    /**
     * This method initializes a combo
     * 
     */
    private ComboViewer createCombo(Composite parent, List<?> list) {
        Combo combo;
        ComboViewer comboViewer;
        combo = new Combo(parent, SWT.READ_ONLY);

        GridData combodata = new GridData();
        combodata.widthHint = 130;
        combo.setLayoutData(combodata);

        comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                String[] s = element.toString().split("\\.");
                if (s.length > 0)
                    return s[s.length - 1];
                else
                    return null;
            }
        });
        comboViewer.setInput(list);
        return comboViewer;
    }

    public boolean getEnabled() {
        return where.getEnabled();
    }

    public void setEnabled(Boolean enable) {
        where.setEnabled(enable);
        whereCombo.getCombo().setEnabled(enable);
        operatorCombo.getCombo().setEnabled(enable);
        searchField.setEnabled(enable);
    }

}
