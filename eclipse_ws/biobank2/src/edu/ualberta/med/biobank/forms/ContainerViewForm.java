package edu.ualberta.med.biobank.forms;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ModelUtils;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.widgets.CabinetDrawerWidget;
import edu.ualberta.med.biobank.widgets.ChooseContainerWidget;
import edu.ualberta.med.biobank.widgets.SamplesListWidget;

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

    ContainerCell[][] cells;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        if (node instanceof ContainerAdapter) {
            containerAdapter = (ContainerAdapter) node;
            appService = containerAdapter.getAppService();
            retrieveContainer();
            position = container.getPosition();
            setPartName(container.getLabel() + " ("
                + container.getContainerType().getName() + ")");
            initCells();
        } else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Container " + container.getLabel() + " ("
            + container.getContainerType().getName() + ")");
        form.getBody().setLayout(new GridLayout(1, false));

        addRefreshToolbarAction();
        createContainerSection();

        if (container.getContainerType().getChildContainerTypeCollection()
            .size() == 0) {
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
                cell.setStatus(ContainerStatus.EMPTY);
                cells[i][j] = cell;
            }
        }
        for (ContainerPosition position : container
            .getChildPositionCollection()) {
            int positionDim1 = position.getPositionDimensionOne().intValue();
            int positionDim2 = position.getPositionDimensionTwo().intValue();
            ContainerCell cell = new ContainerCell(position);
            cell.setStatus(ContainerStatus.FILLED);
            cells[positionDim1][positionDim2] = cell;
        }
    }

    private void retrieveContainer() {
        try {
            container = (Container) ModelUtils.getObjectWithId(appService,
                Container.class, containerAdapter.getContainer().getId());
            containerAdapter.setContainer(container);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void visualizeContainer() {
        // default 2 dimensional grid
        int rowHeight = 40, colWidth = 40;
        Composite client = createSectionWithClient("Container Visual");

        // get occupied positions

        if (container.getContainerType().getName().equalsIgnoreCase("Drawer")) {
            // if Drawer, requires special grid
            CabinetDrawerWidget containerWidget = new CabinetDrawerWidget(
                client);
            containerWidget.initLegend();
            GridData gdBin = new GridData();
            gdBin.widthHint = CabinetDrawerWidget.WIDTH;
            gdBin.heightHint = CabinetDrawerWidget.HEIGHT
                + CabinetDrawerWidget.LEGEND_HEIGHT;
            gdBin.verticalSpan = 2;
            containerWidget.setLayoutData(gdBin);
            containerWidget.setContainersStatus(container
                .getChildPositionCollection());
            containerWidget.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseDown(MouseEvent e) {
                    ContainerCell cell = ((CabinetDrawerWidget) e.widget)
                        .getPositionAtCoordinates(e.x, e.y);
                    openFormFor(cell.getPosition());

                }
            });
        } else {
            // otherwise, normal grid

            ChooseContainerWidget containerWidget = new ChooseContainerWidget(
                client);
            containerWidget.setParams(container.getContainerType(),
                containerLabelLabel);
            containerWidget.initDefaultLegend();
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

        if (cells[pos.getPositionDimensionOne()][pos.getPositionDimensionTwo()]
            .getStatus() == ContainerStatus.EMPTY) {
            Container newContainer = new Container();
            pos.setParentContainer(container);
            newContainer.setPosition(pos);
            newAdapter = new ContainerAdapter(containerAdapter, newContainer);
            Node.openForm(new FormInput(newAdapter), ContainerEntryForm.ID);
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
            Node.openForm(new FormInput(newAdapter), ContainerViewForm.ID);
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
        samplesWidget = new SamplesListWidget(parent, null);
        samplesWidget.adaptToToolkit(toolkit, true);
        samplesWidget.setSamplePositions(container
            .getSamplePositionCollection());
    }

    @Override
    protected void reload() {
        retrieveContainer();
        setPartName("Container " + container.getLabel());
        form.setText("Container " + container.getLabel());
        setContainerValues();
    }

}