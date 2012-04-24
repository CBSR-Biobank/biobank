package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.xnap.commons.i18n.I18n;
import org.xnap.commons.i18n.I18nFactory;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Comment;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.infotables.CommentsInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerEntryForm extends BiobankEntryForm {
    private static final I18n i18n = I18nFactory
        .getI18n(ContainerEntryForm.class);

    @SuppressWarnings("nls")
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerEntryForm";

    @SuppressWarnings("nls")
    // title area message
    public static final String MSG_STORAGE_CONTAINER_NEW_OK =
        i18n.tr("Creating a new storage container.");

    @SuppressWarnings("nls")
    // title area message
    public static final String MSG_STORAGE_CONTAINER_OK =
        i18n.tr("Editing an existing storage container.");

    @SuppressWarnings("nls")
    // validation error message
    public static final String MSG_CONTAINER_NAME_EMPTY =
        i18n.tr("Container must have a name");

    @SuppressWarnings("nls")
    // validation error message
    public static final String MSG_CONTAINER_TYPE_EMPTY =
        i18n.tr("Container must have a container type");

    @SuppressWarnings("nls")
    // validation error message
    public static final String MSG_INVALID_POSITION =
        i18n.tr("Position is empty or not a valid number");

    private ContainerAdapter containerAdapter;

    private final ContainerWrapper container = new ContainerWrapper(
        SessionManager.getAppService());

    private BgcBaseText temperatureWidget;

    private ComboViewer containerTypeComboViewer;

    private String oldContainerLabel;

    private ComboViewer activityStatusComboViewer;

    private boolean doSave;

    protected List<ContainerTypeWrapper> containerTypes;

    private boolean renamingChildren;

    private ContainerInfo containerInfo;

    private CommentsInfoTable commentEntryTable;

    private final CommentWrapper comment = new CommentWrapper(
        SessionManager.getAppService());

    @SuppressWarnings("nls")
    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type "
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;
        updateContainerInfo(adapter.getId());

        String tabName;
        if (container.isNew()) {
            tabName = Container.NAME.format(1).toString();
            container.setActivityStatus(ActivityStatus.ACTIVE);
            if (container.hasParentContainer()) {
                // need to set the label at least for display. But will be set
                // during persit dependencies of the container
                // TODO: don't think this is necessary anymore, but CHECK!
                // container.setLabelUsingPositionAndParent();
            }
        } else {
            // tab name, {0} is the container label
            tabName = i18n.tr("Container {0}", container.getLabel());
            oldContainerLabel = container.getLabel();
        }

        if (adapter.getParent() == null) {
            SiteAdapter siteAdapter =
                (SiteAdapter) SessionManager.searchFirstNode(Site.class,
                    container.getSite().getId());
            if (siteAdapter != null) {
                adapter.setParent(siteAdapter.getContainersGroupNode());
            }
        }

        setPartName(tabName);
    }

    private void updateContainerInfo(Integer id) throws ApplicationException {
        if (id != null) {
            containerInfo =
                SessionManager.getAppService().doAction(
                    new ContainerGetInfoAction(id));
            container.setWrappedObject(containerInfo.container);
        } else {
            containerInfo = new ContainerInfo();
            container.setWrappedObject((Container) containerAdapter
                .getModelObject().getWrappedObject());
        }

        comment.setWrappedObject(new Comment());
        ((AdapterBase) adapter).setModelObject(container);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Container.NAME.format(1).toString());
        setDirty(true);
        page.setLayout(new GridLayout(1, false));
        createContainerSection();
        createButtonsSection();

        if (container.isNew()) {
            GuiUtil.reset(containerTypeComboViewer,
                container.getContainerType());
        }

        setValues();
    }

    @SuppressWarnings("nls")
    private void createContainerSection() throws Exception {
        Composite client = toolkit.createComposite(page);
        GridLayout layout = new GridLayout(2, false);
        layout.horizontalSpacing = 10;
        client.setLayout(layout);
        client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        toolkit.paintBordersFor(client);

        setFirstControl(client);

        boolean labelIsFirstControl = false;
        if ((container.isNew() && container.getParentContainer() == null)
            || (container.getContainerType() != null && Boolean.TRUE
                .equals(container.getContainerType().getTopLevel()))) {
            // only allow edit to label on top level containers
            setFirstControl(createBoundWidgetWithLabel(client,
                BgcBaseText.class, SWT.NONE,
                Container.Property.LABEL.toString(), null, container,
                ContainerPeer.LABEL.getName(), new NonEmptyStringValidator(
                    MSG_CONTAINER_NAME_EMPTY)));
            labelIsFirstControl = true;
        } else {
            BgcBaseText l =
                createReadOnlyLabelledField(client, SWT.NONE,
                    Container.Property.LABEL.toString());
            setTextValue(l, container.getLabel());
        }

        Control c =
            createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
                Container.Property.PRODUCT_BARCODE.toString(),
                null, container,
                ContainerPeer.PRODUCT_BARCODE.getName(), null);
        if (!labelIsFirstControl) setFirstControl(c);

        activityStatusComboViewer =
            createComboViewer(client,
                Container.Property.ACTIVITY_STATUS.toString(),
                ActivityStatus.valuesList(), container.getActivityStatus(),
                // validation error message
                i18n.tr("Container must have an activity status"),
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        container
                            .setActivityStatus((ActivityStatus) selectedObject);
                    }
                });

        createContainerTypesSection(client);
        createCommentSection();
    }

    @SuppressWarnings("nls")
    private void createContainerTypesSection(Composite client) throws Exception {
        ContainerTypeWrapper currentType = container.getContainerType();

        containerTypeComboViewer =
            createComboViewer(client,
                Container.Property.CONTAINER_TYPE.toString(),
                containerTypes, currentType, MSG_CONTAINER_TYPE_EMPTY,
                new ComboSelectionUpdate() {
                    @Override
                    public void doSelection(Object selectedObject) {
                        ContainerTypeWrapper ct =
                            (ContainerTypeWrapper) selectedObject;
                        container.setContainerType(ct);
                        if (temperatureWidget != null) {
                            if (ct != null
                                && Boolean.TRUE.equals(ct.getTopLevel())) {
                                Double temp = ct.getDefaultTemperature();
                                if (temp == null) {
                                    temperatureWidget.setText("");
                                } else {
                                    temperatureWidget.setText(temp.toString());
                                }
                            }
                        }
                    }
                });

        // temperature is set for the toplevel container only.
        String tempProperty = ContainerPeer.TEMPERATURE.getName();
        if (container.hasParentContainer())
            // subcontainer are using topcontainer temperature. This is display
            // only.
            tempProperty =
                Property.concatNames(ContainerPeer.TOP_CONTAINER,
                    ContainerPeer.TEMPERATURE);
        temperatureWidget =
            (BgcBaseText) createBoundWidgetWithLabel(client, BgcBaseText.class,
                SWT.NONE,
                // label
                i18n.tr("Temperature (Celcius)"), null,
                container, tempProperty, new DoubleNumberValidator(
                    // validation error message
                    i18n.tr("Default temperature is not a valid number")));
        if (container.hasParentContainer())
            temperatureWidget.setEnabled(false);

        if (container.hasChildren() || container.hasSpecimens()) {
            containerTypeComboViewer.getCombo().setEnabled(false);
        }
    }

    @SuppressWarnings("nls")
    // TODO: this code seems to be copy and pasted all over, extract into
    // method?
    private void createCommentSection() {
        Composite client =
            createSectionWithClient(Comment.NAME.format(2).toString());
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable =
            new CommentsInfoTable(client, container.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
            // label
            i18n.tr("Add a comment"), null, comment, "message", null);

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

    @SuppressWarnings("nls")
    @Override
    protected void doBeforeSave() throws Exception {
        doSave = true;
        renamingChildren =
            container.hasChildren() && oldContainerLabel != null
                && !oldContainerLabel.equals(container.getLabel());
        if (renamingChildren) {
            doSave =
                BgcPlugin
                    .openConfirm(
                        // dialog title
                        i18n.tr("Renaming container"),
                        // dialog message
                        i18n.tr("This container has been renamed. Its children will also be renamed. Are you sure you want to continue ?"));
        }
    }

    @Override
    protected void saveForm() throws Exception {
        if (doSave) {
            final ContainerSaveAction saveAction = new ContainerSaveAction();
            saveAction.setId(container.getId());
            saveAction.setBarcode(container.getProductBarcode());
            saveAction.setActivityStatus(container.getActivityStatus());
            saveAction.setSiteId(container.getSite().getId());
            saveAction.setTypeId(container.getContainerType().getId());
            saveAction.setPosition(container.getPositionAsRowCol());
            saveAction.setCommentText(comment.getMessage());
            if (container.getParentContainer() != null) {
                saveAction.setParentId(container.getParentContainer().getId());
            }

            // only set the label on top level containers
            if (container.getContainerType().getTopLevel()) {
                saveAction.setLabel(container.getLabel());
            }

            Integer id =
                SessionManager.getAppService().doAction(saveAction).getId();
            updateContainerInfo(id);

            if (renamingChildren)
                Display.getDefault().asyncExec(new Runnable() {
                    @Override
                    public void run() {
                        containerAdapter.rebuild();
                    }
                });
        } else {
            setDirty(true);
        }
    }

    @Override
    public String getNextOpenedFormId() {
        return ContainerViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        if (container.isNew()) {
            container.setActivityStatus(ActivityStatus.ACTIVE);
        }

        if (!container.hasParentContainer()) {
            containerTypes =
                ContainerTypeWrapper.getTopContainerTypesInSite(
                    SessionManager.getAppService(), container.getSite());
        } else {
            containerTypes =
                container.getParentContainer().getContainerType()
                    .getChildContainerTypeCollection();
        }
        containerTypeComboViewer.setInput(containerTypes);
        if (container.isNew() && containerTypes.size() == 1)
            containerTypeComboViewer.setSelection(new StructuredSelection(
                containerTypes.get(0)));

        GuiUtil.reset(activityStatusComboViewer, container.getActivityStatus());
        GuiUtil.reset(containerTypeComboViewer, container.getContainerType());

        commentEntryTable.setList(container.getCommentCollection(false));
    }
}
