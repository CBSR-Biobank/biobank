package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PlatformUI;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ContainerPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.internal.CapacityWrapper;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.ContainerDisplayWidget;
import edu.ualberta.med.biobank.widgets.infotables.SamplesListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerViewForm";

    private static Logger LOGGER = Logger.getLogger(ContainerViewForm.class
        .getName());

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container;

    private ContainerPositionWrapper position;

    private SamplesListWidget samplesWidget;

    private Label siteLabel;

    private Label containerLabelLabel;

    private Label productBarcodeLabel;

    private Label activityStatusLabel;

    private Label commentsLabel;

    private Label containerTypeLabel;

    private Label temperatureLabel;

    private Label positionDimOneLabel = null;

    private Label positionDimTwoLabel;

    private CabinetDrawerWidget cabWidget;

    private ContainerDisplayWidget containerWidget;

    private ContainerCell[][] cells;

    private List<ContainerCell> selectedCells;

    @Override
    public void init() throws Exception {
        Assert.isTrue(adapter instanceof ContainerAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerAdapter = (ContainerAdapter) adapter;
        container = containerAdapter.getContainer();
        container.reload();
        position = container.getPosition();
        setPartName(container.getLabel() + " ("
            + container.getContainerType().getName() + ")");
        initCells();
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container " + container.getLabel() + " ("
            + container.getContainerType().getName() + ")");
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

        siteLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Site");
        containerLabelLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Label");
        productBarcodeLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Product Bar Code");
        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentsLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Comments");
        containerTypeLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Container Type");
        temperatureLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Temperature");

        setContainerValues();

        if (container.getContainerType().getChildContainerTypeCollection()
            .size() > 0) {
            createVisualizeContainer();
        }
    }

    private void initCells() {
        ContainerTypeWrapper containerType = container.getContainerType();
        CapacityWrapper cap = containerType.getCapacity();
        Integer rowCap = cap.getRowCapacity();
        Integer colCap = cap.getColCapacity();
        Assert.isNotNull(rowCap, "row capacity is null");
        Assert.isNotNull(colCap, "column capacity is null");
        if (rowCap == 0)
            rowCap = 1;
        if (colCap == 0)
            colCap = 1;
        cells = new ContainerCell[rowCap][colCap];
        for (ContainerPositionWrapper position : container
            .getChildPositionCollection()) {
            Integer row = position.getRow();
            Integer col = position.getCol();
            Assert.isNotNull(row, "row is null");
            Assert.isNotNull(col, "column is null");
            ContainerCell cell = new ContainerCell(position);
            cell.setStatus(ContainerStatus.INITIALIZED);
            cells[row][col] = cell;
        }
        for (int i = 0; i < rowCap; i++) {
            for (int j = 0; j < colCap; j++) {
                if (cells[i][j] == null) {
                    ContainerPositionWrapper pos = new ContainerPositionWrapper(
                        SessionManager.getAppService());
                    pos.setRow(i);
                    pos.setCol(j);
                    ContainerCell cell = new ContainerCell(pos);
                    cell.setStatus(ContainerStatus.NOT_INITIALIZED);
                    cells[i][j] = cell;
                }
            }
        }
    }

    public void clearSelection() {
        for (ContainerCell cell : selectedCells) {
            cell.setStatus(ContainerStatus.NOT_INITIALIZED);
        }
        selectedCells.clear();
    }

    private void refreshVis() {
        if (isContainerDrawer())
            cabWidget.setContainersStatus(container
                .getChildPositionCollection());
        else {
            initCells();
            containerWidget.setContainersStatus(cells);
        }
    }

    public boolean isContainerDrawer() {
        return container.getContainerType().getName().startsWith("Drawer");
    }

    protected void createVisualizeContainer() {
        Composite client = createSectionWithClient("Container Visual");
        client.setLayout(new GridLayout(1, false));

        if (isContainerDrawer()) {
            initCabinetDrawerContainer(client);
        } else {
            initBasicContainer(client);
        }
        addChildrenActions(client);
    }

    private void addChildrenActions(Composite client) {
        Composite multiSection = toolkit.createComposite(client);
        multiSection.setLayout(new GridLayout(3, false));

        List<ContainerTypeWrapper> containerTypes = container
            .getContainerType().getChildContainerTypeCollection();

        // Initialisation action
        final ComboViewer initCv = createComboViewer(multiSection,
            "Initialize all available containers to", containerTypes,
            containerTypes.get(0));
        Button initialize = toolkit.createButton(multiSection, "Initialize",
            SWT.PUSH);
        initialize.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                ContainerTypeWrapper type = (ContainerTypeWrapper) ((IStructuredSelection) initCv
                    .getSelection()).getFirstElement();
                initContainers(type);
            }
        });

        // Delete action
        final ComboViewer deleteCv = createComboViewer(multiSection,
            "Delete all initialized containers of type", containerTypes,
            containerTypes.get(0));
        Button deleteButton = toolkit.createButton(multiSection, "Delete",
            SWT.PUSH);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Boolean confirm = MessageDialog.openConfirm(PlatformUI
                    .getWorkbench().getActiveWorkbenchWindow().getShell(),
                    "Confirm Delete",
                    "Are you sure you want to delete these containers?");
                if (confirm) {
                    ContainerTypeWrapper type = (ContainerTypeWrapper) ((IStructuredSelection) deleteCv
                        .getSelection()).getFirstElement();
                    deleteContainers(type);
                }
            }
        });
    }

    private void initCabinetDrawerContainer(Composite client) {
        // if Drawer, requires special grid
        cabWidget = new CabinetDrawerWidget(client);
        cabWidget.initLegend();
        GridData gdBin = new GridData();
        gdBin.verticalSpan = 2;
        cabWidget.setLayoutData(gdBin);
        cabWidget.setContainersStatus(container.getChildPositionCollection());
        cabWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                ContainerCell cell = ((CabinetDrawerWidget) e.widget)
                    .getPositionAtCoordinates(e.x, e.y);
                openFormFor(cell.getPosition());

            }
        });
    }

    private void initBasicContainer(Composite client) {
        // otherwise, normal grid
        containerWidget = new ContainerDisplayWidget(client);
        containerWidget.setContainerType(container.getContainerType());
        containerWidget.setParentLabel(containerLabelLabel.getText());
        containerWidget.initDefaultLegend();
        selectedCells = new ArrayList<ContainerCell>();
        int dim2 = cells[0].length;
        if (dim2 <= 1) {
            // single dimension size
            containerWidget.setCellWidth(150);
            containerWidget.setCellHeight(20);
            containerWidget.setLegendOnSide(true);
        }
        containerWidget.setContainersStatus(cells);
        containerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                ContainerCell cell = ((ContainerDisplayWidget) e.widget)
                    .getPositionAtCoordinates(e.x, e.y);
                if (cell != null)
                    openFormFor(cell.getPosition());
            }
        });
    }

    private void initContainers(final ContainerTypeWrapper type) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                boolean initDones = false;
                try {
                    initDones = container.initChildrenWithType(type);
                } catch (ApplicationException ae) {
                    BioBankPlugin.openAsyncError(
                        "Error while creating children", ae);
                }
                // refresh
                if (initDones) {
                    PlatformUI.getWorkbench().getDisplay().asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    reload();
                                } catch (Exception e) {
                                    LOGGER.error("Error loading", e);
                                }
                                containerAdapter.performExpand();
                            }
                        });
                }
            }
        });

    }

    private void deleteContainers(final ContainerTypeWrapper type) {
        BusyIndicator.showWhile(Display.getDefault(), new Runnable() {
            public void run() {
                boolean deleteDones = false;
                try {
                    deleteDones = container.deleteChildrenWithType(type);
                } catch (Exception ex) {
                    BioBankPlugin.openAsyncError("Can't Delete Containers", ex);
                }
                if (deleteDones) {
                    PlatformUI.getWorkbench().getDisplay().asyncExec(
                        new Runnable() {
                            public void run() {
                                try {
                                    reload();
                                } catch (Exception e) {
                                    LOGGER.error("Error loading", e);
                                }
                                containerAdapter.rebuild();
                                containerAdapter.performExpand();
                            }
                        });
                }
            }
        });
    }

    private void openFormFor(ContainerPositionWrapper pos) {
        ContainerAdapter newAdapter = null;
        ContainerAdapter.closeEditor(new FormInput(containerAdapter));
        if (cells[pos.getRow()][pos.getCol()].getStatus() == ContainerStatus.NOT_INITIALIZED) {
            ContainerWrapper newContainer = new ContainerWrapper(SessionManager
                .getAppService());
            newContainer.setSite(containerAdapter.getParentFromClass(
                SiteAdapter.class).getWrapper());
            pos.setParentContainer(container);
            newContainer.setPosition(pos);
            newAdapter = new ContainerAdapter(containerAdapter, newContainer);
            AdapterBase.openForm(new FormInput(newAdapter),
                ContainerEntryForm.ID);
        } else {
            Collection<ContainerPositionWrapper> childPositions = container
                .getChildPositionCollection();
            Assert.isNotNull(childPositions);
            for (ContainerPositionWrapper childPos : childPositions) {
                ContainerWrapper childContainer = childPos.getContainer();
                Assert.isNotNull(childContainer);
                if (childPos.getRow().compareTo(pos.getRow()) == 0
                    && childPos.getCol().compareTo(pos.getCol()) == 0) {
                    newAdapter = new ContainerAdapter(containerAdapter,
                        childContainer);
                }
            }
            Assert.isNotNull(newAdapter);
            AdapterBase.openForm(new FormInput(newAdapter),
                ContainerViewForm.ID);
        }
        containerAdapter.performExpand();
    }

    private void setContainerValues() {
        FormUtils.setTextValue(siteLabel, container.getSite().getName());
        FormUtils.setTextValue(containerLabelLabel, container.getLabel());
        FormUtils.setTextValue(productBarcodeLabel, container
            .getProductBarcode());
        FormUtils.setTextValue(activityStatusLabel, container
            .getActivityStatus());
        FormUtils.setTextValue(commentsLabel, container.getComment());
        FormUtils.setTextValue(containerTypeLabel, container.getContainerType()
            .getName());
        FormUtils.setTextValue(temperatureLabel, container.getTemperature());
        if (position != null) {
            if (positionDimOneLabel != null) {
                FormUtils.setTextValue(positionDimOneLabel, position.getRow());
            }

            if (positionDimTwoLabel != null) {
                FormUtils.setTextValue(positionDimTwoLabel, position.getCol());
            }
        }
    }

    private void createSamplesSection() {
        Composite parent = createSectionWithClient("Samples");
        samplesWidget = new SamplesListWidget(parent, container
            .getSamplePositionCollection());
        samplesWidget.adaptToToolkit(toolkit, true);
    }

    @Override
    protected void reload() throws Exception {
        containerAdapter.getContainer().reload();
        form.setText("Container " + container.getLabel() + " ("
            + container.getContainerType().getName() + ")");
        if (container.getContainerType().getChildContainerTypeCollection()
            .size() > 0)
            refreshVis();
        setContainerValues();
    }

    @Override
    protected String getEntryFormId() {
        return ContainerEntryForm.ID;
    }
}
