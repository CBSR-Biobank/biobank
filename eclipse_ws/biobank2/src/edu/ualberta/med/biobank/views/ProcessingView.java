package edu.ualberta.med.biobank.views;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventGroup;

public class ProcessingView extends AbstractAdministrationView {

    public static final String ID = "edu.ualberta.med.biobank.views.ProcessingView"; //$NON-NLS-1$

    private static ProcessingView currentInstance;

    private ProcessingEventGroup processingNode;

    private Button radioWorksheet;

    private Composite dateComposite;

    private DateTimeWidget dateWidget;

    private Button radioDateProcessed;

    private Button radioPatient;

    public ProcessingView() {
        super();
        currentInstance = this;
        SessionManager.addView(this);
    }

    @Override
    public void createPartControl(Composite parent) {
        super.createPartControl(parent);

        processingNode = new ProcessingEventGroup(rootNode, 2,
            Messages.ProcessingView_pevent_group_label);
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
        radioWorksheet.setText(Messages.ProcessingView_worksheet_label);
        radioWorksheet.setSelection(true);
        radioWorksheet.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioWorksheet.getSelection()) {
                    showTextOnly(true);
                }
            }
        });
        radioPatient = new Button(composite, SWT.RADIO);
        radioPatient.setText(Messages.ProcessingView_patient_label);
        radioPatient.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                if (radioPatient.getSelection()) {
                    showTextOnly(true);
                }
            }
        });

        radioDateProcessed = new Button(composite, SWT.RADIO);
        radioDateProcessed
            .setText(Messages.ProcessingView_date_processed_label);
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
        searchButton.setText(Messages.ProcessingView_go_button_label);
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
        return ""; //$NON-NLS-1$
    }

    @Override
    protected void internalSearch() {
        try {
            List<? extends ModelWrapper<?>> searchedObject = search();
            if (searchedObject.size() == 0) {
                String msg;
                if (radioWorksheet.getSelection()) {
                    msg = NLS.bind(
                        Messages.ProcessingView_notfound_worksheet_msg,
                        treeText.getText());
                } else if (radioPatient.getSelection()) {
                    msg = NLS.bind(
                        Messages.ProcessingView_notfound_patient_msg,
                        treeText.getText());
                } else {
                    msg = NLS.bind(Messages.ProcessingView_notfound_date_msg,
                        DateFormatter.formatAsDate(dateWidget.getDate()));
                }
                BgcPlugin.openMessage(Messages.ProcessingView_notFound_title,
                    msg);
            } else {
                showSearchedObjectsInTree(searchedObject);
                getTreeViewer().expandToLevel(processingNode,
                    TreeViewer.ALL_LEVELS);
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.ProcessingView_search_error_title, e);
        }
    }

    protected List<? extends ModelWrapper<?>> search() throws Exception {
        List<ProcessingEventWrapper> processingEvents;
        if (radioWorksheet.getSelection()) {
            processingEvents = ProcessingEventWrapper
                .getProcessingEventsWithWorksheet(
                    SessionManager.getAppService(), treeText.getText().trim());
        } else if (radioPatient.getSelection()) {
            processingEvents = ProcessingEventWrapper
                .getProcessingEventsByPatient(SessionManager.getAppService(),
                    treeText.getText().trim());
        } else
            processingEvents = ProcessingEventWrapper
                .getProcessingEventsWithDateForCenter(
                    SessionManager.getAppService(), dateWidget.getDate(),
                    SessionManager.getUser().getCurrentWorkingCenter());
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
        List<? extends ModelWrapper<?>> searchedObjects) {
        processingNode.removeAll();
        for (ModelWrapper<?> searchedObject : searchedObjects) {
            List<AbstractAdapterBase> nodeRes = rootNode.search(
                searchedObject.getClass(), searchedObject.getId());
            if (nodeRes.size() == 0) {
                ProcessingEventAdapter newChild = new ProcessingEventAdapter(
                    processingNode, (ProcessingEventWrapper) searchedObject);
                newChild.setParent(processingNode);
                processingNode.addChild(newChild);
            }
        }
        processingNode.performExpand();
        if (searchedObjects.size() == 1) {
            ModelWrapper<?> modelWrapper = searchedObjects.get(0);
            List<AbstractAdapterBase> nodeRes = rootNode.search(
                modelWrapper.getClass(), modelWrapper.getId());
            nodeRes.get(0).performDoubleClick();
        } else
            BgcPlugin.openMessage(Messages.ProcessingView_pevent_info_title,
                NLS.bind(Messages.ProcessingView_number_found_msg,
                    searchedObjects.size()));
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
