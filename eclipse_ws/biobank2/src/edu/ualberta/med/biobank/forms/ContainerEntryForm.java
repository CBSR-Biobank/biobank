package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import edu.ualberta.med.biobank.BiobankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.widgets.BiobankText;
import edu.ualberta.med.biobank.widgets.utils.ComboSelectionUpdate;

public class ContainerEntryForm extends BiobankEntryForm<ContainerWrapper> {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerEntryForm";

    public static final String MSG_STORAGE_CONTAINER_NEW_OK = "Creating a new storage container.";

    public static final String MSG_STORAGE_CONTAINER_OK = "Editing an existing storage container.";

    public static final String MSG_CONTAINER_NAME_EMPTY = "Container must have a name";

    public static final String MSG_CONTAINER_TYPE_EMPTY = "Container must have a container type";

    public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

    private ContainerAdapter containerAdapter;

    private BiobankText tempWidget;

    private ComboViewer containerTypeComboViewer;

    private String oldContainerLabel;

    private ComboViewer activityStatusComboViewer;

    private boolean doSave;

    protected List<ContainerTypeWrapper> containerTypes;

    @Override
    public void init() throws Exception {
        super.init();
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;

        String tabName;
        if (modelObject.isNew()) {
            tabName = "Container";
            modelObject.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
            if (modelObject.hasParentContainer()) {
                modelObject.setLabel(modelObject.getParentContainer()
                    .getLabel() + modelObject.getPositionString());
                modelObject.setTemperature(modelObject.getParentContainer()
                    .getTemperature());
            }
        } else {
            tabName = "Container " + modelObject.getLabel();
            oldContainerLabel = modelObject.getLabel();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container");

        page.setLayout(new GridLayout(1, false));
        createContainerSection();
        createButtonsSection();
    }

    private void createContainerSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        if (!modelObject.hasParentContainer()) {
            containerTypes = ContainerTypeWrapper.getTopContainerTypesInSite(
                appService, modelObject.getSite());
        } else {
            containerTypes = modelObject.getParentContainer()
                .getContainerType().getChildContainerTypeCollection();
        }
        if (modelObject.isNew())
            adapter.setParent(((SiteAdapter) SessionManager
                .searchFirstNode(modelObject.getSite()))
                .getContainersGroupNode());

        setFirstControl(client);

        if ((modelObject.isNew() && modelObject.getParentContainer() == null)
            || (modelObject.getContainerType() != null && Boolean.TRUE
                .equals(modelObject.getContainerType().getTopLevel()))) {
            // only allow edit to label on top level containers
            setFirstControl(createBoundWidgetWithLabel(client,
                BiobankText.class, SWT.NONE, "Label", null, modelObject,
                "label", new NonEmptyStringValidator(MSG_CONTAINER_NAME_EMPTY)));
        } else {
            BiobankText l = createReadOnlyLabelledField(client, SWT.NONE,
                "Label");
            setTextValue(l, modelObject.getLabel());
        }

        Control c = createBoundWidgetWithLabel(client, BiobankText.class,
            SWT.NONE, "Product Barcode", null, modelObject, "productBarcode",
            null);
        if (getFirstControl() == null)
            setFirstControl(c);

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            modelObject.getActivityStatus(),
            "Container must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    modelObject
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, modelObject, "comment", null);

        createContainerTypesSection(client);

    }

    private void createContainerTypesSection(Composite client) throws Exception {
        List<ContainerTypeWrapper> containerTypes;
        ContainerTypeWrapper currentType = modelObject.getContainerType();
        if (!modelObject.hasParentContainer()) {
            SiteWrapper currentSite = modelObject.getSite();
            if (currentSite == null)
                containerTypes = new ArrayList<ContainerTypeWrapper>();
            else
                containerTypes = ContainerTypeWrapper
                    .getTopContainerTypesInSite(appService, currentSite);
        } else {
            containerTypes = modelObject.getParentContainer()
                .getContainerType().getChildContainerTypeCollection();
        }
        if (modelObject.isNew() && containerTypes.size() == 1)
            currentType = containerTypes.get(0);

        containerTypeComboViewer = createComboViewer(client, "Container Type",
            containerTypes, currentType, MSG_CONTAINER_TYPE_EMPTY,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ContainerTypeWrapper ct = (ContainerTypeWrapper) selectedObject;
                    modelObject.setContainerType(ct);
                    if (tempWidget != null) {
                        tempWidget.setText("");
                        if (ct != null && Boolean.TRUE.equals(ct.getTopLevel())) {
                            Double temp = ct.getDefaultTemperature();
                            if (temp == null) {
                                tempWidget.setText("");
                            } else {
                                tempWidget.setText(temp.toString());
                            }
                        }
                    }
                }
            });
        tempWidget = (BiobankText) createBoundWidgetWithLabel(client,
            BiobankText.class, SWT.NONE, "Temperature (Celcius)", null,
            modelObject, "temperature", new DoubleNumberValidator(
                "Default temperature is not a valid number"));
        if (modelObject.hasParentContainer())
            tempWidget.setEnabled(false);

        if (modelObject.hasChildren() || modelObject.hasSpecimens()) {
            containerTypeComboViewer.getCombo().setEnabled(false);
        }
    }

    private void createButtonsSection() {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 10;
        layout.numColumns = 2;
        client.setLayout(layout);
        toolkit.paintBordersFor(client);
    }

    @Override
    protected String getOkMessage() {
        if (modelObject.isNew()) {
            return MSG_STORAGE_CONTAINER_NEW_OK;
        }
        return MSG_STORAGE_CONTAINER_OK;
    }

    @Override
    protected void doBeforeSave() throws Exception {
        doSave = true;
        if (modelObject.hasChildren() && oldContainerLabel != null
            && !oldContainerLabel.equals(modelObject.getLabel())) {
            doSave = BiobankPlugin
                .openConfirm(
                    "Renaming container",
                    "This container has been renamed. Its children will also be renamed. Are you sure you want to continue ?");
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (doSave) {
            modelObject.persist();
            SessionManager.updateAllSimilarNodes(containerAdapter, true);
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
        modelObject.reset();
        ActivityStatusWrapper activity = modelObject.getActivityStatus();
        if (activity != null) {
            activityStatusComboViewer.setSelection(new StructuredSelection(
                activity));
        } else if (activityStatusComboViewer.getCombo().getItemCount() > 1) {
            activityStatusComboViewer.getCombo().deselectAll();
        }
        setDirty(false);
    }
}
