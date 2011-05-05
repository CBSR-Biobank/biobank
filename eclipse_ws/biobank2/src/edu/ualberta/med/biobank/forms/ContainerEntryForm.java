package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
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
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class ContainerEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerEntryForm";

    public static final String MSG_STORAGE_CONTAINER_NEW_OK = "Creating a new storage container.";

    public static final String MSG_STORAGE_CONTAINER_OK = "Editing an existing storage container.";

    public static final String MSG_CONTAINER_NAME_EMPTY = "Container must have a name";

    public static final String MSG_CONTAINER_TYPE_EMPTY = "Container must have a container type";

    public static final String MSG_INVALID_POSITION = "Position is empty or not a valid number";

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container;

    private BiobankText tempWidget;

    private ComboViewer containerTypeComboViewer;

    private String oldContainerLabel;

    private ComboViewer activityStatusComboViewer;

    private boolean doSave;

    protected List<ContainerTypeWrapper> containerTypes;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;
        container = (ContainerWrapper) getModelObject();

        String tabName;
        if (container.isNew()) {
            tabName = "Container";
            container.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        } else {
            tabName = "Container " + container.getLabel();
            oldContainerLabel = container.getLabel();
        }
        setPartName(tabName);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText("Container");

        page.setLayout(new GridLayout(1, false));
        createContainerSection();
        createButtonsSection();

        if (container.isNew()) {
            GuiUtil.reset(containerTypeComboViewer,
                container.getContainerType());
        }
    }

    private void createContainerSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        if (!container.hasParentContainer()) {
            containerTypes = ContainerTypeWrapper.getTopContainerTypesInSite(
                appService, container.getSite());
        } else {
            containerTypes = container.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }
        if (container.isNew())
            adapter
                .setParent(((SiteAdapter) SessionManager
                    .searchFirstNode(container.getSite()))
                    .getContainersGroupNode());

        setFirstControl(client);

        if ((container.isNew() && container.getParentContainer() == null)
            || (container.getContainerType() != null && Boolean.TRUE
                .equals(container.getContainerType().getTopLevel()))) {
            // only allow edit to label on top level containers
            setFirstControl(createBoundWidgetWithLabel(client,
                BiobankText.class, SWT.NONE, "Label", null, container, "label",
                new NonEmptyStringValidator(MSG_CONTAINER_NAME_EMPTY)));
        } else {
            BiobankText l = createReadOnlyLabelledField(client, SWT.NONE,
                "Label");
            setTextValue(l, container.getLabel());
        }

        Control c = createBoundWidgetWithLabel(client, BiobankText.class,
            SWT.NONE, "Product Barcode", null, container, "productBarcode",
            null);
        if (getFirstControl() == null)
            setFirstControl(c);

        activityStatusComboViewer = createComboViewer(client,
            "Activity Status",
            ActivityStatusWrapper.getAllActivityStatuses(appService),
            container.getActivityStatus(),
            "Container must have an activity status",
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    container
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createBoundWidgetWithLabel(client, BiobankText.class, SWT.MULTI,
            "Comments", null, container, "comment", null);

        createContainerTypesSection(client);

    }

    private void createContainerTypesSection(Composite client) throws Exception {
        List<ContainerTypeWrapper> containerTypes;
        ContainerTypeWrapper currentType = container.getContainerType();
        if (!container.hasParentContainer()) {
            SiteWrapper currentSite = container.getSite();
            if (currentSite == null)
                containerTypes = new ArrayList<ContainerTypeWrapper>();
            else
                containerTypes = ContainerTypeWrapper
                    .getTopContainerTypesInSite(appService, currentSite);
        } else {
            containerTypes = container.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }
        if (container.isNew() && containerTypes.size() == 1)
            currentType = containerTypes.get(0);

        containerTypeComboViewer = createComboViewer(client, "Container Type",
            containerTypes, currentType, MSG_CONTAINER_TYPE_EMPTY,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ContainerTypeWrapper ct = (ContainerTypeWrapper) selectedObject;
                    container.setContainerType(ct);
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
            container, "temperature", new DoubleNumberValidator(
                "Default temperature is not a valid number"));
        if (container.hasParentContainer())
            tempWidget.setEnabled(false);

        if (container.hasChildren() || container.hasSpecimens()) {
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
        if (container.isNew()) {
            return MSG_STORAGE_CONTAINER_NEW_OK;
        }
        return MSG_STORAGE_CONTAINER_OK;
    }

    @Override
    protected void doBeforeSave() throws Exception {
        doSave = true;
        if (container.hasChildren() && oldContainerLabel != null
            && !oldContainerLabel.equals(container.getLabel())) {
            doSave = BiobankPlugin
                .openConfirm(
                    "Renaming container",
                    "This container has been renamed. Its children will also be renamed. Are you sure you want to continue ?");
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (doSave) {
            container.persist();
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
    protected void onReset() throws Exception {
        SiteWrapper site = container.getSite();
        container.reset();
        container.setSite(site);

        if (container.isNew()) {
            container.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(appService));
        }

        GuiUtil.reset(activityStatusComboViewer, container.getActivityStatus());
        GuiUtil.reset(containerTypeComboViewer, container.getContainerType());
    }
}
