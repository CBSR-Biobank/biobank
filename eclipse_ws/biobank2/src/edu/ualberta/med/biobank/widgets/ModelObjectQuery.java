package edu.ualberta.med.biobank.widgets;

import java.lang.reflect.Field;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.views.ReportsView;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ModelObjectQuery {

    private Button radio;
    private Label with;
    private Composite attributeQueries;
    private AttributeQueryClause clause;

    public ModelObjectQuery(Composite parent, Field modelObjectField,
        ReportsView view) {

        radio = new Button(parent, SWT.CHECK);
        radio.setSelection(false);

        with = new Label(parent, SWT.NONE);
        with.setText("With: " + AttributeQueryClause.getText(modelObjectField));

        attributeQueries = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.verticalSpacing = 10;
        attributeQueries.setLayout(layout);

        clause = new AttributeQueryClause(attributeQueries, modelObjectField,
            modelObjectField.getName(), view);

        radio.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

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
        else
            return clause.getClause();
    }

    public Boolean getEnabled() {
        return with.getEnabled();
    }

    public void setEnabled(Boolean enable) {
        with.setEnabled(enable);
        clause.setEnabled(enable);
    }
}
