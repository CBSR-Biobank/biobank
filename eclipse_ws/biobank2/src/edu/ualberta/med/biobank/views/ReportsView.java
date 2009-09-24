package edu.ualberta.med.biobank.views;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
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

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.Sample;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ReportsView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";
    private Composite top;
    private List<Class<?>> searchables;
    private List<Field> fields;
    private List<String> stringOps;
    private List<String> numberOps;
    private List<String> collectionOps;

    private ComboViewer typeCombo;
    private ComboViewer whereCombo;
    private ComboViewer operatorCombo;
    private Text searchField;
    private Button searchButton;
    private Button saveSearch;
    private Collection<Object> searchData;
    private InfoTableWidget<Object> searchTable;

    public ReportsView() {
        searchables = new ArrayList<Class<?>>();
        fields = new ArrayList<Field>();
        stringOps = new ArrayList<String>();
        numberOps = new ArrayList<String>();
        collectionOps = new ArrayList<String>();

        // operators

        stringOps.add("=");
        stringOps.add(" like ");

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
        collectionOps.add("contains");

        searchData = new ArrayList<Object>();
    }

    @Override
    public void createPartControl(Composite parent) {
        top = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(8, false);
        layout.horizontalSpacing = 10;
        layout.verticalSpacing = 30;
        top.setLayout(layout);

        createSearchablesList();

        // what type of object are you searching for?
        Label type = new Label(top, SWT.None);
        type.setText("Select a type to search:");
        typeCombo = createCombo(searchables);
        typeCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                updateFields();
                whereCombo.setInput(fields);
            }
        });

        // constraints

        // variable

        Label where = new Label(top, SWT.None);
        where.setText("Where: ");
        whereCombo = createCombo(null);
        whereCombo.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                try {
                    IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
                        .getSelection();
                    Field whereField = (Field) whereSelection.getFirstElement();
                    if (whereField.getType().equals(String.class))
                        operatorCombo.setInput(stringOps);
                    else if (whereField.getType().equals(Integer.class)
                        || whereField.getType().equals(Double.class))
                        operatorCombo.setInput(numberOps);
                    else if (whereField.getType().equals(Collection.class))
                        operatorCombo.setInput(collectionOps);
                    else {
                        operatorCombo.setInput(null);
                        throw new Exception(
                            "Field does not have a corresponding operator set.");
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

        // operator
        operatorCombo = createCombo(null);
        // value
        searchField = new Text(top, SWT.BORDER);
        searchField.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == 13) {
                    searchData = search();
                    searchTable.setCollection(searchData);
                    searchTable.redraw();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
            }

        });
        GridData gd = new GridData();
        gd.widthHint = 100;
        searchField.setLayoutData(gd);

        searchButton = new Button(top, SWT.NONE);
        searchButton.setText("Search");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchData = search();
                searchTable.setCollection(searchData);
                searchTable.redraw();
            }
        });

        saveSearch = new Button(top, SWT.NONE);
        saveSearch.setText("Save Search");
        saveSearch.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

            }
        });

        searchTable = new InfoTableWidget<Object>(top, searchData,
            new String[] {}, null);
        GridData gdst = new GridData();
        gdst.grabExcessHorizontalSpace = true;
        gdst.horizontalSpan = 8;
        gdst.widthHint = 1200;
        searchTable.setLayoutData(gdst);

    }

    private void updateFields() {
        IStructuredSelection whereSelection = (IStructuredSelection) typeCombo
            .getSelection();
        Class<?> type = (Class<?>) whereSelection.getFirstElement();
        Field[] classFields = type.getDeclaredFields();
        for (Field field : classFields)
            if (field.getName().compareToIgnoreCase("id") != 0)
                fields.add(field);

    }

    private void createSearchablesList() {
        searchables.add(Container.class);
        searchables.add(Site.class);
        searchables.add(Patient.class);
        searchables.add(Study.class);
        searchables.add(Sample.class);
    }

    private Collection<Object> search() {
        try {
            IStructuredSelection typeSelection = (IStructuredSelection) typeCombo
                .getSelection();
            IStructuredSelection whereSelection = (IStructuredSelection) whereCombo
                .getSelection();
            IStructuredSelection operatorSelection = (IStructuredSelection) operatorCombo
                .getSelection();

            Class<?> type = (Class<?>) typeSelection.getFirstElement();
            Field where = (Field) whereSelection.getFirstElement();
            String operator = (String) operatorSelection.getFirstElement();
            String value = searchField.getText();

            HQLCriteria c = new HQLCriteria("from " + type.getName()
                + " where ? " + operator + " ?", Arrays.asList(new Object[] {
                where.getName(), value }));
            List<Object> result = SessionManager.getAppService().query(c);
            return result;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void setFocus() {

    }

    /**
     * This method initializes a combo
     * 
     */
    private ComboViewer createCombo(List<?> list) {
        Combo combo;
        ComboViewer comboViewer;
        combo = new Combo(top, SWT.READ_ONLY);

        GridData combodata = new GridData();
        combodata.widthHint = 190;
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
}
