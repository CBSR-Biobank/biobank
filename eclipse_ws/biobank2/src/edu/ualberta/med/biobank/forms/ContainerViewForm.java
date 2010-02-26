package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Cell;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.grids.AbstractContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.grids.ContainerDisplayFatory;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionEvent;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionListener;
import edu.ualberta.med.biobank.widgets.grids.MultiSelectionSpecificBehaviour;
import edu.ualberta.med.biobank.widgets.infotables.SamplesListInfoTable;

public class ContainerViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerViewForm";

    private static Logger LOGGER = Logger.getLogger(ContainerViewForm.class
        .getName());

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container;

    private SamplesListInfoTable samplesWidget;

    private Text siteLabel;

    private Text containerLabelLabel;

    private Text productBarcodeLabel;

    private Text activityStatusLabel;

    private Text commentsLabel;

    private Text containerTypeLabel;

    private Text temperatureLabel;

    private Text rowLabel = null;

    private Text colLabel;

    private AbstractContainerDisplayWidget containerWidget;

    private Map<RowColPos, ContainerCell> cells;

    private boolean childrenOk = true;

    private Composite childrenActionSection;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerAdapter = (ContainerAdapter) adapter;
        container = containerAdapter.getContainer();
        container.reload();
        setPartName(container.getLabel() + " ("
            + container.getContainerType().getNameShort() + ")");
        initCells();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container " + container.getLabel() + " ("
            + container.getContainerType().getNameShort() + ")");
        form.getBody().setLayout(new GridLayout(1, false));
        form.setImage(BioBankPlugin.getDefault().getIconForTypeName(
            container.getContainerType().getName()));

        createContainerSection();

        if (container.getContainerType().getSampleTypeCollection().size() > 0) {
            // only show samples section this if this container type does not
            // have child containers
            createSamplesSection();
        }
    }

    private void createContainerSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        client.setLayoutData(gridData);
        toolkit.paintBordersFor(client);

        siteLabel = createReadOnlyField(client, SWT.NONE, "Repository Site");
        containerLabelLabel = createReadOnlyField(client, SWT.NONE, "Label");
        productBarcodeLabel = createReadOnlyField(client, SWT.NONE,
            "Product Bar Code");
        activityStatusLabel = createReadOnlyField(client, SWT.NONE,
            "Activity Status");
        commentsLabel = createReadOnlyField(client, SWT.NONE, "Comments");
        containerTypeLabel = createReadOnlyField(client, SWT.NONE,
            "Container Type");
        temperatureLabel = createReadOnlyField(client, SWT.NONE, "Temperature");

        setContainerValues();

        if (container.getContainerType().getChildContainerTypeCollection()
            .size() > 0) {
            createVisualizeContainer();
        }
    }

    private void initCells() {
        try {
            Integer rowCap = container.getRowCapacity();
            Integer colCap = container.getColCapacity();
            Assert.isNotNull(rowCap, "row capacity is null");
            Assert.isNotNull(colCap, "column capacity is null");
            if (rowCap == 0)
                rowCap = 1;
            if (colCap == 0)
                colCap = 1;

            cells = new TreeMap<RowColPos, ContainerCell>();
            Map<RowColPos, ContainerWrapper> childrenMap = container
                .getChildren();
            for (int i = 0; i < rowCap; i++) {
                for (int j = 0; j < colCap; j++) {
                    ContainerCell cell = new ContainerCell(i, j);
                    cells.put(new RowColPos(i, j), cell);
                    ContainerWrapper container = childrenMap.get(new RowColPos(
                        i, j));
                    if (container == null) {
                        cell.setStatus(ContainerStatus.NOT_INITIALIZED);
                    } else {
                        cell.setContainer(container);
                        cell.setStatus(ContainerStatus.INITIALIZED);
                    }
                }
            }
        } catch (Exception ex) {
            BioBankPlugin.openAsyncError("Positions errors",
                "Some child container has wrong position number");
            childrenOk = false;
        }
    }

    private void refreshVis() {
        initCells();
        containerWidget.setCells(cells);
    }

    protected void createVisualizeContainer() {
        Composite client = createSectionWithClient("Container Visual");
        client.setLayout(new GridLayout(1, false));
        if (!childrenOk) {
            Label label = toolkit
                .createLabel(client,
                    "Error in container children : can't display those initialized");
            label.setForeground(Display.getCurrent().getSystemColor(
                SWT.COLOR_RED));
        }
        containerWidget = ContainerDisplayFatory
            .createWidget(client, container);
        containerWidget.initLegend();
        containerWidget.setCells(cells);
        containerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDoubleClick(MouseEvent e) {
                Cell cell = ((AbstractContainerDisplayWidget) e.widget)
                    .getObjectAtCoordinates(e.x, e.y);
                if (cell != null)
                    openFormFor((ContainerCell) cell);
            }
        });
        containerWidget.getMultiSelectionManager().enableMultiSelection(
            new MultiSelectionSpecificBehaviour() {
                @Override
                public void removeSelection(Cell cell) {
                }

                @Override
                public boolean isSelectable(Cell cell) {
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

        createChildrenActionsSection(client);
    }

    private void createChildrenActionsSection(Composite client) {
        childrenActionSection = toolkit.createComposite(client);
        childrenActionSection.setLayout(new GridLayout(3, false));

        List<ContainerTypeWrapper> containerTypes = container
            .getContainerType().getChildContainerTypeCollection();

        // Initialisation action for selection
        final ComboViewer initSelectionCv = createComboViewer(
            childrenActionSection, "Initialize selection to", containerTypes,
            containerTypes.get(0));
        Button initializeSelectionButton = toolkit.createButton(
            childrenActionSection, "Initialize", SWT.PUSH);
        initializeSelectionButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ContainerTypeWrapper type = (ContainerTypeWrapper) ((IStructuredSelection) initSelectionCv
                    .getSelection()).getFirstElement();
                initSelection(type);
            }
        });

        // Delete action for selection
        List<Object> deleteComboList = new ArrayList<Object>();
        deleteComboList.add("All");
        deleteComboList.addAll(containerTypes);
        final ComboViewer deleteCv = createComboViewer(childrenActionSection,
            "Delete selected containers of type", deleteComboList, "All");
        Button deleteButton = toolkit.createButton(childrenActionSection,
            "Delete", SWT.PUSH);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Confirm Delete",
                    "Are you sure you want to delete these containers?");
                if (confirm) {
                    Object selection = ((IStructuredSelection) deleteCv
                        .getSelection()).getFirstElement();
                    if (selection instanceof ContainerTypeWrapper) {
                        deleteSelection((ContainerTypeWrapper) selection);
                    } else {
                        deleteSelection(null);
                    }
                }
            }
        });
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
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                boolean initDone = true;
                try {
                    Set<RowColPos> positions = containerWidget
                        .getMultiSelectionManager().getSelectedPositions();
                    container.initChildrenWithType(type, positions);
                } catch (Exception e) {
                    initDone = false;
                    BioBankPlugin.openAsyncError(
                        "Error while creating children", e);
                }
                refresh(initDone, false);
            }
        });
    }

    private void deleteSelection(final ContainerTypeWrapper type) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                boolean deleteDones = false;
                try {
                    Set<RowColPos> positions = containerWidget
                        .getMultiSelectionManager().getSelectedPositions();
                    deleteDones = container.deleteChildrenWithType(type,
                        positions);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError("Can't Delete Containers", ex);
                }
                refresh(deleteDones, true);
            }
        });
    }

    private void refresh(boolean initDone, final boolean rebuild) {
        if (initDone) {
            PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
                public void run() {
                    try {
                        reload();
                    } catch (Exception e) {
                        LOGGER.error("Error loading", e);
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
        if (cell.getStatus() == ContainerStatus.NOT_INITIALIZED) {
            ContainerWrapper containerToOpen = cell.getContainer();
            if (containerToOpen == null) {
                containerToOpen = new ContainerWrapper(appService);
            }
            containerToOpen.setSite(containerAdapter.getParentFromClass(
                SiteAdapter.class).getWrapper());
            containerToOpen.setParent(container);
            containerToOpen.setPosition(new RowColPos(cell.getRow(), cell
                .getCol()));
            newAdapter = new ContainerAdapter(containerAdapter, containerToOpen);
            AdapterBase.openForm(new FormInput(newAdapter),
                ContainerEntryForm.ID);
        } else {
            ContainerWrapper child = cell.getContainer();
            Assert.isNotNull(child);
            SessionManager.openViewForm(child);
        }
        containerAdapter.performExpand();
    }

    private void setContainerValues() {
        setTextValue(siteLabel, container.getSite().getName());
        setTextValue(containerLabelLabel, container.getLabel());
        setTextValue(productBarcodeLabel, container.getProductBarcode());
        setTextValue(activityStatusLabel, container.getActivityStatus());
        setTextValue(commentsLabel, container.getComment());
        setTextValue(containerTypeLabel, container.getContainerType().getName());
        setTextValue(temperatureLabel, container.getTemperature());
        if (container.hasParent()) {
            if (rowLabel != null) {
                setTextValue(rowLabel, container.getPosition().row);
            }

            if (colLabel != null) {
                setTextValue(colLabel, container.getPosition().col);
            }
        }
    }

    private void createSamplesSection() {
        Composite parent = createSectionWithClient("Samples");
        List<SampleWrapper> samples = new ArrayList<SampleWrapper>(container
            .getSamples().values());
        samplesWidget = new SamplesListInfoTable(parent, samples);
        samplesWidget.adaptToToolkit(toolkit, true);
        samplesWidget.addDoubleClickListener(collectionDoubleClickListener);
    }

    @Override
    protected void reload() throws Exception {
        if (!form.isDisposed()) {
            container.reload();
            form.setText("Container " + container.getLabel() + " ("
                + container.getContainerType().getName() + ")");
            if (container.getContainerType().getChildContainerTypeCollection()
                .size() > 0)
                refreshVis();
            setContainerValues();
        }
    }

    @Override
    protected String getEntryFormId() {
        return ContainerEntryForm.ID;
    }

}
