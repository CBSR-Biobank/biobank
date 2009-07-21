package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.SWT;
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
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.widgets.ChooseContainerWidget;
import edu.ualberta.med.biobank.widgets.SamplesListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerViewForm";

    private ContainerAdapter containerAdapter;

    private Container container;

    private SamplesListWidget samplesWidget;

    private Label nameLabel;

    private Label barCodeLabel;

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
            setPartName("Container " + container.getName());
        } else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }
    }

    private void retrieveContainer() {
        List<Container> result;
        Container searchContainer = new Container();
        searchContainer.setId(containerAdapter.getContainer().getId());
        try {
            result = appService.search(Container.class, searchContainer);
            Assert.isTrue(result.size() == 1);
            container = result.get(0);
            containerAdapter.setContainer(container);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Container " + container.getName());
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
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        nameLabel = (Label) createWidget(client, Label.class, SWT.NONE, "Name");
        barCodeLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Bar Code");
        activityStatusLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Activity Status");
        commentsLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Comments");
        containerTypeLabel = (Label) createWidget(client, Label.class,
            SWT.NONE, "Container Type");
        temperatureLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Temperature");

        ContainerType containerType = container.getContainerType();
        String label = containerType.getDimensionOneLabel();
        if ((label != null) && (label.length() > 0)) {
            positionDimOneLabel = (Label) createWidget(client, Label.class,
                SWT.NONE, label);
        }

        label = containerType.getDimensionTwoLabel();
        if ((label != null) && (label.length() > 0)) {
            positionDimTwoLabel = (Label) createWidget(client, Label.class,
                SWT.NONE, label);
        }

        setContainerValues();

        ChooseContainerWidget containerWidget = new ChooseContainerWidget(
            client);
        Capacity cap = containerType.getCapacity();
        ContainerCell[][] cells = new ContainerCell[cap
            .getDimensionOneCapacity()][cap.getDimensionTwoCapacity()];
        for (ContainerPosition position : container
            .getChildPositionCollection()) {
            int positionDim1 = position.getPositionDimensionOne() - 1;
            int positionDim2 = position.getPositionDimensionTwo() - 1;
            ContainerCell cell = new ContainerCell(position);
            Container occupiedContainer = position.getContainer();
            setStatus(cell, occupiedContainer);
            cells[positionDim1][positionDim2] = cell;
        }
        containerWidget.setContainersStatus(cells);

    }

    protected void setStatus(ContainerCell cell, Container occupiedContainer) {
        Boolean full = occupiedContainer.getFull();
        if (full == null) {
            full = Boolean.FALSE;
        }
        int total = 0;
        if (!full) {
            // check if we can add a palette in the hotel
            if (occupiedContainer.getChildPositionCollection() != null) {
                total = occupiedContainer.getChildPositionCollection().size();
            }
            int capacityTotal = occupiedContainer.getContainerType()
                .getCapacity().getDimensionOneCapacity()
                * occupiedContainer.getContainerType().getCapacity()
                    .getDimensionTwoCapacity();
            full = (total == capacityTotal);
        }
        if (full) {
            cell.setStatus(ContainerStatus.FILLED);
        } else if (total == 0) {
            cell.setStatus(ContainerStatus.EMPTY);
        } else {
            cell.setStatus(ContainerStatus.FREE_POSITIONS);
        }
    }

    private void setContainerValues() {
        FormUtils.setTextValue(nameLabel, container.getName());
        FormUtils.setTextValue(barCodeLabel, container.getBarcode());
        FormUtils.setTextValue(activityStatusLabel, container
            .getActivityStatus());
        FormUtils.setTextValue(commentsLabel, container.getComment());
        FormUtils.setTextValue(containerTypeLabel, container.getContainerType()
            .getName());
        FormUtils.setTextValue(temperatureLabel, container.getTemperature());
        ContainerPosition position = container.getPosition();
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
        samplesWidget.setSamplesFromPositions(container
            .getSamplePositionCollection());
    }

    @Override
    protected void reload() {
        retrieveContainer();
        setPartName("Container " + container.getName());
        form.setText("Container " + container.getName());
        setContainerValues();
    }

}
