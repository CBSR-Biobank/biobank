package edu.ualberta.med.biobank.widgets.queries;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.views.AdvancedReportsView;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryPage extends Composite {

    private Composite barsAndButton;
    private Composite whereBars;
    private Composite parent;
    private Composite subSection;
    private AdvancedReportsView view;

    private List<Class<?>> searchableModelObjects;
    private List<Method> modelObjectMethods;
    private ComboViewer selectedTypeCombo;
    private AttributeQueryClause attributeClause;
    private List<ModelObjectQuery> modelObjectClauses;

    public QueryPage(AdvancedReportsView advancedReportsView, Composite parent,
        int style) {
        super(parent, style);
        this.view = advancedReportsView;
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
        List<Method> methods = filterMethods(type);

        attributeClause = new AttributeQueryClause(whereBars, methods,
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

        // update parents
        view.resetSearch();
        view.updateScrollBars();
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

    public static List<Method> filterMethods(Class<?> type) {
        Method[] unfiltered = type.getDeclaredMethods();
        List<Method> filtered = new ArrayList<Method>();
        Method getProperties = null;
        String[] props = null;
        if (type.getName().contains("Wrapper")) {
            try {
                getProperties = type
                    .getDeclaredMethod("getPropertyChangesNames");
                getProperties.setAccessible(true);
            } catch (SecurityException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            try {
                Constructor<?> c = null;
                try {
                    c = type
                        .getDeclaredConstructor(WritableApplicationService.class);
                    c.setAccessible(true);
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                }
                Object arglist = null;
                props = (String[]) getProperties.invoke(c.newInstance(arglist));
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < unfiltered.length; i++) {
            String name = unfiltered[i].getName();
            if (name.startsWith("get")
                && unfiltered[i].getParameterTypes().length == 0) {
                if (props != null) {
                    for (int j = 0; j < props.length; j++) {
                        if (name.substring(3).compareToIgnoreCase(props[j]) == 0) {
                            filtered.add(unfiltered[i]);
                            break;
                        }
                    }
                } else
                    filtered.add(unfiltered[i]);
            }
        }
        return filtered;
    }

    public Class<?> getActiveWrapperClass() {
        IStructuredSelection typeSelection = (IStructuredSelection) selectedTypeCombo
            .getSelection();
        return (Class<?>) (typeSelection.getFirstElement());
    }

    private void createSearchablesList() {
        searchableModelObjects.add(ContainerWrapper.class);
        searchableModelObjects.add(ContainerTypeWrapper.class);
        searchableModelObjects.add(SiteWrapper.class);
        searchableModelObjects.add(PatientWrapper.class);
        searchableModelObjects.add(StudyWrapper.class);
        searchableModelObjects.add(AliquotWrapper.class);
        searchableModelObjects.add(ClinicWrapper.class);
    }

    @SuppressWarnings("unchecked")
    public HQLCriteria getQuery() {

        IStructuredSelection typeSelection = (IStructuredSelection) selectedTypeCombo
            .getSelection();
        Class<?> type = (Class<?>) typeSelection.getFirstElement();
        String query = "select " + AttributeQueryClause.getText(type.getName());
        try {
            query += " from "
                + ((ModelWrapper<?>) type.getDeclaredConstructor(
                    WritableApplicationService.class).newInstance(
                    SessionManager.getAppService())).getWrappedClass()
                    .getName() + " as "
                + AttributeQueryClause.getText(type.getName());
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

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
