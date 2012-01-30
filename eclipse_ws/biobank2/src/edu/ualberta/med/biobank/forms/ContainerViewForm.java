package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.exception.BiobankCheckException;
import edu.ualberta.med.biobank.common.util.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.SpecimenPosition;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.cell.AbstractUICell;
import edu.ualberta.med.biobank.widgets.grids.cell.ContainerCell;
import edu.ualberta.med.biobank.widgets.grids.cell.UICellStatus;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.SpecimenInfoTable.ColumnsShown;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerViewForm extends BiobankViewForm {

    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerViewForm"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger
        .getLogger(ContainerViewForm.class.getName());

    private SpecimenInfoTable specimensWidget;

    private BgcBaseText siteLabel;

    private BgcBaseText containerLabelLabel;

    private BgcBaseText productBarcodeLabel;

    private BgcBaseText activityStatusLabel;

    private BgcBaseText containerTypeLabel;

    private BgcBaseText temperatureLabel;

    private ContainerDisplayWidget containerWidget;

    private Map<RowColPos, ContainerCell> cells;

    private boolean childrenOk = true;

    private Composite childrenActionSection;

    private boolean canCreate;

    private boolean canDelete;

    private ComboViewer initSelectionCv;

    private ComboViewer deleteCv;

    private CommentCollectionInfoTable commentTable;

    private ContainerInfo containerInfo;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerAdapter,
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());

        updateContainerInfo();

        setPartName(NLS.bind(Messages.ContainerViewForm_title,
            containerInfo.container.getLabel(), containerInfo.container
                .getContainerType().getNameShort()));
        initCells();
        canCreate = SessionManager.canCreate(ContainerWrapper.class);
        canDelete = SessionManager.canDelete(ContainerWrapper.class);
    }

    private void updateContainerInfo() throws ApplicationException {
        containerInfo = SessionManager.getAppService().doAction(
            new ContainerGetInfoAction(adapter.getId()));
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(NLS.bind(Messages.ContainerViewForm_title,
            containerInfo.container.getLabel(), containerInfo.container
                .getContainerType().getNameShort()));
        page.setLayout(new GridLayout(1, false));

        createContainerSection();

        if (containerInfo.container.getContainerType()
            .getSpecimenTypeCollection().size() > 0) {
            // only show specimens section this if this container type does not
            // have child containers
            createSpecimensSection();
        }
    }

    private void createContainerSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        client.setLayoutData(gridData);
        toolkit.paintBordersFor(client);

        siteLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.container_field_label_site);
        containerLabelLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.container_field_label_label);
        productBarcodeLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.container_field_label_barcode);
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.label_activity);
        containerTypeLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.container_field_label_type);
        temperatureLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Messages.container_field_label_temperature);

        createCommentsSection();

        setContainerValues();

        if (containerInfo.container.getContainerType()
            .getChildContainerTypeCollection()
            .size() > 0) {
            createVisualizeContainer();
        }
    }

    private void createCommentsSection() {
        Composite client = createSectionWithClient(Messages.label_comments);
        commentTable =
            new CommentCollectionInfoTable(client,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    containerInfo.container.getCommentCollection(),
                    CommentWrapper.class));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    private void initCells() {
        try {
            if (containerInfo.container.getContainerType()
                .getChildContainerTypeCollection().isEmpty()) return;

            Integer rowCap =
                containerInfo.container.getContainerType().getCapacity()
                    .getRowCapacity();
            Integer colCap =
                containerInfo.container.getContainerType().getCapacity()
                    .getColCapacity();
            Assert.isNotNull(rowCap, "row capacity is null"); //$NON-NLS-1$
            Assert.isNotNull(colCap, "column capacity is null"); //$NON-NLS-1$
            if (rowCap == 0) rowCap = 1;
            if (colCap == 0) colCap = 1;

            cells = new TreeMap<RowColPos, ContainerCell>();
            Map<RowColPos, ContainerWrapper> childrenMap =
                new HashMap<RowColPos, ContainerWrapper>();
            for (ContainerPosition position : containerInfo.container
                .getChildPositionCollection()) {
                childrenMap.put(
                    new RowColPos(position.getRow(), position.getCol()),
                    new ContainerWrapper(SessionManager.getAppService(),
                        position.getContainer()));

            }
            for (int i = 0; i < rowCap; i++) {
                for (int j = 0; j < colCap; j++) {
                    ContainerCell cell = new ContainerCell(i, j);
                    cells.put(new RowColPos(i, j), cell);
                    ContainerWrapper container =
                        childrenMap.get(new RowColPos(i, j));
                    if (container == null) {
                        cell.setStatus(UICellStatus.NOT_INITIALIZED);
                    } else {
                        cell.setContainer(container);
                        cell.setStatus(UICellStatus.INITIALIZED);
                    }
                }
            }
        } catch (Exception ex) {
            BgcPlugin.openAsyncError(
                Messages.ContainerViewForm_initCell_error_title,
                Messages.ContainerViewForm_initCell_error_msg);
            childrenOk = false;
        }
    }

    private void refreshVis() {
        initCells();
        if (containerWidget == null) {
            createVisualizeContainer();
            form.layout(true, true);
        }
        containerWidget.setCells(cells);
    }

    protected void createVisualizeContainer() {
        Section s = createSection(Messages.ContainerViewForm_visual_title);
        s.setLayout(new GridLayout(1, false));
        Composite containerSection = new Composite(s, SWT.NONE);
        containerSection.setLayout(new FillLayout(SWT.VERTICAL));
        ScrolledComposite sc =
            new ScrolledComposite(containerSection, SWT.H_SCROLL);
        sc.setExpandHorizontal(true);
        sc.setExpandVertical(true);
        Composite client = new Composite(sc, SWT.NONE);
        client.setLayout(new GridLayout(1, false));
        toolkit.adapt(containerSection);
        toolkit.adapt(sc);
        toolkit.adapt(client);
        sc.setContent(client);
        s.setClient(containerSection);
        if (!childrenOk) {
            Label label =
                toolkit.createLabel(client,
                    Messages.ContainerViewForm_visualization_error_msg);
            label.setForeground(Display.getCurrent().getSystemColor(
                SWT.COLOR_RED));
        }
        containerWidget =
            new ContainerDisplayWidget(client,
                UICellStatus.DEFAULT_CONTAINER_STATUS_LIST);
        containerWidget.setContainer(new ContainerWrapper(SessionManager
            .getAppService(), containerInfo.container));
        containerWidget.setCells(cells);
        toolkit.adapt(containerWidget);

        // Set the minimum size
        sc.setMinSize(containerWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        containerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                AbstractUICell cell =
                    ((ContainerDisplayWidget) e.widget).getObjectAtCoordinates(
                        e.x, e.y);
                if (cell != null) openFormFor((ContainerCell) cell);
            }
        });
        containerWidget.getMultiSelectionManager().enableMultiSelection(
            new MultiSelectionSpecificBehaviour() {
                @Override
                public void removeSelection(AbstractUICell cell) {
                    //
                }

                @Override
                public boolean isSelectable(AbstractUICell cell) {
                    return true;
                }
            });
        containerWidget.getMultiSelectionManager().addMultiSelectionListener(
            new MultiSelectionListener() {
                @Override
                public void selectionChanged(MultiSelectionEvent mse) {
                    setChildrenActionSectionEnabled(mse.selections > 0);
                }
            });
        containerWidget.displayFullInfoString(true);

        createChildrenActionsSection(containerSection);
    }

    private void createChildrenActionsSection(Composite client) {
        childrenActionSection = toolkit.createComposite(client);
        childrenActionSection.setLayout(new GridLayout(3, false));

        if (canCreate || canDelete) {
            List<ContainerTypeWrapper> containerTypes =
                getChildContainerTypes();
            if (canCreate) {
                // Initialisation action for selection
                initSelectionCv =
                    createComboViewer(
                        childrenActionSection,
                        Messages.ContainerViewForm_visualization_init_selection_label,
                        containerTypes, containerTypes.get(0));
                initSelectionCv.getCombo()
                    .setLayoutData(new GridData(SWT.LEFT));
                Button initializeSelectionButton =
                    toolkit
                        .createButton(
                            childrenActionSection,
                            Messages.ContainerViewForm_visualization_init_button_text,
                            SWT.PUSH);
                initializeSelectionButton
                    .addSelectionListener(new SelectionAdapter() {
                        @Override
                        public void widgetSelected(SelectionEvent e) {
                            ContainerTypeWrapper type =
                                (ContainerTypeWrapper) ((IStructuredSelection) initSelectionCv
                                    .getSelection()).getFirstElement();
                            initSelection(type);
                        }
                    });
                initializeSelectionButton.setLayoutData(new GridData(SWT.LEFT));
            }

            if (canDelete) {
                // Delete action for selection
                List<Object> deleteComboList = new ArrayList<Object>();
                deleteComboList
                    .add(Messages.ContainerViewForm_visualization_all_label);
                deleteComboList.addAll(containerTypes);
                deleteCv =
                    createComboViewer(
                        childrenActionSection,
                        Messages.ContainerViewForm_visualization_delete_select_label,
                        deleteComboList,
                        Messages.ContainerViewForm_visualization_all_label);
                deleteCv.getCombo().setLayoutData(new GridData(SWT.LEFT));
                Button deleteButton =
                    toolkit
                        .createButton(
                            childrenActionSection,
                            Messages.ContainerViewForm_visualization_delete_button_text,
                            SWT.PUSH);
                deleteButton.setLayoutData(new GridData(SWT.LEFT));
                deleteButton.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {
                        Boolean confirm =
                            MessageDialog
                                .openConfirm(
                                    PlatformUI.getWorkbench()
                                        .getActiveWorkbenchWindow().getShell(),
                                    Messages.ContainerViewForm_vizualisation_delete_confirm_title,
                                    Messages.ContainerViewForm_vizualisation_delete_confirm_msg);
                        if (confirm) {
                            Object selection =
                                ((IStructuredSelection) deleteCv.getSelection())
                                    .getFirstElement();
                            if (selection instanceof ContainerTypeWrapper) {
                                deleteSelection((ContainerTypeWrapper) selection);
                            } else {
                                deleteSelection(null);
                            }
                        }
                    }
                });
            }
        }
        setChildrenActionSectionEnabled(false);
    }

    private void setChildrenActionSectionEnabled(boolean enable) {
        // don't use the method seEnabled on the composite because the children
        // are not greyed out in this case
        for (Control c : childrenActionSection.getChildren()) {
            c.setEnabled(enable);
        }
    }

    private void initSelection(final ContainerTypeWrapper type) {
        IRunnableContext context =
            new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {
            context.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor
                        .beginTask(
                            Messages.ContainerViewForm_vizualisation_init_monitor_msg,
                            IProgressMonitor.UNKNOWN);
                    boolean initDone = true;
                    try {
                        final Set<RowColPos> positions =
                            containerWidget.getMultiSelectionManager()
                                .getSelectedPositions();
                        initChildrenWithType(type, positions);
                    } catch (Exception e) {
                        initDone = false;
                        BgcPlugin
                            .openAsyncError(
                                Messages.ContainerViewForm_visualization_init_error_msg,
                                e);
                    }
                    refresh(initDone, false);
                    monitor.done();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            containerWidget.getMultiSelectionManager()
                                .clearMultiSelection();
                        }
                    });
                }
            });

        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.ContainerViewForm_visualization_init_error_msg, e);
            refresh(false, false);
        }
    }

    /**
     * Initialise children at given position with the given type. If the
     * positions list is null, initialise all the children. <strong>If a
     * position is already filled then it is skipped and no changes are made to
     * it</strong>.
     * 
     * @return true if at least one children has been initialised
     * @throws BiobankCheckException
     * @throws WrapperException
     * @throws ApplicationException
     */
    public void initChildrenWithType(ContainerTypeWrapper type,
        Set<RowColPos> positions) throws Exception {
        if (positions == null) {
            Capacity capacity =
                containerInfo.container.getContainerType().getCapacity();
            for (int i = 0, n = capacity.getRowCapacity().intValue(); i < n; i++) {
                for (int j = 0, m = capacity.getColCapacity().intValue(); j < m; j++) {
                    initPositionIfEmpty(type, i, j);
                }
            }
        } else {
            for (RowColPos rcp : positions) {
                initPositionIfEmpty(type, rcp.getRow(), rcp.getCol());
            }
        }
        reload();
    }

    private void initPositionIfEmpty(ContainerTypeWrapper type, int i, int j)
        throws Exception {
        if (type == null) {
            throw new Exception(
                "Error initializing container. That is not a valid container type."); //$NON-NLS-1$
        }
        Boolean filled = (containerInfo.getChildContainer(i, j) != null);
        if (!filled) {
            // TODO: save new container

            ContainerWrapper newContainer =
                new ContainerWrapper(SessionManager.getAppService());
            newContainer.setContainerType(type);
            newContainer.setSite(getSite());
            newContainer.setParent(this, new RowColPos(i, j));
            newContainer.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            newContainer.persist();
        }
    }

    private void deleteSelection(final ContainerTypeWrapper type) {
        IRunnableContext context =
            new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {
            context.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor
                        .beginTask(
                            Messages.ContainerViewForm_visualization_delete_monitor_msg,
                            IProgressMonitor.UNKNOWN);
                    boolean deleteDones = false;
                    try {

                        Set<RowColPos> positions =
                            containerWidget.getMultiSelectionManager()
                                .getSelectedPositions();
                        deleteDones =
                            container.deleteChildrenWithType(type, positions);
                    } catch (Exception ex) {
                        BgcPlugin
                            .openAsyncError(
                                Messages.ContainerViewForm_visualization_delete_error_msg,
                                ex);
                    }
                    refresh(deleteDones, true);
                    monitor.done();
                    Display.getDefault().syncExec(new Runnable() {
                        @Override
                        public void run() {
                            containerWidget.getMultiSelectionManager()
                                .clearMultiSelection();
                        }
                    });
                }
            });
        } catch (Exception e) {
            BgcPlugin.openAsyncError(
                Messages.ContainerViewForm_visualization_delete_error_msg, e);
            refresh(false, false);
        }
    }

    private void refresh(boolean initDone, final boolean rebuild) {
        if (initDone) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                @Override
                public void run() {
                    try {
                        reload();
                    } catch (Exception e) {
                        logger.error(
                            Messages.ContainerViewForm_refresh_error_msg, e);
                    }
                    if (rebuild) {
                        containerAdapter.rebuild();
                    }
                    containerAdapter.performExpand();
                }
            });
        }
    }

    private void openFormFor(ContainerCell cell) {
        try {
            ContainerAdapter newAdapter = null;
            if (cell.getStatus() == UICellStatus.NOT_INITIALIZED) {
                if (canCreate) {
                    ContainerWrapper containerToOpen = cell.getContainer();
                    if (containerToOpen == null) {
                        containerToOpen =
                            new ContainerWrapper(SessionManager.getAppService());
                    }
                    containerToOpen
                        .setSite((SiteWrapper) containerAdapter
                            .getParentFromClass(SiteAdapter.class)
                            .getModelObject());
                    RowColPos pos = new RowColPos(cell.getRow(), cell.getCol());
                    containerToOpen.setParent(container, pos);
                    newAdapter =
                        new ContainerAdapter(containerAdapter, containerToOpen);
                    newAdapter.openEntryForm(true);
                }
            } else {
                ContainerWrapper child = cell.getContainer();
                Assert.isNotNull(child);
                SessionManager.openViewForm(child);
            }
            containerAdapter.performExpand();
        } catch (BiobankCheckException e) {
            BgcPlugin.openAsyncError(Messages.ContainerViewForm_error_title, e);
        }
    }

    private void setContainerValues() {
        setTextValue(siteLabel, containerInfo.container.getSite().getName());
        setTextValue(containerLabelLabel, containerInfo.container.getLabel());
        setTextValue(productBarcodeLabel,
            containerInfo.container.getProductBarcode());
        setTextValue(activityStatusLabel,
            containerInfo.container.getActivityStatus());
        setTextValue(containerTypeLabel, containerInfo.container
            .getContainerType().getName());
        setTextValue(temperatureLabel, containerInfo.container
            .getTopContainer().getTemperature());
    }

    private List<SpecimenWrapper> getSpecimenWrappers() {
        List<SpecimenWrapper> specimens = new ArrayList<SpecimenWrapper>();
        for (SpecimenPosition pos : containerInfo.container
            .getSpecimenPositionCollection()) {
            specimens.add(new SpecimenWrapper(SessionManager.getAppService(),
                pos.getSpecimen()));
        }
        return specimens;
    }

    List<ContainerTypeWrapper> getChildContainerTypes() {
        return ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(),
            containerInfo.container.getContainerType()
                .getChildContainerTypeCollection(),
            ContainerTypeWrapper.class);

    }

    private void createSpecimensSection() {
        Composite parent =
            createSectionWithClient(Messages.ContainerViewForm_specimens_title);
        specimensWidget =
            new SpecimenInfoTable(parent, getSpecimenWrappers(),
                ColumnsShown.ALL, 20);
        specimensWidget.adaptToToolkit(toolkit, true);
        specimensWidget.addClickListener(collectionDoubleClickListener);
        specimensWidget.createDefaultEditItem();
    }

    @Override
    public void reload() throws Exception {
        if (form.isDisposed()) return;

        updateContainerInfo();

        form.setText(NLS.bind(Messages.ContainerViewForm_title,
            containerInfo.container
                .getLabel(), containerInfo.container.getContainerType()
                .getNameShort()));
        if (containerInfo.container.getContainerType()
            .getChildContainerTypeCollection()
            .size() > 0) refreshVis();
        setContainerValues();
        List<ContainerTypeWrapper> containerTypes = getChildContainerTypes();
        List<Object> deleteComboList = new ArrayList<Object>();
        deleteComboList
            .add(Messages.ContainerViewForm_visualization_all_label);
        deleteComboList.addAll(containerTypes);

        if (initSelectionCv != null) {
            initSelectionCv.setInput(containerTypes);
            initSelectionCv.getCombo().select(0);
        }
        if (deleteCv != null) {
            deleteCv.setInput(deleteComboList);
            deleteCv.getCombo().select(0);
        }

        if (specimensWidget != null) {
            specimensWidget.reloadCollection(getSpecimenWrappers());
        }
        commentTable.setList(
            ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                containerInfo.container.getCommentCollection(),
                CommentWrapper.class));
    }
}
