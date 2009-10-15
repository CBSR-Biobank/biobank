package edu.ualberta.med.biobank.widgets;

import java.lang.reflect.Method;
import java.util.ArrayList;
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
    private List<Method> modelObjectMethods;
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
        modelObjectMethods = new ArrayList<Method>();

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
        // Field[] classFields = type.getDeclaredFields();
        List<Method> methods = filterMethods(type.getDeclaredMethods(), true);

        attributeClause = new AttributeQueryClause(whereBars, type,
            AttributeQueryClause.getText(type.getName()), view);

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

        modelObjectMethods.clear();
        initModelObjectClasses(methods);
        for (Method method : modelObjectMethods)
            modelObjectClauses.add(new ModelObjectQuery(subSection, method,
                AttributeQueryClause.getText(type.getName()), view));

        QueryPage.this.parent.layout(true, true);

    }

    private void initModelObjectClasses(List<Method> methods) {
        for (Method method : methods) {
            if (!method.getReturnType().equals(String.class)
                && !method.getReturnType().equals(Integer.class)
                && !method.getReturnType().equals(Double.class)
                && !method.getReturnType().equals(Boolean.class))
                if (!method.getReturnType().isPrimitive())
                    modelObjectMethods.add(method);
        }
    }

    public static List<Method> filterMethods(Method[] unfiltered,
        boolean includeCollections) {
        List<Method> filtered = new ArrayList<Method>();
        for (int i = 0; i < unfiltered.length; i++)
            if (unfiltered[i].getName().startsWith("get")
                && !unfiltered[i].getName().contains("Proxied")
                && !unfiltered[i].getName().contains("Call")
                && !unfiltered[i].getName().contains("Advisors")
                && !unfiltered[i].getName().contains("Target")
                && (!unfiltered[i].getName().contains("Collection") || includeCollections))
                filtered.add(unfiltered[i]);
        return filtered;
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

    @SuppressWarnings("unchecked")
    public HQLCriteria getQuery() {

        IStructuredSelection typeSelection = (IStructuredSelection) selectedTypeCombo
            .getSelection();
        Class<?> type = (Class<?>) typeSelection.getFirstElement();
        String query = "select " + AttributeQueryClause.getText(type.getName());
        query += " from " + type.getName() + " as "
            + AttributeQueryClause.getText(type.getName());

        List<Object> params = new ArrayList<Object>();

        // compute joins
        String firstPart = "";
        for (int i = 0; i < modelObjectClauses.size(); i++) {
            ModelObjectQuery clause = modelObjectClauses.get(i);
            if (clause.getJoin() != null) {
                firstPart += " left join ";
                firstPart += clause.getJoin() + " ";
            }
        }

        // compute clauses
        String secondPart = "";
        for (int i = 0; i < modelObjectClauses.size(); i++) {
            HQLCriteria modelObjectCriteria = modelObjectClauses.get(i)
                .getClause();
            if (modelObjectCriteria == null)
                continue;
            secondPart += modelObjectCriteria.getHqlString();
            params.addAll(modelObjectCriteria.getParameters());
        }

        HQLCriteria attributeCriteria = attributeClause.getClause();
        if (attributeCriteria != null) {
            secondPart += attributeCriteria.getHqlString();
            params.addAll(attributeCriteria.getParameters());
        }
        if (firstPart.compareTo("") != 0)
            query += firstPart;
        if (secondPart.compareTo("") != 0) {
            query += " where " + secondPart;
            query = query.substring(0, query.length() - 4);
        }

        HQLCriteria c = new HQLCriteria(query, params);
        return c;
    }
}
