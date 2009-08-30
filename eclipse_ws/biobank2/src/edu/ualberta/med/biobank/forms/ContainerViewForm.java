package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.utils.ModelUtils;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.ChooseContainerWidget;
import edu.ualberta.med.biobank.widgets.infotables.SamplesListWidget;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationEvent;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationListener;
import gov.nih.nci.system.applicationservice.ApplicationException;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.example.DeleteExampleQuery;
import gov.nih.nci.system.query.example.InsertExampleQuery;

public class ContainerViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerViewForm";

    private ContainerAdapter containerAdapter;

    private Container container;

    private ContainerPosition position;

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

    private ChooseContainerWidget containerWidget;

    private ContainerType initType;

    ContainerCell[][] cells;

    private List<ContainerCell> selectedCells;
    List<ScanPalletModificationListener> listeners;
    private MouseListener selectionMouseListener;
    private MouseTrackListener selectionMouseTrackListener;

    private ContainerType deleteType;

    public void addModificationListener(ScanPalletModificationListener listener) {
        listeners.add(listener);
    }

    public void removeModificationListener(
        ScanPalletModificationListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void init() {
        Assert.isTrue(adapter instanceof ContainerAdapter,
            "Invalid editor input: object of type "
                + adapter.getClass().getName());

        containerAdapter = (ContainerAdapter) adapter;
        retrieveContainer();
        position = container.getPosition();
        setPartName(container.getLabel() + " ("
            + container.getContainerType().getName() + ")");
        initCells();
        // List<ScanPalletModificationListener> listeners = new
        // ArrayList<ScanPalletModificationListener>();
    }

    @Override
    protected void createFormContent() {
        form.setText("Container " + container.getLabel() + " ("
            + container.getContainerType().getName() + ")");
        form.getBody().setLayout(new GridLayout(1, false));

        addRefreshToolbarAction();
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
        ContainerType containerType = container.getContainerType();
        initEditButton(client, containerAdapter);

        if (containerType.getChildContainerTypeCollection().size() > 0) {
            visualizeContainer();
        }
    }

    private void initCells() {
        ContainerType containerType = container.getContainerType();
        Capacity cap = containerType.getCapacity();
        int dim1 = cap.getRowCapacity().intValue();
        int dim2 = cap.getColCapacity().intValue();
        if (dim1 == 0)
            dim1 = 1;
        if (dim2 == 0)
            dim2 = 1;
        cells = new ContainerCell[dim1][dim2];
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                ContainerPosition pos = new ContainerPosition();
                pos.setRow(i);
                pos.setCol(j);
                ContainerCell cell = new ContainerCell(pos);
                cell.setStatus(ContainerStatus.NOT_INITIALIZED);
                cells[i][j] = cell;
            }
        }
        for (ContainerPosition position : container
            .getChildPositionCollection()) {
            int positionDim1 = position.getRow().intValue();
            int positionDim2 = position.getCol().intValue();
            ContainerCell cell = new ContainerCell(position);
            cell.setStatus(ContainerStatus.INITIALIZED);
            cells[positionDim1][positionDim2] = cell;
        }
    }

    public void enableSelection() {
        containerWidget.addMouseListener(selectionMouseListener);
        containerWidget.addMouseTrackListener(selectionMouseTrackListener);
    }

    public void clearSelection() {
        for (ContainerCell cell : selectedCells) {
            cell.setStatus(ContainerStatus.NOT_INITIALIZED);
        }
        notifyListeners();
        selectedCells.clear();
    }

    public void notifyListeners(ScanPalletModificationEvent event) {
        if (listeners != null && listeners.size() != 0)
            for (ScanPalletModificationListener listener : listeners) {
                listener.modification(event);
            }
    }

    private void notifyListeners() {
        notifyListeners(new ScanPalletModificationEvent(this, selectedCells
            .size()));
    }

    private void retrieveContainer() {
        try {
            container = ModelUtils.getObjectWithId(appService, Container.class,
                containerAdapter.getContainer().getId());
            containerAdapter.setContainer(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    protected void visualizeContainer() {
        // default 2 dimensional grid
        Composite client = createSectionWithClient("Container Visual");
        GridLayout gl = new GridLayout();
        gl.numColumns = 1;
        client.setLayout(gl);
        // get occupied positions
        if (isContainerDrawer()) {
            // if Drawer, requires special grid
            cabWidget = new CabinetDrawerWidget(client);
            cabWidget.initLegend();
            GridData gdBin = new GridData();
            gdBin.widthHint = CabinetDrawerWidget.WIDTH;
            gdBin.heightHint = CabinetDrawerWidget.HEIGHT
                + CabinetDrawerWidget.LEGEND_HEIGHT;
            gdBin.verticalSpan = 2;
            cabWidget.setLayoutData(gdBin);
            cabWidget.setContainersStatus(container
                .getChildPositionCollection());
            cabWidget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    ContainerCell cell = ((CabinetDrawerWidget) e.widget)
                        .getPositionAtCoordinates(e.x, e.y);
                    openFormFor(cell.getPosition());

                }
            });
        } else {
            // otherwise, normal grid
            containerWidget = new ChooseContainerWidget(client);
            containerWidget.setContainerType(container.getContainerType());
            containerWidget.setParentLabel(containerLabelLabel.getText());
            containerWidget.initDefaultLegend();
            selectedCells = new ArrayList<ContainerCell>();
            // initListeners();
            int dim2 = cells[0].length;
            if (dim2 <= 1) {
                // single dimension size
                containerWidget.setCellWidth(150);
                containerWidget.setCellHeight(20);
                containerWidget.setLegendOnSide(true);
            }
            containerWidget.setContainersStatus(cells);
            // enableSelection();
            containerWidget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    ContainerCell cell = ((ChooseContainerWidget) e.widget)
                        .getPositionAtCoordinates(e.x, e.y);
                    if (cell != null)
                        openFormFor(cell.getPosition());

                }

            });
            containerWidget.addMouseMoveListener(new MouseMoveListener() {

                @Override
                public void mouseMove(MouseEvent e) {
                    Rectangle widgetBounds = containerWidget.getBounds();
                    int safetyPadding = 15;
                    widgetBounds.height -= safetyPadding;
                    widgetBounds.width -= safetyPadding;
                    if (containerWidget.legendOnSide == false)
                        widgetBounds.height -= ChooseContainerWidget.LEGEND_HEIGHT;
                    else
                        widgetBounds.width -= ChooseContainerWidget.LEGEND_WIDTH;

                    if (widgetBounds.width > e.x && widgetBounds.height > e.y)
                        ((ChooseContainerWidget) e.widget)
                            .setCursor(containerWidget.getDisplay()
                                .getSystemCursor(SWT.CURSOR_HAND));
                    else
                        ((ChooseContainerWidget) e.widget)
                            .setCursor(containerWidget.getDisplay()
                                .getSystemCursor(SWT.CURSOR_ARROW));
                }

            });
        }
        Composite multiSection = toolkit.createComposite(client);
        GridLayout gli = new GridLayout();
        gli.numColumns = 3;

        multiSection.setLayout(gli);
        Label comboLabel = new Label(multiSection, SWT.NONE);
        comboLabel.setText("Initialize all available containers to:");
        final Combo init = new Combo(multiSection, SWT.NONE);
        Collection<ContainerType> containerTypes = container.getContainerType()
            .getChildContainerTypeCollection();
        for (ContainerType ct : containerTypes) {
            init.add(ct.getName());
        }
        init.select(0);

        final Button initialize = toolkit.createButton(multiSection,
            "Initialize", SWT.PUSH);
        initialize.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setInitType(init.getItem(init.getSelectionIndex()));
                initContainers();
            }
        });
        Label deleteLabel = new Label(multiSection, SWT.NONE);
        deleteLabel.setText("Delete all initialized containers of type:");
        final Combo delete = new Combo(multiSection, SWT.NONE);
        for (ContainerType ct : containerTypes) {
            delete.add(ct.getName());
        }
        delete.select(0);
        final Button deleteButton = toolkit.createButton(multiSection,
            "Delete", SWT.PUSH);
        deleteButton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                setDeleteType(delete.getItem(delete.getSelectionIndex()));
                deleteContainers();
            }
        });

    }

    protected void setInitType(String name) {
        Collection<ContainerType> containerTypes = container.getContainerType()
            .getChildContainerTypeCollection();
        for (ContainerType ct : containerTypes) {
            if (name.compareTo(ct.getName()) == 0) {
                initType = ct;
                break;
            }
        }
    }

    protected void setDeleteType(String name) {
        Collection<ContainerType> containerTypes = container.getContainerType()
            .getChildContainerTypeCollection();
        for (ContainerType ct : containerTypes) {
            if (name.compareTo(ct.getName()) == 0) {
                deleteType = ct;
                break;
            }
        }
    }

    public void initContainers() {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();
        Collection<ContainerPosition> positions = container
            .getChildPositionCollection();
        int rows = container.getContainerType().getCapacity().getRowCapacity()
            .intValue();
        int cols = container.getContainerType().getCapacity().getColCapacity()
            .intValue();
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                Boolean filled = false;
                for (ContainerPosition pos : positions) {
                    if (pos.getRow().intValue() == i
                        && pos.getCol().intValue() == j)
                        filled = true;
                }
                if (!filled) {
                    Container newContainer = new Container();

                    newContainer.setContainerType(initType);
                    newContainer.setSite(container.getSite());
                    newContainer.setTemperature(container.getTemperature());

                    ContainerPosition newPos = new ContainerPosition();
                    newPos.setRow(new Integer(i));
                    newPos.setCol(new Integer(j));
                    newPos.setParentContainer(container);
                    newContainer.setPosition(newPos);
                    newContainer.setLabel(container.getLabel()
                        + LabelingScheme.getPositionString(newPos));

                    // insert containers/positions to db

                    queries.add(new InsertExampleQuery(newContainer));

                }
            }
        }
        // refresh
        if (queries.size() > 0) {
            try {
                appService.executeBatchQuery(queries);
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
            containerAdapter.performExpand();
            positions = container.getChildPositionCollection();
            reload();
        }
    }

    public void deleteContainers() {
        List<SDKQuery> queries = new ArrayList<SDKQuery>();
        Collection<ContainerPosition> positions = container
            .getChildPositionCollection();
        for (ContainerPosition pos : positions) {
            Container deletingContainer = pos.getContainer();
            if (deletingContainer != null
                && deletingContainer.getContainerType().getId().equals(
                    deleteType.getId())) {
                // insert containers/positions to db

                queries.add(new DeleteExampleQuery(deletingContainer));

            }
        }
        // refresh
        if (queries.size() > 0) {
            try {
                appService.executeBatchQuery(queries);
            } catch (ApplicationException e) {
                e.printStackTrace();
            }
            containerAdapter.removeAll();
            containerAdapter.loadChildren(false);

            containerAdapter.performExpand();
            positions = container.getChildPositionCollection();
            reload();
        }
    }

    private void openFormFor(ContainerPosition pos) {
        ContainerAdapter newAdapter = null;
        ContainerAdapter.closeEditor(new FormInput(containerAdapter));
        if (cells[pos.getRow()][pos.getCol()].getStatus() == ContainerStatus.NOT_INITIALIZED) {
            Container newContainer = new Container();
            newContainer.setSite(containerAdapter.getParentFromClass(
                SiteAdapter.class).getSite());
            pos.setParentContainer(container);
            newContainer.setPosition(pos);
            newAdapter = new ContainerAdapter(containerAdapter, newContainer);
            AdapterBase.openForm(new FormInput(newAdapter),
                ContainerEntryForm.ID);
        } else {
            Container childContainer;
            Collection<ContainerPosition> childPositions = container
                .getChildPositionCollection();
            Assert.isNotNull(childPositions);
            for (ContainerPosition childPos : childPositions) {
                childContainer = childPos.getContainer();
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
    protected void reload() {
        retrieveContainer();
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