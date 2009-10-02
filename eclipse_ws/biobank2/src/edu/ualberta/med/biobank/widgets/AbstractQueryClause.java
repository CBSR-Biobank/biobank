package edu.ualberta.med.biobank.widgets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.views.ReportsView;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class AbstractQueryClause {
    protected ReportsView view;
    protected String name;
    protected List<Field> attributes;
    protected Class<?> modelObjectClass;

    protected List<ComboViewer> whereCombos;
    protected List<ComboViewer> operatorCombos;
    protected List<Text> searchFields;

    protected List<String> stringOps;
    protected List<String> numberOps;
    protected List<String> collectionOps;

    protected AbstractQueryClause(Class<?> modelObjectClass, String name,
        ReportsView view) {
        this.modelObjectClass = modelObjectClass;
        if (name == null)
            this.name = "";
        else
            this.name = name + ".";
        this.view = view;

        whereCombos = new ArrayList<ComboViewer>();
        operatorCombos = new ArrayList<ComboViewer>();
        searchFields = new ArrayList<Text>();

        initAttributes();

        initStringOps();
        initNumberOps();
        initCollectionOps();
    }

    protected void initStringOps() {
        // operators
        stringOps = new ArrayList<String>();
        stringOps.add("=");
        stringOps.add("contains");
    }

    protected void initNumberOps() {
        // operators
        numberOps = new ArrayList<String>();
        numberOps.add("=");
        numberOps.add("<=");
        numberOps.add(">=");
        numberOps.add("<");
        numberOps.add(">");
    }

    protected void initCollectionOps() {
        // operators
        collectionOps = new ArrayList<String>();
        collectionOps.add("=");
        collectionOps.add("<=");
        collectionOps.add(">=");
        collectionOps.add("<");
        collectionOps.add(">");
    }

    private String getOperator(ComboViewer operatorCombo) {
        IStructuredSelection operatorSelection = (IStructuredSelection) operatorCombo
            .getSelection();
        return (String) operatorSelection.getFirstElement();
    }

    private Field getAttribute(ComboViewer whereCombo) {
        IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
            .getSelection();
        return (Field) whereSelection.getFirstElement();
    }

    private String getValue(Text searchField) {
        return searchField.getText();

    }

    protected HQLCriteria getClause() {
        if (!getEnabled())
            return null;

        String query = "";
        List<Object> params = new ArrayList<Object>();
        for (int i = 0; i < whereCombos.size(); i++) {
            ComboViewer whereCombo = whereCombos.get(i);
            Field attribute = getAttribute(whereCombo);
            ComboViewer operatorCombo = operatorCombos.get(i);
            String operator = getOperator(operatorCombo);
            Text searchField = searchFields.get(i);
            String value = getValue(searchField);
            if (value.compareTo("") == 0 || value == null)
                continue;
            String attributeName = name + attribute.getName();

            // convert value if necessary to correct type
            if (attribute.getType().equals(Integer.class)
                || attribute.getType().equals(Double.class)
                || attribute.getType().equals(Collection.class))
                params.add(Integer.valueOf(value));
            else
                params.add(value);

            // query syntax modifications
            if (attribute.getType().equals(Collection.class))
                query += "size(" + attributeName + ") " + operator + " ? and ";
            // modify query to use "like" if user wants contains
            else if (operator.compareTo("contains") == 0) {
                query += attributeName + " like ? and ";
                params.set(params.size() - 1, "%"
                    + params.get(params.size() - 1) + "%");
            } else
                query += attributeName + " " + operator + " ? and ";
        }
        return new HQLCriteria(query, params);
    }

    protected static ComboViewer createCombo(Composite parent, List<?> list) {
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
                return AbstractQueryClause.getText(element);
            }
        });
        comboViewer.setInput(list);
        return comboViewer;
    }

    protected void initAttributes() {
        Field[] classFields = modelObjectClass.getDeclaredFields();
        List<Field> attributes = new ArrayList<Field>();
        for (Field field : classFields) {
            if (field.getType().equals(String.class)
                || field.getType().equals(Integer.class)
                || field.getType().equals(Double.class)
                || field.getType().equals(Collection.class)) {
                attributes.add(field);
            }
        }
        this.attributes = attributes;
    }

    protected void updateOperatorCombo(int index) {
        try {
            ComboViewer whereCombo = whereCombos.get(index);
            ComboViewer opCombo = operatorCombos.get(index);
            IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
                .getSelection();
            Field whereField = (Field) whereSelection.getFirstElement();
            Class<?> type = whereField.getType();
            if (type.equals(String.class))
                opCombo.setInput(stringOps);
            else if (type.equals(Integer.class) || type.equals(Double.class))
                opCombo.setInput(numberOps);
            else if (type.equals(Collection.class))
                opCombo.setInput(collectionOps);
            else {
                opCombo.setInput(null);
                throw new Exception(
                    "Field does not have a corresponding operator set.");
            }
            opCombo.getCombo().select(0);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public Boolean getEnabled() {
        if (whereCombos.size() > 0)
            return (whereCombos.get(0).getCombo().getEnabled() & getInternalEnabled());
        else
            return false;
    }

    public void setEnabled(Boolean enable) {
        for (int i = 0; i < whereCombos.size(); i++) {
            whereCombos.get(i).getCombo().setEnabled(enable);
            operatorCombos.get(i).getCombo().setEnabled(enable);
            searchFields.get(i).setEnabled(enable);
        }
        setInternalEnabled(enable);
    }

    protected abstract void setInternalEnabled(Boolean enable);

    protected abstract Boolean getInternalEnabled();

    protected abstract void createFormContent();

    public static String getText(Object element) {
        String[] s = element.toString().split("\\.");
        if (s.length > 0)
            return s[s.length - 1];
        else
            return null;
    }
}
