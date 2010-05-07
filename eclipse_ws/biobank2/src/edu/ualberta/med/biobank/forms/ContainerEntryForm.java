package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;

public class ContainerEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerEntryForm";

    public static final String MSG_STORAGE_CONTAINER_NEW_OK = "Creating a new storage container.";

    public static final String MSG_STORAGE_CONTAINER_OK = "Editing an existing storage container.";

    public static final String MSG_CONTAINER_NAME_EMPTY = "Container must have a name";

    public static final String MSG_CONTAINER_TYPE_EMPTY = "Container must have a container type";

    public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container;

    private SiteWrapper siteWrapper;

    private Text tempWidget;

    private ContainerTypeWrapper currentContainerType;

    private ComboViewer containerTypeComboViewer;

    private String oldContainerLabel;

    private ComboViewer activityStatusComboViewer;

    @Override
    public void init() {
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;
        container = containerAdapter.getContainer();
        siteWrapper = container.getSite();

        String tabName;
        if (container.isNew()) {
            tabName = "Container";
            if (container.hasParent()) {
                container.setLabel(container.getParent().getLabel()
                    + LabelingScheme.getPositionString(container));
                container
                    .setTemperature(container.getParent().getTemperature());
            }
        } else {
            tabName = "Container " + container.getLabel();
            oldContainerLabel = container.getLabel();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container");
        if (container.getContainerType() != null) {
            form.setImage(BioBankPlugin.getDefault().getIconForTypeName(
                container.getContainerType().getName()));
        }

        currentContainerType = container.getContainerType();
        form.getBody().setLayout(new GridLayout(1, false));
        createContainerSection();
        createButtonsSection();
    }

    private void createContainerSection() throws Exception {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        Text siteLabel = createReadOnlyLabelledField(client, SWT.NONE,
            "Repository Site");
        setTextValue(siteLabel, container.getSite().getName());

        if ((container.isNew() && container.getParent() == null)
            || (container.getContainerType() != null && Boolean.TRUE
                .equals(container.getContainerType().getTopLevel()))) {
            // only allow edit to label on top level containers
            setFirstControl(createBoundWidgetWithLabel(client, Text.class,
                SWT.NONE, "Label", null, BeansObservables.observeValue(
                    container, "label"), new NonEmptyStringValidator(
                    MSG_CONTAINER_NAME_EMPTY)));
        } else {
            Text l = createReadOnlyLabelledField(client, SWT.NONE, "Label");
            setTextValue(l, container.getLabel());
        }

        Control c = createBoundWidgetWithLabel(client, Text.class, SWT.NONE,
            "Product Barcode", null, BeansObservables.observeValue(container,
                "productBarcode"), null);
        if (getFirstControl() == null)
            setFirstControl(c);

        activityStatusComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Activity Status", ActivityStatusWrapper
                .getAllActivityStatuses(appService), container
                .getActivityStatus(), "Container must have an activity status",
            true);

        Text comment = (Text) createBoundWidgetWithLabel(client, Text.class,
            SWT.MULTI, "Comments", null, BeansObservables.observeValue(
                container, "comment"), null);
        GridData gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.heightHint = 40;
        comment.setLayoutData(gd);

        createContainerTypesSection(client);
    }

    private void createContainerTypesSection(Composite client) throws Exception {
        List<ContainerTypeWrapper> containerTypes;
        if (!container.hasParent()) {
            containerTypes = ContainerTypeWrapper.getTopContainerTypesInSite(
                appService, siteWrapper);
        } else {
            containerTypes = container.getParent().getContainerType()
                .getChildContainerTypeCollection();
        }

        if (currentContainerType == null) {
            if (containerTypes.size() == 1) {
                currentContainerType = containerTypes.get(0);
                setDirty(true);
            }
        }

        containerTypeComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Container Type", containerTypes, currentContainerType,
            MSG_CONTAINER_TYPE_EMPTY, true);
        containerTypeComboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    IStructuredSelection selection = (IStructuredSelection) event
                        .getSelection();
                    ContainerTypeWrapper containerType = (ContainerTypeWrapper) selection
                        .getFirstElement();
                    if (Boolean.TRUE.equals(containerType.getTopLevel())) {
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
            SWT.NONE, "Temperature (Celcius)", null, BeansObservables
                .observeValue(container, "temperature"),
            new DoubleNumberValidator(
                "Default temperature is not a valid number"));
        if (container.hasParent())
            tempWidget.setEnabled(false);
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(form.getBody());
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);
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
        boolean doSave = true;
        boolean newName = false;
        if (container.hasChildren() && oldContainerLabel != null
            && !oldContainerLabel.equals(container.getLabel())) {
            doSave = BioBankPlugin
                .openConfirm(
                    "Renaming container",
                    "This container has been renamed. Its children will also be renamed. Are you sure you want to continue ?");
            newName = true;
        }
        if (doSave) {
            ContainerTypeWrapper containerType = (ContainerTypeWrapper) ((StructuredSelection) containerTypeComboViewer
                .getSelection()).getFirstElement();
            container.setContainerType(containerType);
            ActivityStatusWrapper activity = (ActivityStatusWrapper) ((StructuredSelection) activityStatusComboViewer
                .getSelection()).getFirstElement();
            container.setActivityStatus(activity);
            callPersistWithProgressDialog();
            if (newName) {
                container.reload();
                containerAdapter.rebuild();
                containerAdapter.performExpand();
            } else {
                containerAdapter.getParent().addChild(containerAdapter);
            }
            containerAdapter.getParent().performExpand();
        } else {
            setDirty(true);
        }
    }

    @Override
    public String getNextOpenedFormID() {
        return ContainerViewForm.ID;
    }

    @Override
    public void reset() throws Exception {
        super.reset();
        currentContainerType = container.getContainerType();
        if (currentContainerType != null) {
            containerTypeComboViewer.setSelection(new StructuredSelection(
                currentContainerType));
        } else if (containerTypeComboViewer.getCombo().getItemCount() > 1) {
            containerTypeComboViewer.getCombo().deselectAll();
        }
        ActivityStatusWrapper activity = container.getActivityStatus();
        if (activity != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activity));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
    }

}
