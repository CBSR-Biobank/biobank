package edu.ualberta.med.biobank.widgets.queries;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.views.AdvancedReportsView;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelObjectQuery {

    private Button radio;
    private Label with;
    private Composite attributeQueries;
    private AttributeQueryClause clause;
    private Class<?> modelObjectClass;
    private Type modelObjectType;
    private String joins;

    public ModelObjectQuery(Composite parent, Method modelObjectMethod,
        String alias, AdvancedReportsView view) {
        with = new Label(parent, SWT.NONE);
        joins = "";

        // is it a collection?
        modelObjectType = modelObjectMethod.getGenericReturnType();
        if (modelObjectType instanceof ParameterizedType) {
            // what type of collection?
            ParameterizedType type = (ParameterizedType) modelObjectType;
            modelObjectClass = (Class<?>) type.getActualTypeArguments()[0];
            with.setText("With: "
                + AttributeQueryClause.getText(modelObjectMethod.getName()
                    .substring(3)));
        } else {
            // it's not a collection
            modelObjectClass = (Class<?>) modelObjectType;
            with.setText("With: "
                + AttributeQueryClause.getText(modelObjectClass.toString()));
        }
        radio = new Button(parent, SWT.CHECK);
        radio.setSelection(false);

        attributeQueries = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 10;
        attributeQueries.setLayout(layout);

        if (modelObjectType instanceof ParameterizedType) {
            joins = alias
                + "."
                + AttributeQueryClause.getText(modelObjectMethod.getName()
                    .substring(3)) + " as ";
            alias = AttributeQueryClause.getText(modelObjectClass.getName())
                + "Alias";
            joins += alias;
        } else
            alias += "."
                + AttributeQueryClause.getText(modelObjectMethod.getName());

        clause = new AttributeQueryClause(attributeQueries, QueryPage
            .filterMethods(modelObjectClass), alias, view);

        radio.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setEnabled(!getEnabled());
            }
        });

        with.setEnabled(false);
        clause.setEnabled(false);

    }

    public HQLCriteria getClause() {
        if (!getEnabled())
            return null;
        else {
            return clause.getClause();
        }
    }

    public String getJoin() {
        if (!getEnabled() || joins.compareTo("") == 0
            || clause.getClause() == null)
            return null;
        else {
            return joins;
        }
    }

    public Boolean getEnabled() {
        return with.getEnabled();
    }

    public void setEnabled(Boolean enable) {
        with.setEnabled(enable);
        clause.setEnabled(enable);
    }
}
