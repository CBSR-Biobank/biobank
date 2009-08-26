package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.PartInitException;

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
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.ChooseContainerWidget;
import edu.ualberta.med.biobank.widgets.infotables.SamplesListWidget;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationEvent;
import edu.ualberta.med.biobank.widgets.listener.ScanPalletModificationListener;

public class ContainerViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerViewForm";

    private ContainerAdapter containerAdapter;

    private Container container;

    private ContainerPosition position;

    private SamplesListWidget samplesWidget;

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

    ContainerCell[][] cells;

    private List<ContainerCell> selectedCells;
    private ContainerCell lastSelectedCell;
    private SelectionMode selectionMode = SelectionMode.NONE;
    private boolean selectionTrackOn = false;
    List<ScanPalletModificationListener> listeners;
    private MouseListener selectionMouseListener;
    private MouseTrackListener selectionMouseTrackListener;

    private enum SelectionMode {
        NONE, MULTI, RANGE;
    }

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
        final Button edit = toolkit.createButton(client,
            "Edit this information", SWT.PUSH);
        edit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                getSite().getPage().closeEditor(ContainerViewForm.this, false);
                try {
                    getSite().getPage().openEditor(
                        new FormInput(containerAdapter), ContainerEntryForm.ID,
                        true);
                } catch (PartInitException exp) {
                    exp.printStackTrace();
                }
            }
        });
        if (containerType.getChildContainerTypeCollection().size() > 0) {
            visualizeContainer();
        }
    }

    private void initCells() {
        ContainerType containerType = container.getContainerType();
        Capacity cap = containerType.getCapacity();
        int dim1 = cap.getDimensionOneCapacity().intValue();
        int dim2 = cap.getDimensionTwoCapacity().intValue();
        if (dim1 == 0)
            dim1 = 1;
        if (dim2 == 0)
            dim2 = 1;
        cells = new ContainerCell[dim1][dim2];
        for (int i = 0; i < dim1; i++) {
            for (int j = 0; j < dim2; j++) {
                ContainerPosition pos = new ContainerPosition();
                pos.setPositionDimensionOne(i);
                pos.setPositionDimensionTwo(j);
                ContainerCell cell = new ContainerCell(pos);
                cell.setStatus(ContainerStatus.NOT_INITIALIZED);
                cells[i][j] = cell;
            }
        }
        for (ContainerPosition position : container
            .getChildPositionCollection()) {
            int positionDim1 = position.getPositionDimensionOne().intValue();
            int positionDim2 = position.getPositionDimensionTwo().intValue();
            ContainerCell cell = new ContainerCell(position);
            cell.setStatus(ContainerStatus.INITIALIZED);
            cells[positionDim1][positionDim2] = cell;
        }
    }

    private void addAllCellsInRange(ContainerCell cell) {
        ContainerCell lastSelected = selectedCells
            .get(selectedCells.size() - 1);
        int startRow = lastSelected.getPosition().getPositionDimensionOne();
        int endRow = cell.getPosition().getPositionDimensionOne();
        if (startRow > endRow) {
            startRow = cell.getPosition().getPositionDimensionOne();
            endRow = lastSelected.getPosition().getPositionDimensionOne();
        }
        for (int indexRow = startRow; indexRow <= endRow; indexRow++) {
            int startCol = lastSelected.getPosition().getPositionDimensionTwo();
            int endCol = cell.getPosition().getPositionDimensionTwo();
            if (startCol > endCol) {
                startCol = cell.getPosition().getPositionDimensionTwo();
                endCol = lastSelected.getPosition().getPositionDimensionTwo();
            }
            for (int indexCol = startCol; indexCol <= endCol; indexCol++) {
                ContainerCell cellToAdd = cells[indexRow][indexCol];
                if (cellToAdd != null && cellToAdd.getPosition() != null) {
                    if (!selectedCells.contains(cellToAdd)) {
                        cellToAdd.setStatus(ContainerStatus.FREE_LOCATIONS);
                        selectedCells.add(cellToAdd);
                    }
                }
            }
        }
    }

    public void enableSelection() {
        containerWidget.addMouseListener(selectionMouseListener);
        containerWidget.addMouseTrackListener(selectionMouseTrackListener);
    }

    private void initListeners() {
        selectionMouseListener = new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                selectionTrackOn = true;
                if (cells != null) {
                    ContainerCell cell = ((ChooseContainerWidget) e.widget)
                        .getPositionAtCoordinates(e.x, e.y);
                    if (cell != null && cell.getPosition() != null) {
                        switch (selectionMode) {
                        case MULTI:
                            if (selectedCells.contains(cell)) {
                                selectedCells.remove(cell);
                                cell.setStatus(ContainerStatus.NOT_INITIALIZED);
                            } else {
                                selectedCells.add(cell);
                                cell.setStatus(ContainerStatus.FREE_LOCATIONS);
                            }
                            break;
                        case RANGE:
                            if (selectedCells.size() > 0) {
                                addAllCellsInRange(cell);
                            } else {
                                selectedCells.add(cell);
                                cell.setStatus(ContainerStatus.FREE_LOCATIONS);
                            }
                            break;
                        default:
                            clearSelection();
                            selectedCells.add(cell);
                            cell.setStatus(ContainerStatus.FREE_LOCATIONS);
                            break;
                        }
                        notifyListeners();
                        ((ChooseContainerWidget) e.widget).redraw();
                    }
                }
            }

            @Override
            public void mouseUp(MouseEvent e) {
                selectionTrackOn = false;
            }
        };
        selectionMouseTrackListener = new MouseTrackAdapter() {

            @Override
            public void mouseHover(MouseEvent e) {
                if (selectionTrackOn) {
                    ContainerCell cell = ((ChooseContainerWidget) e.widget)
                        .getPositionAtCoordinates(e.x, e.y);
                    if (cell != null && !cell.equals(lastSelectedCell)) {
                        selectedCells.add(cell);
                        cell.setStatus(ContainerStatus.FREE_LOCATIONS);
                        notifyListeners();
                        ((ChooseContainerWidget) e.widget).redraw();
                    }
                }
            }
        };

        containerWidget.addKeyListener(new KeyListener() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.keyCode == SWT.SHIFT) {
                    selectionMode = SelectionMode.RANGE;
                } else if (e.keyCode == SWT.CTRL) {
                    selectionMode = SelectionMode.MULTI;
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode == SWT.SHIFT || e.keyCode == SWT.CTRL) {
                    selectionMode = SelectionMode.NONE;
                }
            }
        });
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
        int rowHeight = 40, colWidth = 40;
        Composite client = createSectionWithClient("Container Visual");

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
            containerWidget.setParams(container.getContainerType(),
                containerLabelLabel);
            containerWidget.initDefaultLegend();
            selectedCells = new ArrayList<ContainerCell>();
            // initListeners();
            int dim1 = cells.length;
            int dim2 = cells[0].length;
            if (dim2 <= 1) {
                // single dimension size
                rowHeight = 40;
                colWidth = 150;
                containerWidget.setLegendOnSide(true);
            }
            containerWidget.setGridSizes(dim1, dim2, colWidth * dim2, rowHeight
                * dim1);
            containerWidget.setContainersStatus(cells);
            // enableSelection();

            containerWidget.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseDown(MouseEvent e) {
                    ContainerCell cell = ((ChooseContainerWidget) e.widget)
                        .getPositionAtCoordinates(e.x, e.y);
                    openFormFor(cell.getPosition());

                }
            });
        }
    }

    private void openFormFor(ContainerPosition pos) {
        ContainerAdapter newAdapter = null;
        ContainerAdapter.closeEditor(new FormInput(containerAdapter));
        if (cells[pos.getPositionDimensionOne()][pos.getPositionDimensionTwo()]
            .getStatus() == ContainerStatus.NOT_INITIALIZED) {
            Container newContainer = new Container();
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
                if (childPos.getPositionDimensionOne().compareTo(
                    pos.getPositionDimensionOne()) == 0
                    && childPos.getPositionDimensionTwo().compareTo(
                        pos.getPositionDimensionTwo()) == 0) {
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
                FormUtils.setTextValue(positionDimOneLabel, position
                    .getPositionDimensionOne());
            }

            if (positionDimTwoLabel != null) {
                FormUtils.setTextValue(positionDimTwoLabel, position
                    .getPositionDimensionTwo());
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

}