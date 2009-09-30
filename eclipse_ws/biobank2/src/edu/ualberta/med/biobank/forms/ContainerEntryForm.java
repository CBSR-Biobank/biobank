package edu.ualberta.med.biobank.forms;

import java.util.Collection;

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

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.utils.SiteUtils;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumber;
import edu.ualberta.med.biobank.validators.NonEmptyString;

public class ContainerEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerEntryForm";

    public static final String MSG_STORAGE_CONTAINER_NEW_OK = "Creating a new storage container.";

    public static final String MSG_STORAGE_CONTAINER_OK = "Editing an existing storage container.";

    public static final String MSG_CONTAINER_NAME_EMPTY = "Container must have a name";

    public static final String MSG_CONTAINER_TYPE_EMPTY = "Container must have a container type";

    public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container;

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

        String tabName;
        if (container.isNew()) {
            tabName = "Container";
            if (container.getPosition() != null) {
                ContainerPosition pos = container.getPosition();
                container.setLabel(pos.getParentContainer().getLabel()
                    + LabelingScheme.getPositionString(pos));
                container.setTemperature(pos.getParentContainer()
                    .getTemperature());
            }
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

        if (container.getPosition() == null) {
            // only allow edit to label on top level containers
            createBoundWidgetWithLabel(client, Text.class, SWT.NONE, "Label",
                null, PojoObservables.observeValue(
                    container.getWrappedObject(), "label"),
                NonEmptyString.class, MSG_CONTAINER_NAME_EMPTY);
        } else {
            Label l = (Label) createWidget(client, Label.class, SWT.NONE,
                "Label");
            FormUtils.setTextValue(l, container.getLabel());
        }

        createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Product Barcode", null, PojoObservables.observeValue(container
                .getWrappedObject(), "productBarcode"), null, null);

        createBoundWidgetWithLabel(client, Combo.class, SWT.NONE,
            "Activity Status", FormConstants.ACTIVITY_STATUS, PojoObservables
                .observeValue(container.getWrappedObject(), "activityStatus"),
            null, null);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, PojoObservables.observeValue(container
                .getWrappedObject(), "comment"), null, null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);

        createContainerTypesSection(client);
    }

    private void createContainerTypesSection(Composite client) {
        Collection<ContainerType> containerTypes;
        ContainerPosition pos = container.getPosition();
        if ((pos == null) || (pos.getParentContainer() == null)) {
            containerTypes = SiteUtils.getTopContainerTypesInSite(appService,
                site);
        } else {
            containerTypes = pos.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }

        if (currentContainerType == null) {
            if (containerTypes.size() == 1) {
                currentContainerType = containerTypes.iterator().next();
                setDirty(true);
            }
        } else {
            for (ContainerType type : containerTypes) {
                if (currentContainerType.getId().equals(type.getId())) {
                    currentContainerType = type;
                    break;
                }
            }
        }

        containerTypeComboViewer = createCComboViewerWithNoSelectionValidator(
            client, "Container Type", containerTypes, currentContainerType,
            MSG_CONTAINER_TYPE_EMPTY);
        containerTypeComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                    ContainerType containerType = (ContainerType) selection
                        .getFirstElement();
                    if (containerType.getTopLevel() != null
                        && containerType.getTopLevel()) {
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
                .observeValue(container.getWrappedObject(), "temperature"),
            DoubleNumber.class, "Default temperature is not a valid number");
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
        if (container.isNew()) {
            return MSG_STORAGE_CONTAINER_NEW_OK;
        }
        return MSG_STORAGE_CONTAINER_OK;
    }

    @Override
    protected void saveForm() throws Exception {
        ContainerType containerType = (ContainerType) ((StructuredSelection) containerTypeComboViewer
            .getSelection()).getFirstElement();
        container.setContainerType(containerType);
        container.setSite(site);
        container.persist();
        containerAdapter.getParent().addChild(containerAdapter);
        containerAdapter.getParent().performExpand();

    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerViewForm.ID;
    }
}
