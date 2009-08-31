package edu.ualberta.med.biobank.forms;

import java.util.Arrays;
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

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.utils.SiteUtils;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
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

    public static final String MSG_CONTAINER_NAME_EMPTY = "Container must have a name";

    public static final String MSG_CONTAINER_TYPE_EMPTY = "Container must have a container type";

    public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

    private ContainerAdapter containerAdapter;

    private Container container;

    private ContainerPosition position;

    private Site site;

    private Text tempWidget;

    private ContainerType currentContainerType;

    private ComboViewer containerTypeComboViewer;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;
        container = containerAdapter.getContainer();
        site = containerAdapter.getParentFromClass(SiteAdapter.class).getSite();
        position = container.getPosition();

        if (position != null) {
            container.setLabel(position.getParentContainer().getLabel()
                + LabelingScheme.getPositionString(position));
            container.setTemperature(position.getParentContainer()
                .getTemperature());
        }

        String tabName;
        if (container.getId() == null) {
            tabName = "Container";
        } else {
            tabName = "Container " + container.getLabel();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() {
        form.setText("Container");
        currentContainerType = container.getContainerType();
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

        Label siteLabel = (Label) createWidget(client, Label.class, SWT.NONE,
            "Site");
        FormUtils.setTextValue(siteLabel, container.getSite().getName());

        if (position == null) {
            // only allow edit to label on top level containers
            createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Label",
                null, PojoObservables.observeValue(container, "label"),
                NonEmptyString.class, MSG_CONTAINER_NAME_EMPTY);
        } else {
            Label l = (Label) createWidget(client, Label.class, SWT.NONE,
                "Label");
            FormUtils.setTextValue(l, container.getLabel());
        }

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Product Barcode", null, PojoObservables.observeValue(container,
                "productBarcode"), null, null);

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
        Collection<ContainerType> containerTypes;
        if ((position == null) || (position.getParentContainer() == null)) {
            containerTypes = SiteUtils.getTopContainerTypesInSite(appService,
                site);
        } else {
            containerTypes = position.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }

        ContainerType selection = null;
        if ((container.getContainerType() == null)
            && (containerTypes.size() == 1)) {
            selection = containerTypes.iterator().next();
            setDirty(true);
        }
        containerTypeComboViewer = createCComboViewerWithNoSelectionValidator(
            client, "Container Type", containerTypes, selection,
            MSG_CONTAINER_TYPE_EMPTY);

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
                    if (containerType.getTopLevel()) {
                        Double temp = containerType.getDefaultTemperature();

                        if (temp == null) {
                            tempWidget.setText("");
                        } else {
                            tempWidget.setText(temp.toString());
                        }
                    }

                }
            });

        tempWidget = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.NONE, "Temperature (Celcius)", null, PojoObservables
                .observeValue(container, "temperature"), DoubleNumber.class,
            "Default temperature is not a valid number");
        if (container.getPosition() != null)
            tempWidget.setEnabled(false);

    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);

        initCancelConfirmWidget(client);
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
        if (position != null) {
            container.setPosition(position);
        }
        container.setSite(site);

        SDKQuery query;
        if (container.getId() == null) {
            query = new InsertExampleQuery(container);
        } else {
            query = new UpdateExampleQuery(container);
        }

        SDKQueryResult result = appService.executeQuery(query);
        container = (Container) result.getObjectResult();
        containerAdapter.setContainer(container);
        containerAdapter.getParent().performExpand();

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
        HQLCriteria c;
        if (position == null) {
            ContainerType containerType = (ContainerType) ((StructuredSelection) containerTypeComboViewer
                .getSelection()).getFirstElement();

            c = new HQLCriteria(
                "from edu.ualberta.med.biobank.model.Container as c "
                    + "inner join fetch c.site where c.site.id=? "
                    + "and c.position=null and c.label=? "
                    + "and c.containerType.id=?");

            c.setParameters(Arrays.asList(new Object[] { site.getId(),
                container.getLabel(), containerType.getId() }));

            List<Object> results = appService.query(c);
            if (results.size() > 0) {
                BioBankPlugin.openAsyncError("Site Name Problem",
                    "A container with label \"" + container.getLabel()
                        + "\" and type \"" + containerType.getName()
                        + "\" already exists.");
                return false;
            }

        }

        c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.Container as sc "
                + "inner join fetch sc.site where sc.site.id=? "
                + "and sc.productBarcode=?)");

        c.setParameters(Arrays.asList(new Object[] { site.getId(),
            container.getProductBarcode() }));

        List<Object> results = appService.query(c);
        if (results.size() == 0)
            return true;

        BioBankPlugin.openAsyncError("Site Name Problem",
            "A container with product barcode \""
                + container.getProductBarcode() + "\" already exists.");
        return false;
    }

    @Override
    public void cancelForm() {

    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerViewForm.ID;
    }
}
