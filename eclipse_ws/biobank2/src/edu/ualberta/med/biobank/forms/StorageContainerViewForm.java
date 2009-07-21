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
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.StorageContainerAdapter;
import edu.ualberta.med.biobank.widgets.SamplesListWidget;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class StorageContainerViewForm extends BiobankViewForm {

    public static final String ID = "edu.ualberta.med.biobank.forms.StorageContainerViewForm";

    private StorageContainerAdapter storageContainerAdapter;

    private StorageContainer storageContainer;

    private SamplesListWidget samplesWidget;

    private Label nameLabel;

    private Label barCodeLabel;

    private Label activityStatusLabel;

    private Label commentsLabel;

    private Label storageTypeLabel;

    private Label temperatureLabel;

    private Label positionDimOneLabel = null;

    private Label positionDimTwoLabel;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        if (node instanceof StorageContainerAdapter) {
            storageContainerAdapter = (StorageContainerAdapter) node;
            appService = storageContainerAdapter.getAppService();
            retrieveStorageContainer();
            setPartName("Storage Container " + storageContainer.getName());
        } else {
            Assert.isTrue(false, "Invalid editor input: object of type "
                + node.getClass().getName());
        }
    }

    private void retrieveStorageContainer() {
        List<StorageContainer> result;
        StorageContainer searchStorageContainer = new StorageContainer();
        searchStorageContainer.setId(storageContainerAdapter
            .getStorageContainer().getId());
        try {
            result = appService.search(StorageContainer.class,
                searchStorageContainer);
            Assert.isTrue(result.size() == 1);
            storageContainer = result.get(0);
            storageContainerAdapter.setStorageContainer(storageContainer);
        } catch (ApplicationException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void createFormContent() {
        form.setText("Storage Container " + storageContainer.getName());
        form.getBody().setLayout(new GridLayout(1, false));

        addRefreshToolbarAction();
        createContainerSection();

        if (storageContainer.getStorageType().getChildStorageTypeCollection()
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
        storageTypeLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Storage Type");
        temperatureLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Temperature");

        StorageType storageType = storageContainer.getStorageType();
        String label = storageType.getDimensionOneLabel();
        if ((label != null) && (label.length() > 0)) {
            positionDimOneLabel = (Label) createWidget(client, Label.class,
                SWT.NONE, label);
        }

        label = storageType.getDimensionTwoLabel();
        if ((label != null) && (label.length() > 0)) {
            positionDimTwoLabel = (Label) createWidget(client, Label.class,
                SWT.NONE, label);
        }

        setContainerValues();
        /*
         * ViewStorageContainerWidget containerWidget = new
         * ViewStorageContainerWidget( client); ContainerCell[][] cells =
         * initGridSize(); if (storageContainer != null) { // get cell
         * information for (ContainerPosition position : storageContainer
         * .getOccupiedPositions()) { int positionDim1 =
         * position.getPositionDimensionOne() - 1; int positionDim2 =
         * position.getPositionDimensionTwo() - 1; ContainerCell cell = new
         * ContainerCell(position); StorageContainer occupiedContainer =
         * position .getOccupiedContainer();
         * containerWidget.setContainersStatus(cells); } }
         */
    }

    private void setContainerValues() {
        FormUtils.setTextValue(nameLabel, storageContainer.getName());
        FormUtils.setTextValue(barCodeLabel, storageContainer.getBarcode());
        FormUtils.setTextValue(activityStatusLabel, storageContainer
            .getActivityStatus());
        FormUtils.setTextValue(commentsLabel, storageContainer.getComment());
        FormUtils.setTextValue(storageTypeLabel, storageContainer
            .getStorageType().getName());
        FormUtils.setTextValue(temperatureLabel, storageContainer
            .getTemperature());
        ContainerPosition position = storageContainer.getLocatedAtPosition();
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
        samplesWidget.setSamplesFromPositions(storageContainer
            .getSamplePositionCollection());
    }

    @Override
    protected void reload() {
        retrieveStorageContainer();
        setPartName("Storage Container " + storageContainer.getName());
        form.setText("Storage Container " + storageContainer.getName());
        setContainerValues();
    }

}
