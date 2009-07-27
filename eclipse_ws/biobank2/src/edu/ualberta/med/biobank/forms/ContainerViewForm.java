package edu.ualberta.med.biobank.forms;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
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

    private Container parentContainer;

    private SamplesListWidget samplesWidget;

    private Label positionCodeLabel;

    private Label productBarcodeLabel;

    private Label activityStatusLabel;

    private Label commentsLabel;

    private Label containerTypeLabel;

    private Label temperatureLabel;

    private Label positionDimOneLabel = null;

    private Label positionDimTwoLabel;

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
            setPartName("Container " + container.getPositionCode());
            parentContainer = null;
        } else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
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

    @Override
    protected void createFormContent() {
        form.setText("Container " + container.getPositionCode());
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

        positionCodeLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Position Code");
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

        position = container.getPosition();

        if (position != null) {
            parentContainer = position.getParentContainer();
            Assert.isNotNull(parentContainer);
            ContainerType parentContainerType = parentContainer
                .getContainerType();
            String label = parentContainerType.getDimensionOneLabel();
            if ((label != null) && (label.length() > 0)) {
                positionDimOneLabel = (Label) createWidget(client, Label.class,
                    SWT.NONE, label);
            }

            label = parentContainerType.getDimensionTwoLabel();
            if ((label != null) && (label.length() > 0)) {
                positionDimTwoLabel = (Label) createWidget(client, Label.class,
                    SWT.NONE, label);
            }
        }

        setContainerValues();
        ContainerType containerType = container.getContainerType();
        if (containerType.getChildContainerTypeCollection().size() > 0) {
            visualizeContainer();
        }
    }

    protected void visualizeContainer() {
        // default 2 dimensional grid
        int rowHeight = 40, colWidth = 40;
        Composite client = createSectionWithClient("Container Visual");

        ContainerType containerType = container.getContainerType();
        Capacity cap = containerType.getCapacity();
        Integer dim1 = cap.getDimensionOneCapacity();
        Integer dim2 = cap.getDimensionTwoCapacity();
        if (dim1 == null || dim1.intValue() == 0)
            dim1 = new Integer(1);
        if (dim2 == null || dim2.intValue() == 0)
            dim2 = new Integer(1);

        // get occupied positions
        ContainerCell[][] cells = new ContainerCell[dim1][dim2];

        if (containerType.getName().equalsIgnoreCase("Drawer")) {
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
        } else {
            // otherwise, normal grid
            for (ContainerPosition position : container
                .getChildPositionCollection()) {
                int positionDim1 = position.getPositionDimensionOne()
                    .intValue() - 1;
                int positionDim2 = position.getPositionDimensionTwo()
                    .intValue() - 1;
                ContainerCell cell = new ContainerCell(position);
                cell.setStatus(ContainerStatus.FILLED);
                cells[positionDim1][positionDim2] = cell;
            }
            ChooseContainerWidget containerWidget = new ChooseContainerWidget(
                client);
            containerWidget.initLegend();
            if (dim2.compareTo(new Integer(1)) == 0) {
                // single dimension size
                rowHeight = 40;
                colWidth = 150;
                containerWidget.setLegendOnSide(true);
            }
            containerWidget.setGridSizes(dim1, dim2, colWidth * dim2, rowHeight
                * dim1);
            containerWidget.setContainersStatus(cells);
            containerWidget.addMouseListener(new MouseListener() {
                @Override
                public void mouseDoubleClick(MouseEvent e) {
                    Object source = e.getSource();
                    // openForm(new FormInput(adapter),
                    // ContainerTypeEntryForm.ID);

                }

                @Override
                public void mouseDown(MouseEvent e) {
                    // TODO Auto-generated method stub

                }

                @Override
                public void mouseUp(MouseEvent e) {
                    // TODO Auto-generated method stub

                }
            });
        }
    }

    private void setContainerValues() {
        FormUtils.setTextValue(positionCodeLabel, container.getPositionCode());
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
        setPartName("Container " + container.getPositionCode());
        form.setText("Container " + container.getPositionCode());
        setContainerValues();
    }

}
