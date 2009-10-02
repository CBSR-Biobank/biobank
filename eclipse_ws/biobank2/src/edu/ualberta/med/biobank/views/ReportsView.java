package edu.ualberta.med.biobank.views;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.part.ViewPart;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.widgets.QueryPage;
import edu.ualberta.med.biobank.widgets.infotables.InfoTableWidget;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ReportsView extends ViewPart {

    public static final String ID = "edu.ualberta.med.biobank.views.ReportsView";
    private ScrolledComposite sc;
    private Composite top;

    private QueryPage queryPage;

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

        queryPage = new QueryPage(this, top, SWT.NONE);

        Label resultsLabel = new Label(top, SWT.NONE);
        resultsLabel.setText("Results:");

        searchTable = new InfoTableWidget<Object>(top, searchData,
            new String[] {}, null);
        GridData searchLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
        searchLayoutData.minimumHeight = 500;
        searchTable.setLayoutData(searchLayoutData);

        searchButton = new Button(top, SWT.NONE);
        searchButton.setText("Search");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                searchData = search();
                if (searchData.size() > 0) {
                    Iterator<Object> searchDataIt = searchData.iterator();
                    List<Method> filteredMethods = filterMethods(searchDataIt
                        .next().getClass().getDeclaredMethods());

                    String[] names = new String[filteredMethods.size()];
                    for (int i = 0; i < filteredMethods.size(); i++) {
                        names[i] = filteredMethods.get(i).getName()
                            .substring(3);
                    }
                    searchTable.dispose();
                    searchTable = new InfoTableWidget<Object>(top, searchData,
                        names, null);
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

        top.layout();
        sc.setContent(top);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));

    }

    private Collection<Object> search() {
        try {
            HQLCriteria c = queryPage.getQuery();
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

    public void updateScrollBars() {
        sc.layout(true, true);
        sc.setMinSize(top.computeSize(SWT.DEFAULT, SWT.DEFAULT));
    }

    public List<Method> filterMethods(Method[] unfiltered) {
        List<Method> filtered = new ArrayList<Method>();
        for (int i = 0; i < unfiltered.length; i++)
            if (unfiltered[i].getName().contains("get")
                || unfiltered[i].getName().contains("set"))
                filtered.add(unfiltered[i]);
        return filtered;
    }
}
