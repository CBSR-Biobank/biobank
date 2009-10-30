package edu.ualberta.med.biobank.views;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.widgets.AbstractQueryClause;
import edu.ualberta.med.biobank.widgets.QueryPage;
import edu.ualberta.med.biobank.widgets.ReportsLabelProvider;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;

public class ReportsView extends ViewPart {

    @SuppressWarnings("unused")
    public static Logger LOGGER = Logger.getLogger(ReportsView.class.getName());

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";
    private ScrolledComposite sc;
    private Composite top;
    private Composite subSection;

    private ComboViewer querySelect;
    private List<QueryObject> queryObjects;
    private List<Text> textFields;
    private List<Label> textLabels;

    private Button searchButton;
    private Button saveSearch;
    private Collection<Object> searchData;
    private InfoTableWidget<Object> searchTable;

    public ReportsView() {
        searchData = new ArrayList<Object>();
    }

    @Override
    public void createPartControl(Composite parent) {
        sc = new ScrolledComposite(parent, SWT.V_SCROLL);
        sc.setLayout(new GridLayout(1, false));
        sc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);

        top = new Composite(sc, SWT.NONE);
        top.setLayout(new GridLayout(1, false));
        top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

        queryObjects = QueryObject.getAllQueries();
        querySelect = createCombo(top, queryObjects);
        querySelect
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    comboChanged();
                }
            });

        // create the query's display here
        subSection = new Composite(top, SWT.NONE);

        Label resultsLabel = new Label(top, SWT.NONE);
        resultsLabel.setText("Results:");

        searchTable = new InfoTableWidget<Object>(top, searchData,
            new String[] {}, null);
        GridData searchLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        // searchLayoutData.minimumHeight = 300;
        searchTable.setLayoutData(searchLayoutData);

        searchButton = new Button(top, SWT.NONE);
        searchButton.setText("Search");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchData = search();
                if (searchData.size() > 0) {
                    IStructuredSelection typeSelection = (IStructuredSelection) querySelect
                        .getSelection();
                    QueryObject query = (QueryObject) typeSelection
                        .getFirstElement();
                    List<Method> filteredMethods = QueryPage
                        .filterMethods(query.getReturnType());
                    int[] bounds = new int[filteredMethods.size()];
                    String[] names = new String[filteredMethods.size()];
                    for (int i = 0; i < filteredMethods.size(); i++) {
                        names[i] = filteredMethods.get(i).getName()
                            .substring(3);
                    }
                    Arrays.sort(names, new Comparator<String>() {

                        @Override
                        public int compare(String o1, String o2) {
                            if (o1.compareToIgnoreCase("Name") == 0)
                                return -1;
                            else if (o2.compareToIgnoreCase("Name") == 0)
                                return 1;
                            else
                                return o1.compareToIgnoreCase(o2);
                        }

                    });
                    for (int i = 0; i < filteredMethods.size(); i++) {
                        bounds[i] = 100 + names[i].length() * 2;
                    }

                    searchTable.dispose();
                    searchTable = new InfoTableWidget<Object>(top, searchData,
                        names, bounds);
                    searchTable.getTableViewer().setLabelProvider(
                        new ReportsLabelProvider());
                    GridData searchLayoutData = new GridData(SWT.FILL,
                        SWT.FILL, true, true);
                    searchLayoutData.minimumHeight = 500;
                    searchTable.setLayoutData(searchLayoutData);
                    searchTable.moveAbove(searchButton);
                }
                searchTable.setCollection(searchData);
                searchTable.redraw();
                top.layout();

            }
        });

        saveSearch = new Button(top, SWT.NONE);
        saveSearch.setText("Save Search");
        saveSearch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

            }
        });

        querySelect.setSelection(new StructuredSelection(queryObjects.get(0)));
        top.layout();
        sc.setContent(top);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    }

    private Collection<Object> search() {
        IStructuredSelection typeSelection = (IStructuredSelection) querySelect
            .getSelection();
        QueryObject query = (QueryObject) typeSelection.getFirstElement();
        ArrayList<Object> params = new ArrayList<Object>();
        for (int i = 0; i < textFields.size(); i++) {
            params.add(textFields.get(i).getText());
        }
        return query.executeQuery(params);
    }

    public void comboChanged() {
        IStructuredSelection typeSelection = (IStructuredSelection) querySelect
            .getSelection();
        QueryObject query = (QueryObject) typeSelection.getFirstElement();

        List<Class<?>> fields = query.getFieldTypes();
        List<String> fieldNames = query.getFieldNames();
        textLabels = new ArrayList<Label>();
        textFields = new ArrayList<Text>();

        if (subSection != null)
            subSection.dispose();

        subSection = new Composite(top, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        subSection.setLayout(layout);

        Label description = new Label(subSection, SWT.NONE);
        description.setText("Description: " + query.getDescription());
        GridData gd2 = new GridData();
        gd2.horizontalSpan = 2;
        description.setLayoutData(gd2);

        for (int i = 0; i < fields.size(); i++) {
            Label fieldLabel = new Label(subSection, SWT.NONE);
            fieldLabel.setText(fieldNames.get(i) + ":");
            textLabels.add(fieldLabel);
            Text textField = new Text(subSection, SWT.BORDER);
            textFields.add(textField);
        }

        subSection.moveBelow(querySelect.getCombo());
        subSection.setVisible(true);

        // update parents
        resetSearch();
        updateScrollBars();
        top.layout(true, true);

    }

    @Override
    public void setFocus() {

    }

    public void updateScrollBars() {
        sc.layout(true, true);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public void resetSearch() {
        if (searchTable != null)
            searchTable.setCollection(new ArrayList<Object>());
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
}
