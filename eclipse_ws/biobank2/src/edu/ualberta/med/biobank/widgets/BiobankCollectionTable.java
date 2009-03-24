package edu.ualberta.med.biobank.widgets;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.springframework.util.Assert;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Clinic;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.treeview.ClinicAdapter;
import edu.ualberta.med.biobank.treeview.StudyAdapter;

public class BiobankCollectionTable extends Composite {
    
    private TableViewer tableViewer;

    public BiobankCollectionTable(Composite parent, int style, 
            String [] headings, Object [] data) {
        super(parent, style);
        
        setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        setLayout(new GridLayout(1, false));
        
        tableViewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION);
        tableViewer.setLabelProvider(new BiobankLabelProvider());
        tableViewer.setContentProvider(new BiobankContentProvider());
        
        Table table = tableViewer.getTable();
        table.setLayout(new TableLayout());
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = 100;
        table.setLayoutData(gd);
        table.setFont(getFont());
        table.setHeaderVisible(true);
        table.setLinesVisible(true);
        
        for (String name : headings) {
            final TableColumn col = new TableColumn(table, SWT.NONE);
            col.setText(name);
            col.setResizable(true);
            col.addListener(SWT.SELECTED, new Listener() {
               public void handleEvent(Event event) {
                   col.pack();
               }
            });
        }
        tableViewer.setColumnProperties(headings);
        
        // hack required here because site.getStudyCollection().toArray(new Study[0])
        // returns Object[].        
        tableViewer.setInput(data);
        
        for (int i = 0, n = table.getColumnCount(); i < n; i++) {
            table.getColumn(i).pack();
        }
    }
    
    public TableViewer getTableViewer() {
        return tableViewer;
    }

    public void adaptToToolkit(FormToolkit toolkit) {
        toolkit.adapt(this, true, true);
        adaptAllChildren(this, toolkit);
    }
    
    private void adaptAllChildren(Composite container, FormToolkit toolkit) {
        Control[] children = container.getChildren();
        for (Control aChild : children) {
            toolkit.adapt(aChild, true, true);
            if (aChild instanceof Composite) {
                adaptAllChildren((Composite) aChild, toolkit);
            }
        }
    }
}
