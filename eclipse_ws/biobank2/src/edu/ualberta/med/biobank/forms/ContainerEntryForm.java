package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.databinding.beans.PojoObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.IntegerNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;
import gov.nih.nci.system.query.SDKQuery;
import gov.nih.nci.system.query.SDKQueryResult;
import gov.nih.nci.system.query.example.InsertExampleQuery;
import gov.nih.nci.system.query.example.UpdateExampleQuery;
import gov.nih.nci.system.query.hibernate.HQLCriteria;

public class ContainerEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerEntryForm";

    public static final String MSG_STORAGE_CONTAINER_NEW_OK = "Creating a new storage container.";

    public static final String MSG_STORAGE_CONTAINER_OK = "Editing an existing storage container.";

    public static final String MSG_CONTAINER_NAME_EMPTY = "Storage container must have a name";

    public static final String MSG_STORAGE_TYPE_EMPTY = "Storage container must have a container type";

    public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

    private ContainerAdapter containerAdapter;

    private Container container;

    private ContainerPosition position;

    private Site site;

    private Text tempWidget;

    private Label dimensionOneLabel;

    private Label dimensionTwoLabel;

    private ContainerType currentContainerType;

    private ComboViewer containerTypeComboViewer;

    @Override
    public void init(IEditorSite editorSite, IEditorInput input)
        throws PartInitException {
        super.init(editorSite, input);

        Node node = ((FormInput) input).getNode();
        Assert.isNotNull(node, "Null editor input");

        containerAdapter = (ContainerAdapter) node;
        appService = containerAdapter.getAppService();
        container = containerAdapter.getContainer();
        site = containerAdapter.getSite();
        position = container.getPosition();

        if (container.getId() == null) {
            setPartName("Container");
        } else {
            setPartName("Container " + container.getName());
        }
    }

    @Override
    protected void createFormContent() {
        currentContainerType = container.getContainerType();

        form.setText("Container");
        form.getBody().setLayout(new GridLayout(1, false));

        createContainerSection();
        createButtonsSection();
    }

    private void createContainerSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Name", null,
            PojoObservables.observeValue(container, "name"),
            NonEmptyString.class, MSG_CONTAINER_NAME_EMPTY);

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Barcode",
            null, PojoObservables.observeValue(container, "barcode"), null,
            null);

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
                .observeValue(container, "activityStatus"), null, null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, PojoObservables.observeValue(
                container, "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);

        createContainerTypesSection(client);
    }

    private void createContainerTypesSection(Composite client) {
        Collection<ContainerType> containerTypes = new ArrayList<ContainerType>();
        if (position.getParentContainer() == null) {
            containerTypes = site.getContainerTypeCollection();
        } else {
            containerTypes = position.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }
        containerTypeComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Container Type", containerTypes, MSG_STORAGE_TYPE_EMPTY);
        if (currentContainerType != null) {
            for (ContainerType type : containerTypes) {
                if (currentContainerType.getId().equals(type.getId())) {
                    currentContainerType = type;
                    break;
                }
            }
            containerTypeComboViewer.setSelection(new StructuredSelection(
                currentContainerType));
        }
        containerTypeComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                    ContainerType containerType = (ContainerType) selection
                        .getFirstElement();
                    Double temp = containerType.getDefaultTemperature();
                    if (temp == null) {
                        tempWidget.setText("");
                    } else {
                        tempWidget.setText(temp.toString());
                    }
                }
            });

        tempWidget = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Temperature (Celcius)", null, PojoObservables
                .observeValue(container, "temperature"), DoubleNumber.class,
            "Default temperature is not a valid number");

        createLocationSection();
    }

    private void createLocationSection() {
        Container parentContainer = position.getParentContainer();
        if (parentContainer != null) {
            String dim1Label = null, dim2Label = null;
            Integer dim1Max = null, dim2Max = null;

            Composite locationComposite = createSectionWithClient("Location");
            dim1Label = parentContainer.getContainerType()
                .getDimensionOneLabel();
            dim2Label = parentContainer.getContainerType()
                .getDimensionTwoLabel();

            Capacity capacity = parentContainer.getContainerType()
                .getCapacity();
            if (capacity != null) {
                dim1Max = capacity.getDimensionOneCapacity();
                dim2Max = capacity.getDimensionTwoCapacity();
                if (dim1Max != null) {
                    dim1Label += "\n(1 - " + dim1Max + ")";
                }
                if (dim2Max != null) {
                    dim2Label += "\n(1 - " + dim2Max + ")";
                }
            }

            // could be that the dimension labels are not assigned in the
            // database objects
            if (dim1Label == null) {
                dim1Label = "Dimension 1";
                dim2Label = "Dimension 2";
            }

            dimensionOneLabel = toolkit.createLabel(locationComposite,
                dim1Label + ":", SWT.LEFT);

            IntegerNumber validator = new IntegerNumber(MSG_INVALID_POSITION,
                FormUtils.createDecorator(dimensionOneLabel,
                    MSG_INVALID_POSITION), false);

            createBoundWidget(locationComposite, Text.class, SWT.NONE, null,
                PojoObservables.observeValue(position, "positionDimensionOne"),
                validator);

            dimensionTwoLabel = toolkit.createLabel(locationComposite,
                dim2Label + ":", SWT.LEFT);

            validator = new IntegerNumber(MSG_INVALID_POSITION, FormUtils
                .createDecorator(dimensionTwoLabel, MSG_INVALID_POSITION),
                false);

            createBoundWidget(locationComposite, Text.class, SWT.NONE, null,
                PojoObservables.observeValue(position, "positionDimensionTwo"),
                validator);
        }
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        initConfirmButton(client, false, true);
    }

    @Override
    protected String getOkMessage() {
        if (container.getId() == null) {
            return MSG_STORAGE_CONTAINER_NEW_OK;
        }
        return MSG_STORAGE_CONTAINER_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        if ((container.getId() == null) && !checkContainerUnique()) {
            setDirty(true);
            return;
        }

        ContainerType containerType = (ContainerType) ((StructuredSelection) containerTypeComboViewer
            .getSelection()).getFirstElement();
        container.setContainerType(containerType);
        container.setPosition(position);
        container.setSite(site);

        SDKQuery query;
        if (container.getId() == null) {
            query = new InsertExampleQuery(container);
        } else {
            query = new UpdateExampleQuery(container);
        }

        SDKQueryResult result = appService.executeQuery(query);
        container = (Container) result.getObjectResult();

        containerAdapter.getParent().performExpand();
        getSite().getPage().closeEditor(this, false);

    }

    // protected void savePosition() throws Exception {
    // if (position != null) {
    // SDKQuery query;
    // SDKQueryResult result;
    //
    // Integer id = position.getId();
    //
    // if ((id == null) || (id == 0)) {
    // query = new InsertExampleQuery(position);
    // } else {
    // query = new UpdateExampleQuery(position);
    // }
    //
    // result = appService.executeQuery(query);
    // container.setPosition((ContainerPosition) result
    // .getObjectResult());
    // }
    // }

    private boolean checkContainerUnique() throws Exception {
        // FIXME set contraint directly into the model ?
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Container as sc "
                + "inner join fetch sc.site " + "where sc.site.id='"
                + site.getId() + "' " + "and (sc.name = '"
                + container.getName() + "' " + "or sc.barcode = '"
                + container.getBarcode() + "')");

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        BioBankPlugin.openAsyncError("Site Name Problem",
            "A storage container with name \"" + container.getName()
                + "\" already exists.");
        return false;
    }

    @Override
    protected void cancelForm() {
        // TODO Auto-generated method stub

    }
}
