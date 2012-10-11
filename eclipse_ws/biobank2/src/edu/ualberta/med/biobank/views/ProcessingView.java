package edu.ualberta.med.biobank.views;

import java.util.Date;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.formatters.DateFormatter;
import edu.ualberta.med.biobank.common.permission.processingEvent.ProcessingEventReadPermissionByCenter;
import edu.ualberta.med.biobank.common.util.StringUtil;
import edu.ualberta.med.biobank.common.wrappers.CenterWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.ProcessingEventWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.DateTimeWidget;
import edu.ualberta.med.biobank.model.Patient;
import edu.ualberta.med.biobank.model.ProcessingEvent;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.RootNode;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventAdapter;
import edu.ualberta.med.biobank.treeview.processing.ProcessingEventGroup;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ProcessingView extends AbstractAdministrationView {
    private static final I18n i18n = I18nFactory.getI18n(ProcessingView.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.views.ProcessingView";

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

    private void createNodes() {
        processingNode = new ProcessingEventGroup((RootNode) rootNode, 2,
            ProcessingEvent.NAME.plural().toString());
        processingNode.setParent(rootNode);
        rootNode.addChild(processingNode);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createTreeTextOptions(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(3, false);
        layout.horizontalSpacing = 0;
        layout.marginHeight = 0;
        layout.verticalSpacing = 0;
        composite.setLayout(layout);

        radioWorksheet = new Button(composite, SWT.RADIO);
        radioWorksheet.setText(ProcessingEvent.PropertyName.WORKSHEET
            .toString());
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
        radioPatient.setText(Patient.NAME.singular().toString());
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
            .setText(
            // radio label.
            i18n.tr("Date Processed"));
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
        searchButton.setText(
            // button label.
            i18n.tr("Go"));
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
    public void reload() {
        if (processingNode == null) createNodes();
        try {
            for (AbstractAdapterBase adaper : processingNode.getChildren()) {
                ((AdapterBase) adaper).resetObject();
            }
            setSearchFieldsEnablement(SessionManager.getAppService().isAllowed(
                new ProcessingEventReadPermissionByCenter(SessionManager
                    .getUser().getCurrentWorkingCenter().getWrappedObject())));
        } catch (Exception e) {
            BgcPlugin.openAccessDeniedErrorMessage();
        }
        try {
            CenterWrapper<?> center = SessionManager
                .getUser().getCurrentWorkingCenter();
            if (center != null) {
                setSearchFieldsEnablement(SessionManager.getAppService()
                    .isAllowed(new ProcessingEventReadPermissionByCenter(center
                        .getWrappedObject())));
            }
        } catch (ApplicationException e) {
            BgcPlugin.openAccessDeniedErrorMessage();
        }
        super.reload();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    protected String getTreeTextToolTip() {
        return StringUtil.EMPTY_STRING;
    }

    @SuppressWarnings("nls")
    @Override
    protected void internalSearch() {
        try {
            List<? extends ModelWrapper<?>> searchedObject = search();
            if (searchedObject.size() == 0) {
                String msg;
                if (radioWorksheet.getSelection()) {
                    // dialog message.
                    msg =
                        i18n.tr("No Processing Events found for worksheet {0}",
                            treeText.getText());
                } else if (radioPatient.getSelection()) {
                    // dialog message.
                    msg = i18n.tr("No Processing Events found for patient {0}",
                        treeText.getText());
                } else {
                    // dialog message.
                    msg = i18n.tr("No Processing Events found for date ",
                        DateFormatter.formatAsDate(dateWidget.getDate()));
                }
                BgcPlugin.openMessage(
                    // dialog title.
                    i18n.tr("Processing Event not found"),
                    msg);
            } else {
                showSearchedObjectsInTree(searchedObject);
                getTreeViewer().expandToLevel(processingNode,
                    TreeViewer.ALL_LEVELS);
            }
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                // dialog title
                i18n.tr("Search error"), e);
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

    @SuppressWarnings("nls")
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
            BgcPlugin.openMessage(ProcessingEvent.NAME.plural().toString(),
                // dialog message
                i18n.tr("{0} found.", searchedObjects.size()));
    }

    public AdapterBase getProcessingNode() {
        return processingNode;
    }

    @Override
    public void clear() {
        rootNode.removeAll();
        processingNode = null;
        setSearchFieldsEnablement(false);
    }

    @Override
    protected void createRootNode() {
        createOldRootNode();
    }

}
