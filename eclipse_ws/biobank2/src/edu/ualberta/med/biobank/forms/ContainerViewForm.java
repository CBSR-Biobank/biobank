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
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.BooleanResult;
import edu.ualberta.med.biobank.common.action.container.ContainerCreateChildrenAction;
import edu.ualberta.med.biobank.common.action.container.ContainerDeleteChildrenAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerGetSpecimenListInfoAction;
import edu.ualberta.med.biobank.common.action.specimen.SpecimenInfo;
import edu.ualberta.med.biobank.common.permission.container.ContainerCreatePermission;
import edu.ualberta.med.biobank.common.permission.container.ContainerDeletePermission;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableDoubleClickItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.IInfoTableEditItemListener;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableEvent;
import edu.ualberta.med.biobank.gui.common.widgets.InfoTableSelection;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.model.Specimen;
import edu.ualberta.med.biobank.model.util.RowColPos;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.SpecimenAdapter;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.selection.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.grids.well.AbstractUIWell;
import edu.ualberta.med.biobank.widgets.grids.well.ContainerCell;
import edu.ualberta.med.biobank.widgets.grids.well.UICellStatus;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable;
import edu.ualberta.med.biobank.widgets.infotables.NewSpecimenInfoTable.ColumnsShown;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerViewForm extends BiobankViewForm {
    private static final I18n i18n = I18nFactory
        .getI18n(ContainerViewForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerViewForm";

    private static BgcLogger logger = BgcLogger
        .getLogger(ContainerViewForm.class.getName());

    private NewSpecimenInfoTable specimensWidget;

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

    private CommentsInfoTable commentTable;

    private ContainerInfo containerInfo;

    private ContainerAdapter containerAdapter;

    private ArrayList<SpecimenInfo> specInfo;

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerAdapter = (ContainerAdapter) adapter;
        updateContainerInfo();

        setPartName(i18n.tr("Container {0} ({1})",
            containerInfo.container.getLabel(), containerInfo.container
                .getContainerType().getNameShort()));
        initCells();
        canCreate = SessionManager.getAppService().isAllowed(
            new ContainerCreatePermission(((ContainerWrapper) containerAdapter
                .getModelObject()).getSite().getId()));
        canDelete = SessionManager.getAppService().isAllowed(new
            ContainerDeletePermission());
    }

    private void updateContainerInfo() throws ApplicationException {
        Assert.isNotNull(adapter.getId());
        containerInfo = SessionManager.getAppService().doAction(
            new ContainerGetInfoAction(adapter.getId()));
        specInfo = SessionManager.getAppService().doAction(
            new ContainerGetSpecimenListInfoAction(
                containerInfo.container.getId())).getList();
        Assert.isNotNull(containerInfo);
        Assert.isNotNull(containerInfo.container);
    }

    @SuppressWarnings("nls")
    @Override
    protected void createFormContent() throws Exception {
        form.setText(i18n.tr("Container {0} ({1})",
            containerInfo.container.getLabel(), containerInfo.container
                .getContainerType().getNameShort()));
        page.setLayout(new GridLayout(1, false));

        createContainerSection();

        if (containerInfo.container.getContainerType()
            .getSpecimenTypes().size() > 0) {
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
                Site.NAME.singular().toString());
        containerLabelLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Container.PropertyName.LABEL.toString());
        productBarcodeLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Container.PropertyName.PRODUCT_BARCODE.toString());
        activityStatusLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                ActivityStatus.NAME.singular().toString());
        containerTypeLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                ContainerType.NAME.singular().toString());
        temperatureLabel =
            createReadOnlyLabelledField(client, SWT.NONE,
                Container.PropertyName.TEMPERATURE.toString());

        createCommentsSection();

        setContainerValues();

        if (containerInfo.container.getContainerType()
            .getChildContainerTypes().size() > 0) {
            createVisualizeContainer();
        }
    }

    private void createCommentsSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.plural().toString());
        commentTable =
            new CommentsInfoTable(client,
                ModelWrapper.wrapModelCollection(
                    SessionManager.getAppService(),
                    containerInfo.container.getComments(),
                    CommentWrapper.class));
        commentTable.adaptToToolkit(toolkit, true);
        toolkit.paintBordersFor(commentTable);
    }

    @SuppressWarnings("nls")
    private void initCells() {
        try {
            if (containerInfo.container.getContainerType()
                .getChildContainerTypes().isEmpty()) return;

            Integer rowCap =
                containerInfo.container.getContainerType().getCapacity()
                    .getRowCapacity();
            Integer colCap =
                containerInfo.container.getContainerType().getCapacity()
                    .getColCapacity();
            Assert.isNotNull(rowCap, "row capacity is null");
            Assert.isNotNull(colCap, "column capacity is null");
            if (rowCap == 0) rowCap = 1;
            if (colCap == 0) colCap = 1;

            cells = new TreeMap<RowColPos, ContainerCell>();
            Map<RowColPos, ContainerWrapper> childrenMap =
                new HashMap<RowColPos, ContainerWrapper>();
            for (ContainerPosition position : containerInfo.container
                .getChildPositions()) {
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
                i18n.tr("Positions errors"),
                i18n.tr("Some child container has wrong position number"));
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
        @SuppressWarnings("nls")
        Section section = createSection(
            // section label.
            i18n.tr("Container Visual"));
        section.setLayout(new GridLayout(1, false));
        Composite containerSection = new Composite(section, SWT.NONE);
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
        section.setClient(containerSection);
        if (!childrenOk) {
            @SuppressWarnings("nls")
            Label label =
                toolkit
                    .createLabel(
                        client,
                        i18n.tr("Error in container children : can't display those initialized"));
            label.setForeground(Display.getCurrent().getSystemColor(
                SWT.COLOR_RED));
        }
        containerWidget =
            new ContainerDisplayWidget(client,
                UICellStatus.DEFAULT_CONTAINER_STATUS_LIST);
        containerWidget.setContainer(containerInfo.container);
        containerWidget.setCells(cells);
        toolkit.adapt(containerWidget);

        // Set the minimum size
        sc.setMinSize(containerWidget.computeSize(SWT.DEFAULT, SWT.DEFAULT));

        containerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                AbstractUIWell cell =
                    ((ContainerDisplayWidget) e.widget).getObjectAtCoordinates(
                        e.x, e.y);
                if (cell != null) openFormFor((ContainerCell) cell);
            }
        });
        containerWidget.getMultiSelectionManager().enableMultiSelection(
            new MultiSelectionSpecificBehaviour() {
                @Override
                public void removeSelection(AbstractUIWell cell) {
                    //
                }

                @Override
                public boolean isSelectable(AbstractUIWell cell) {
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

    @SuppressWarnings("nls")
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
                        i18n.tr("Initialize selection to"),
                        containerTypes, containerTypes.get(0));
                initSelectionCv.getCombo()
                    .setLayoutData(new GridData(SWT.LEFT));
                Button initializeSelectionButton =
                    toolkit
                        .createButton(
                            childrenActionSection,
                            i18n.tr("Initialize"),
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
                    .add("All");
                deleteComboList.addAll(containerTypes);
                deleteCv =
                    createComboViewer(
                        childrenActionSection,
                        i18n.tr("Delete selected containers of type"),
                        deleteComboList,
                        i18n.tr("All"));
                deleteCv.getCombo().setLayoutData(new GridData(SWT.LEFT));
                Button deleteButton =
                    toolkit
                        .createButton(
                            childrenActionSection,
                            i18n.tr("Delete"),
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
                                    i18n.tr("Confirm Delete"),
                                    i18n.tr("Are you sure you want to delete these containers?"));
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

    @SuppressWarnings("nls")
    private void initSelection(final ContainerTypeWrapper type) {
        IRunnableContext context =
            new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {
            context.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor
                        .beginTask(
                            // progress monitor message.
                            i18n.tr("Initializing..."),
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
                                i18n.tr("Error while creating children"),
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
                // dialog title.
                i18n.tr("Error while creating children"), e);
            refresh(false, false);
        }
    }

    public boolean initChildrenWithType(ContainerTypeWrapper type,
        Set<RowColPos> positions) throws Exception {
        ContainerCreateChildrenAction containerCreateChildrenAction =
            new ContainerCreateChildrenAction(containerInfo.container.getSite());
        containerCreateChildrenAction
            .setParentContainerId(containerInfo.container.getId());
        containerCreateChildrenAction.setContainerTypeId(type.getId());
        containerCreateChildrenAction.setParentPositions(positions);
        BooleanResult result =
            SessionManager.getAppService().doAction(
                containerCreateChildrenAction);
        return result.isTrue();
    }

    @SuppressWarnings("nls")
    private void deleteSelection(final ContainerTypeWrapper type) {
        IRunnableContext context =
            new ProgressMonitorDialog(Display.getDefault().getActiveShell());
        try {
            context.run(true, false, new IRunnableWithProgress() {
                @Override
                public void run(final IProgressMonitor monitor) {
                    monitor
                        .beginTask(
                            // progress monitor message.
                            i18n.tr("Deleting..."),
                            IProgressMonitor.UNKNOWN);
                    boolean deleteDones = false;
                    try {

                        Set<RowColPos> positions =
                            containerWidget.getMultiSelectionManager()
                                .getSelectedPositions();
                        deleteDones =
                            deleteChildrenWithType(type, positions);
                    } catch (Exception ex) {
                        BgcPlugin
                            .openAsyncError(
                                // dialog title.
                                i18n.tr("Can't Delete Containers"),
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
                // dialog title.
                i18n.tr("Can't Delete Containers"), e);
            refresh(false, false);
        }
    }

    protected boolean deleteChildrenWithType(ContainerTypeWrapper type,
        Set<RowColPos> positions) throws Exception {
        ContainerDeleteChildrenAction containerDeleteChildrenAction =
            new ContainerDeleteChildrenAction();
        containerDeleteChildrenAction
            .setParentContainerId(containerInfo.container.getId());
        if (type != null) {
            containerDeleteChildrenAction.setContainerTypeId(type.getId());
        }
        containerDeleteChildrenAction.setParentPositions(positions);
        BooleanResult result =
            SessionManager.getAppService().doAction(
                containerDeleteChildrenAction);
        return result.isTrue();
    }

    private void refresh(boolean initDone, final boolean rebuild) {
        if (initDone) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                @SuppressWarnings("nls")
                @Override
                public void run() {
                    try {
                        reload();
                    } catch (Exception e) {
                        logger.error(
                            "Error loading", e);
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
        ContainerAdapter newAdapter = null;
        if (cell.getStatus() == UICellStatus.NOT_INITIALIZED) {
            if (canCreate) {
                ContainerWrapper containerToOpen = cell.getContainer();
                if (containerToOpen == null) {
                    containerToOpen =
                        new ContainerWrapper(SessionManager.getAppService());
                }
                containerToOpen.setSite(new SiteWrapper(SessionManager
                    .getAppService(), containerInfo.container.getSite()));
                RowColPos pos = new RowColPos(cell.getRow(), cell.getCol());
                containerToOpen.setParentInternal(
                    new ContainerWrapper(SessionManager.getAppService(),
                        containerInfo.container), pos);
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
    }

    private void setContainerValues() {
        setTextValue(siteLabel, containerInfo.container.getSite().getName());
        setTextValue(containerLabelLabel, containerInfo.container.getLabel());
        setTextValue(productBarcodeLabel,
            containerInfo.container.getProductBarcode());
        setTextValue(activityStatusLabel,
            containerInfo.container.getActivityStatus().getName());
        setTextValue(containerTypeLabel, containerInfo.container
            .getContainerType().getName());
        setTextValue(temperatureLabel, containerInfo.container
            .getTopContainer().getTemperature());
    }

    List<ContainerTypeWrapper> getChildContainerTypes() {
        return ModelWrapper.wrapModelCollection(
            SessionManager.getAppService(),
            containerInfo.container.getContainerType()
                .getChildContainerTypes(),
            ContainerTypeWrapper.class);

    }

    private void createSpecimensSection() {
        Composite parent = createSectionWithClient(Specimen.NAME.plural().toString());
        specimensWidget = new NewSpecimenInfoTable(parent, specInfo,
            ColumnsShown.CEVENT_SOURCE_SPECIMENS, 20);
        specimensWidget.adaptToToolkit(toolkit, true);
        specimensWidget
            .addClickListener(new IInfoTableDoubleClickItemListener<SpecimenInfo>() {

                @Override
                public void doubleClick(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenViewForm.ID);
                }
            });
        specimensWidget
            .addEditItemListener(new IInfoTableEditItemListener<SpecimenInfo>() {
                @Override
                public void editItem(InfoTableEvent<SpecimenInfo> event) {
                    Specimen s =
                        ((SpecimenInfo) ((InfoTableSelection) event
                            .getSelection()).getObject()).specimen;
                    AdapterBase.openForm(
                        new FormInput(
                            new SpecimenAdapter(null,
                                new SpecimenWrapper(SessionManager
                                    .getAppService(), s))),
                        SpecimenEntryForm.ID);
                }
            });
    }

    @SuppressWarnings("nls")
    @Override
    public void setValues() throws Exception {
        if (form.isDisposed()) return;

        form.setText(i18n.tr("Container {0} ({1})",
            containerInfo.container
                .getLabel(), containerInfo.container.getContainerType()
                .getNameShort()));
        if (containerInfo.container.getContainerType()
            .getChildContainerTypes()
            .size() > 0) refreshVis();
        setContainerValues();
        List<ContainerTypeWrapper> containerTypes = getChildContainerTypes();
        List<Object> deleteComboList = new ArrayList<Object>();
        deleteComboList
            .add(i18n.tr("All"));
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
            specimensWidget.setList(specInfo);
        }
        commentTable.setList(
            ModelWrapper.wrapModelCollection(
                SessionManager.getAppService(),
                containerInfo.container.getComments(),
                CommentWrapper.class));
    }
}
