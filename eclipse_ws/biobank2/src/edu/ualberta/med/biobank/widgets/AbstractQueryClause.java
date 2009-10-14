package edu.ualberta.med.biobank.widgets;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.views.AdvancedReportsView;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public abstract class AbstractQueryClause {
    
  private static Logger LOGGER = Logger.getLogger(AbstractQueryClause.class
        .getName());

  protected AdvancedReportsView view;
    protected String alias;
    protected List<Method> attributes;

    protected List<ComboViewer> whereCombos;
    protected List<ComboViewer> operatorCombos;
    protected List<Text> searchFields;

    protected List<String> stringOps;
    protected List<String> numberOps;
    protected List<String> collectionOps;

    protected AbstractQueryClause(List<Method> methods, String alias,
        AdvancedReportsView view) {

        this.alias = alias;
        this.view = view;

        whereCombos = new ArrayList<ComboViewer>();
        operatorCombos = new ArrayList<ComboViewer>();
        searchFields = new ArrayList<Text>();

        initAttributes(methods);

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

    private Method getAttribute(ComboViewer whereCombo) {
        IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
            .getSelection();
        return (Method) whereSelection.getFirstElement();
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
            Method attribute = getAttribute(whereCombo);
            ComboViewer operatorCombo = operatorCombos.get(i);
            String operator = getOperator(operatorCombo);
            Text searchField = searchFields.get(i);
            String value = getValue(searchField);
            if (value.compareTo("") == 0 || value == null)
                continue;
            String attributeName = alias + "."
                + AbstractQueryClause.getText(attribute);

            // convert value if necessary to correct type
            if (attribute.getReturnType().equals(Integer.class)
                || attribute.getReturnType().equals(Double.class))
                params.add(Integer.valueOf(value));
            else
                params.add(value);

            // query syntax modifications

            // modify query to use "like" if user wants contains
            if (operator.compareTo("contains") == 0) {
                query += attributeName + " like ? and ";
                params.set(params.size() - 1, "%"
                    + params.get(params.size() - 1) + "%");
            } else
                query += attributeName + " " + operator + " ? and ";
        }
        if (query.compareTo("") == 0)
            return null;
        else
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

    protected void initAttributes(List<Method> methods) {
        List<Method> attributes = new ArrayList<Method>();
        for (Method method : methods) {
            if (method.getReturnType().equals(String.class)
                || method.getReturnType().equals(Integer.class)
                || method.getReturnType().equals(Double.class))

                attributes.add(method);

        }
        this.attributes = attributes;
    }

    protected void updateOperatorCombo(int index) {
        try {
            ComboViewer whereCombo = whereCombos.get(index);
            ComboViewer opCombo = operatorCombos.get(index);
            IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
                .getSelection();
            Method whereMethod = (Method) whereSelection.getFirstElement();
            Class<?> type = whereMethod.getReturnType();
            if (type.equals(String.class))
                opCombo.setInput(stringOps);
            else if (type.equals(Integer.class) || type.equals(Double.class))
                opCombo.setInput(numberOps);
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
        if (s.length > 0) {
            String filtered = s[s.length - 1].replace("Wrapper", "").replace(
                "get", "").replace("()", "");
            String lowercase = filtered.substring(0, 1).toLowerCase();
            lowercase += filtered.substring(1);
            return lowercase;
        } else
            return null;
    }
}
