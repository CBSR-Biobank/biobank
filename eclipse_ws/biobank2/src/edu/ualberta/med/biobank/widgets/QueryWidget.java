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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.views.ReportsView;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class QueryWidget extends Composite {
    private List<Class<?>> searchables;
    private List<Field> subObjs;
    private ComboViewer typeCombo;
    private List<QueryBarWidget> queryBars;
    private List<QueryBarWidget> subObjectBars;
    private Composite barsAndButton;
    private Composite whereBars;
    private Composite parent;
    private Composite subSection;
    private ReportsView view;

    private SelectionAdapter mainClassAddClause = new SelectionAdapter() {
        @Override
        public void widgetSelected(SelectionEvent e) {
            IStructuredSelection typeSelection = (IStructuredSelection) typeCombo
                .getSelection();
            Class<?> type = (Class<?>) typeSelection.getFirstElement();
            Field[] classFields = type.getDeclaredFields();
            List<Field> fields = getAttributes(classFields);

            QueryBarWidget bar = new QueryBarWidget(whereBars);
            bar.updateWhereCombo(fields);
            queryBars.add(bar);
            view.updateScrollBars();
        }
    };

    public QueryWidget(ReportsView view, Composite parent, int style) {
        super(parent, style);
        this.view = view;
        this.parent = parent;
        GridLayout queryLayout = new GridLayout(4, false);
        queryLayout.verticalSpacing = 20;
        setLayout(queryLayout);

        searchables = new ArrayList<Class<?>>();
        subObjs = new ArrayList<Field>();
        createSearchablesList();

        Label type = new Label(this, SWT.NONE);
        // what type of object are you searching for?
        type.setText("Select a type to search:");

        typeCombo = createCombo(this, searchables);
        typeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                typeChanged();
            }
        });

        barsAndButton = new Composite(this, SWT.NONE);
        GridLayout bnbLayout = new GridLayout(2, false);
        bnbLayout.verticalSpacing = 10;
        barsAndButton.setLayout(bnbLayout);

        whereBars = new Composite(barsAndButton, SWT.NONE);
        GridLayout whereLayout = new GridLayout(4, false);
        whereLayout.verticalSpacing = 10;
        whereBars.setLayout(whereLayout);

        queryBars = new ArrayList<QueryBarWidget>();
        queryBars.add(new QueryBarWidget(whereBars));
        subObjectBars = new ArrayList<QueryBarWidget>();

        typeCombo.setSelection(new StructuredSelection(searchables.get(0)));
    }

    public void typeChanged() {

        barsAndButton.dispose();
        queryBars.clear();
        barsAndButton = new Composite(this, SWT.NONE);
        GridLayout bnbLayout = new GridLayout(2, false);
        bnbLayout.verticalSpacing = 10;
        barsAndButton.setLayout(bnbLayout);

        whereBars = new Composite(barsAndButton, SWT.NONE);
        GridLayout whereLayout = new GridLayout(4, false);
        whereLayout.verticalSpacing = 10;
        whereBars.setLayout(whereLayout);

        queryBars = new ArrayList<QueryBarWidget>();
        queryBars.add(new QueryBarWidget(whereBars));
        subObjectBars = new ArrayList<QueryBarWidget>();

        Button andButton;
        andButton = new Button(barsAndButton, SWT.NONE);
        andButton.setText("And");
        andButton.addSelectionListener(mainClassAddClause);

        if (subSection != null)
            subSection.dispose();

        subSection = new Composite(this, SWT.NONE);
        GridLayout subSectionLayout = new GridLayout(4, false);
        subSectionLayout.verticalSpacing = 20;
        subSection.setLayout(subSectionLayout);
        GridData colSpanInfo = new GridData();
        colSpanInfo.horizontalSpan = 3;
        subSection.setLayoutData(colSpanInfo);

        IStructuredSelection typeSelection = (IStructuredSelection) typeCombo
            .getSelection();
        Class<?> type = (Class<?>) typeSelection.getFirstElement();
        Field[] classFields = type.getDeclaredFields();
        List<Field> fields = getAttributes(classFields);

        // update sub-objects
        setSubObjs(classFields);
        for (Field subObj : subObjs)
            addSubObjBar(subSection, subObj);

        // update attributes
        for (QueryBarWidget bar : queryBars)
            bar.updateWhereCombo(fields);

        QueryWidget.this.parent.layout(true, true);

    }

    private List<Field> getAttributes(Field[] classFields) {
        List<Field> attributes = new ArrayList<Field>();
        for (Field field : classFields) {
            if (field.getType().equals(String.class)
                || field.getType().equals(Integer.class)
                || field.getType().equals(Double.class)
                || field.getType().equals(Collection.class)) {
                attributes.add(field);
            }
        }
        return attributes;
    }

    private void setSubObjs(Field[] classFields) {
        subObjs.clear();
        subObjectBars.clear();
        for (Field field : classFields) {
            if (!field.getType().equals(String.class)
                && !field.getType().equals(Integer.class)
                && !field.getType().equals(Double.class)
                && !field.getType().equals(Collection.class)) {
                if (!field.getType().isPrimitive())
                    subObjs.add(field);
            }
        }
    }

    private void addSubObjBar(Composite parent, Field subObj) {

        Button radio = new Button(parent, SWT.CHECK);
        radio.setSelection(false);

        final Label with = new Label(parent, SWT.NONE);
        with.setText("With: " + getText(subObj));

        final Composite subObjWhereBars = new Composite(parent, SWT.NONE);
        GridLayout subObjWhereLayout = new GridLayout(4, false);
        subObjWhereLayout.verticalSpacing = 10;
        subObjWhereBars.setLayout(subObjWhereLayout);

        final QueryBarWidget bar = new QueryBarWidget(subObjWhereBars);
        final List<Field> subObjAttributes = getAttributes(subObj.getType()
            .getDeclaredFields());
        bar.updateWhereCombo(subObjAttributes);
        subObjectBars.add(bar);
        final Button andButton;
        andButton = new Button(parent, SWT.NONE);
        andButton.setText("And");
        andButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                QueryBarWidget bar = new QueryBarWidget(subObjWhereBars);
                bar.updateWhereCombo(subObjAttributes);
                subObjectBars.add(bar);
                view.updateScrollBars();
            }
        });

        radio.addSelectionListener(new SelectionListener() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void widgetSelected(SelectionEvent e) {
                with.setEnabled(!with.getEnabled());
                bar.setEnabled(!bar.getEnabled());
                andButton.setEnabled(!andButton.getEnabled());
            }
        });

        with.setEnabled(false);
        bar.setEnabled(false);
        andButton.setEnabled(false);
    }

    private void createSearchablesList() {
        searchables.add(Container.class);
        searchables.add(ContainerType.class);
        searchables.add(Site.class);
        searchables.add(Patient.class);
        searchables.add(Study.class);
        searchables.add(Sample.class);
    }

    public HQLCriteria getQuery() {

        IStructuredSelection typeSelection = (IStructuredSelection) typeCombo
            .getSelection();
        Class<?> type = (Class<?>) typeSelection.getFirstElement();

        String query = "from " + type.getName() + " where ";

        List<Object> params = new ArrayList<Object>();

        for (int i = 0; i < queryBars.size(); i++) {
            QueryBarWidget qb = queryBars.get(i);

            // convert value if necessary to correct type
            if (qb.getWhere().getType().equals(Integer.class)
                || qb.getWhere().getType().equals(Double.class)
                || qb.getWhere().getType().equals(Collection.class))
                params.add(Integer.valueOf(qb.getValue()));
            else
                params.add(qb.getValue());

            // query syntax modifications
            if (qb.getWhere().getType().equals(Collection.class))
                query += "size(" + qb.getWhere().getName() + ") "
                    + qb.getOperator() + " ? and ";
            // modify query to use "like" if user wants contains
            else if (qb.getOperator().compareTo("contains") == 0) {
                query += qb.getWhere().getName() + " like ? and ";
                params.set(i, "%" + params.get(i) + "%");
            } else
                query += qb.getWhere().getName() + " " + qb.getOperator()
                    + " ? and ";

        }
        for (int i = 0; i < subObjectBars.size(); i++) {
            QueryBarWidget qbs = subObjectBars.get(i);
            if (!qbs.getEnabled())
                continue;
            String where = subObjs.get(i).getName() + "."
                + qbs.getWhere().getName();
            // convert value if necessary to correct type
            if (qbs.getWhere().getType().equals(Integer.class)
                || qbs.getWhere().getType().equals(Double.class)
                || qbs.getWhere().getType().equals(Collection.class))
                params.add(Integer.valueOf(qbs.getValue()));
            else
                params.add(qbs.getValue());

            // query syntax modifications
            if (qbs.getWhere().getType().equals(Collection.class))
                query += "size(" + where + ") " + qbs.getOperator() + " ? and ";
            // modify query to use "like" if user wants contains
            else if (qbs.getOperator().compareTo("contains") == 0) {
                query += where + " like ? and ";
                params.set(params.size() - 1, "%"
                    + params.get(params.size() - 1) + "%");
            } else
                query += where + " " + qbs.getOperator() + " ? and ";
        }

        query = query.substring(0, query.length() - 4);
        HQLCriteria c = new HQLCriteria(query, params);
        return c;
    }

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
                return QueryWidget.getText(element);
            }
        });
        comboViewer.setInput(list);
        return comboViewer;
    }

    private static String getText(Object element) {
        String[] s = element.toString().split("\\.");
        if (s.length > 0)
            return s[s.length - 1];
        else
            return null;
    }

}
