package edu.ualberta.med.biobank.views;

import java.util.Date;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventGroup;
import edu.ualberta.med.biobank.widgets.DateTimeWidget;

public class ProcessingView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ProcessingView";

    private static ProcessingView currentInstance;

    private ProcessingEventGroup processingNode;

    private Button radioWorksheet;

    private Composite dateComposite;

    private DateTimeWidget dateWidget;

    private Button radioDateProcessed;

    public ProcessingView() {
        super();
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        processingNode = new ProcessingEventGroup(rootNode, 2,
            "Processing Events");
        processingNode.setParent(rootNode);
        rootNode.addChild(processingNode);
    }

    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioWorksheet = new Button(composite, SWT.RADIO);
        radioWorksheet.setText("Worksheet");
        radioWorksheet.setSelection(true);
        radioWorksheet.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioWorksheet.getSelection()) {
                    showTextOnly(true);
                }
            }
        });
        // radioDateSent = new Button(composite, SWT.RADIO);
        // radioDateSent.setText("Packed At");
        // radioDateSent.addSelectionListener(new SelectionAdapter() {
        // @Override
        // public void widgetSelected(SelectionEvent e) {
        // if (radioDateSent.getSelection()) {
        // showTextOnly(false);
        // }
        // }
        // });

        radioDateProcessed = new Button(composite, SWT.RADIO);
        radioDateProcessed.setText("Date Processed");
        radioDateProcessed.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioDateProcessed.getSelection()) {
                    showTextOnly(false);
                }
            }
        });

        dateComposite = new Composite(parent, SWT.NONE);
        layout = new GridLayout(2, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        dateComposite.setLayout(layout);
        GridData gd = new GridData();
        gd.exclude = true;
        dateComposite.setLayoutData(gd);

        dateWidget = new DateTimeWidget(dateComposite, SWT.DATE, new Date());
        dateWidget.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
                internalSearch();
            }
        });
        Button searchButton = new Button(dateComposite, SWT.PUSH);
        searchButton.setText("Go");
        searchButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                internalSearch();
            }
        });
    }

    public static ProcessingView getCurrent() {
        return currentInstance;
    }

    public static void reloadCurrent() {
        if (currentInstance != null)
            currentInstance.reload();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return "";
    }

    @Override
    protected void internalSearch() {
        try {
            List<? extends ModelWrapper<?>> searchedObject = search();
            if (searchedObject.size() == 0) {
                String msg = "No Processing Events found";
                if (radioWorksheet.getSelection()) {
                    msg += " for worksheet " + treeText.getText();
                } else {
                    msg += " for date "
                        + DateFormatter.formatAsDate(DateFormatter.convertDate(
                            DateFormatter.LOCAL, DateFormatter.GMT,
                            dateWidget.getDate()));
                }
                BiobankPlugin.openMessage("Processing Event not found", msg);
            } else {
                showSearchedObjectsInTree(searchedObject, true);
                getTreeViewer().expandToLevel(processingNode, 2);
            }
        } catch (Exception e) {
            BiobankPlugin.openAsyncError("Search error", e);
        }
    }

    protected List<? extends ModelWrapper<?>> search() throws Exception {
        List<ProcessingEventWrapper> processingEvents;
        if (radioWorksheet.getSelection()) {
            processingEvents = ProcessingEventWrapper
                .getProcessingEventsWithWorksheet(
                    SessionManager.getAppService(), treeText.getText().trim());
        } else
            processingEvents = ProcessingEventWrapper
                .getProcessingEventsWithDate(SessionManager.getAppService(),
                    dateWidget.getDate());
        return processingEvents;
    }

    protected void showTextOnly(boolean show) {
        treeText.setVisible(show);
        ((GridData) treeText.getLayoutData()).exclude = !show;
        dateComposite.setVisible(!show);
        ((GridData) dateComposite.getLayoutData()).exclude = show;
        treeText.getParent().layout(true, true);
    }

    protected void showSearchedObjectsInTree(
        List<? extends ModelWrapper<?>> searchedObjects, boolean doubleClick) {
        for (ModelWrapper<?> searchedObject : searchedObjects) {
            List<AdapterBase> nodeRes = rootNode.search(searchedObject);
            if (nodeRes.size() == 0) {
                ProcessingEventAdapter newChild = new ProcessingEventAdapter(
                    processingNode, (ProcessingEventWrapper) searchedObject);
                newChild.setParent(processingNode);
                processingNode.addChild(newChild);
                processingNode.performExpand();
                nodeRes = processingNode.search(searchedObject);
            }
            if (nodeRes.size() > 0) {
                setSelectedNode(nodeRes.get(0));
                if (doubleClick) {
                    nodeRes.get(0).performDoubleClick();
                }
            }
        }
    }

    public AdapterBase getProcessingNode() {
        return processingNode;
    }

    @Override
    public void clear() {
        processingNode.removeAll();
        setSearchFieldsEnablement(false);
    }
}
