package edu.ualberta.med.biobank.widgets;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.views.ReportsView;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryPage extends Composite {

    private Composite barsAndButton;
    private Composite whereBars;
    private Composite parent;
    private Composite subSection;
    private ReportsView view;

    private List<Class<?>> searchableModelObjects;
    private List<Field> modelObjectFields;
    private ComboViewer selectedTypeCombo;
    private AttributeQueryClause attributeClause;
    private List<ModelObjectQuery> modelObjectClauses;

    public QueryPage(ReportsView view, Composite parent, int style) {
        super(parent, style);
        this.view = view;
        this.parent = parent;
        GridLayout queryLayout = new GridLayout(3, false);
        queryLayout.verticalSpacing = 20;
        setLayout(queryLayout);

        searchableModelObjects = new ArrayList<Class<?>>();
        modelObjectFields = new ArrayList<Field>();

        createSearchablesList();

        Label type = new Label(this, SWT.NONE);
        // what type of object are you searching for?
        type.setText("Select a type to search:");

        selectedTypeCombo = AbstractQueryClause.createCombo(this,
            searchableModelObjects);
        selectedTypeCombo
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    typeChanged();
                }
            });

        selectedTypeCombo.setSelection(new StructuredSelection(
            searchableModelObjects.get(0)));
    }

    public void typeChanged() {
        if (barsAndButton != null)
            barsAndButton.dispose();

        barsAndButton = new Composite(this, SWT.NONE);
        GridLayout bnbLayout = new GridLayout(2, false);
        bnbLayout.verticalSpacing = 10;
        barsAndButton.setLayout(bnbLayout);

        if (whereBars != null)
            whereBars.dispose();

        whereBars = new Composite(barsAndButton, SWT.NONE);
        GridLayout whereLayout = new GridLayout(3, false);
        whereLayout.verticalSpacing = 10;
        whereBars.setLayout(whereLayout);

        IStructuredSelection typeSelection = (IStructuredSelection) selectedTypeCombo
            .getSelection();
        Class<?> type = (Class<?>) typeSelection.getFirstElement();
        Field[] classFields = type.getDeclaredFields();

        attributeClause = new AttributeQueryClause(whereBars, type, null, view);

        modelObjectClauses = new ArrayList<ModelObjectQuery>();

        if (subSection != null)
            subSection.dispose();

        subSection = new Composite(this, SWT.NONE);
        GridLayout subSectionLayout = new GridLayout(3, false);
        subSectionLayout.verticalSpacing = 20;
        subSection.setLayout(subSectionLayout);
        GridData colSpanInfo = new GridData();
        colSpanInfo.horizontalSpan = 3;
        subSection.setLayoutData(colSpanInfo);

        // update sub-objects
        modelObjectFields.clear();
        initModelObjectFields(classFields);
        for (Field field : modelObjectFields)
            modelObjectClauses
                .add(new ModelObjectQuery(subSection, field, view));

        QueryPage.this.parent.layout(true, true);

    }

    private void initModelObjectFields(Field[] classFields) {
        for (Field field : classFields) {
            if (!field.getType().equals(String.class)
                && !field.getType().equals(Integer.class)
                && !field.getType().equals(Double.class)
                && !field.getType().equals(Collection.class)) {
                if (!field.getType().isPrimitive())
                    modelObjectFields.add(field);
            }
        }
    }

    private void createSearchablesList() {
        searchableModelObjects.add(Container.class);
        searchableModelObjects.add(ContainerType.class);
        searchableModelObjects.add(Site.class);
        searchableModelObjects.add(Patient.class);
        searchableModelObjects.add(Study.class);
        searchableModelObjects.add(Sample.class);
        searchableModelObjects.add(Clinic.class);
    }

    public HQLCriteria getQuery() {

        IStructuredSelection typeSelection = (IStructuredSelection) selectedTypeCombo
            .getSelection();
        Class<?> type = (Class<?>) typeSelection.getFirstElement();

        String query = "from " + type.getName() + " where ";

        List<Object> params = new ArrayList<Object>();

        HQLCriteria attributeCriteria = attributeClause.getClause();
        query += attributeCriteria.getHqlString();
        params.addAll(attributeCriteria.getParameters());

        for (int i = 0; i < modelObjectClauses.size(); i++) {
            if (modelObjectClauses.get(i).getEnabled()) {
                HQLCriteria modelObjectCriteria = modelObjectClauses.get(i)
                    .getClause();
                query += modelObjectCriteria.getHqlString();
                params.addAll(modelObjectCriteria.getParameters());
            }
        }

        query = query.substring(0, query.length() - 4);
        HQLCriteria c = new HQLCriteria(query, params);
        return c;
    }

}
