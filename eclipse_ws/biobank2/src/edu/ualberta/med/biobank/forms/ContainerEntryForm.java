package edu.ualberta.med.biobank.forms;

import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction;
import edu.ualberta.med.biobank.common.action.container.ContainerGetInfoAction.ContainerInfo;
import edu.ualberta.med.biobank.common.action.container.ContainerSaveAction;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.Property;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.gui.common.BgcPlugin;
import edu.ualberta.med.biobank.gui.common.validators.NonEmptyStringValidator;
import edu.ualberta.med.biobank.gui.common.widgets.BgcBaseText;
import edu.ualberta.med.biobank.gui.common.widgets.BgcEntryFormWidgetListener;
import edu.ualberta.med.biobank.gui.common.widgets.MultiSelectEvent;
import edu.ualberta.med.biobank.gui.common.widgets.utils.ComboSelectionUpdate;
import edu.ualberta.med.biobank.model.ActivityStatus;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class ContainerEntryForm extends BiobankEntryForm {
    public static final String ID =
        "edu.ualberta.med.biobank.forms.ContainerEntryForm"; //$NON-NLS-1$

    public static final String MSG_STORAGE_CONTAINER_NEW_OK =
        Messages.ContainerEntryForm_new_ok_msg;

    public static final String MSG_STORAGE_CONTAINER_OK =
        Messages.ContainerEntryForm_edit_ok_msg;

    public static final String MSG_CONTAINER_NAME_EMPTY =
        Messages.ContainerEntryForm_name_validation_msg;

    public static final String MSG_CONTAINER_TYPE_EMPTY =
        Messages.ContainerEntryForm_type_validation_msg;

    public static final String MSG_INVALID_POSITION =
        Messages.ContainerEntryForm_position_validation_msg;

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container = new ContainerWrapper(
        SessionManager.getAppService());

    private BgcBaseText temperatureWidget;

    private ComboViewer containerTypeComboViewer;

    private String oldContainerLabel;

    private ComboViewer activityStatusComboViewer;

    private boolean doSave;

    protected List<ContainerTypeWrapper> containerTypes;

    private boolean renamingChildren;

    private ContainerInfo containerInfo;

    private BgcEntryFormWidgetListener listener =
        new BgcEntryFormWidgetListener() {
            @Override
            public void selectionChanged(MultiSelectEvent event) {
                setDirty(true);
            }
        };

    private CommentCollectionInfoTable commentEntryTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;
        updateContainerInfo(adapter.getId());

        String tabName;
        if (container.isNew()) {
            tabName = Messages.ContainerEntryForm_new_title;
            container.setActivityStatus(ActivityStatus.ACTIVE);
            if (container.hasParentContainer()) {
                // need to set the label at least for display. But will be set
                // during persit dependencies of the container
                // TODO: don't think this is necessary anymore, but CHECK!
                // container.setLabelUsingPositionAndParent();
            }
        } else {
            tabName = NLS.bind(Messages.ContainerEntryForm_edit_title,
                container.getLabel());
            oldContainerLabel = container.getLabel();
        }

        if (adapter.getParent() == null) {
            SiteAdapter siteAdapter = (SiteAdapter) SessionManager
                .searchFirstNode(Site.class, container.getSite().getId());
            if (siteAdapter != null) {
                adapter.setParent(siteAdapter.getContainersGroupNode());
            }
        }

        setPartName(tabName);
    }

    private void updateContainerInfo(Integer id) throws ApplicationException {
        if (id != null) {
            containerInfo = SessionManager.getAppService().doAction(
                new ContainerGetInfoAction(id));
            container.setWrappedObject(containerInfo.container);
        } else {
            containerInfo = new ContainerInfo();
            container.setWrappedObject((Container) containerAdapter
                .getModelObject().getWrappedObject());
        }

        ((AdapterBase) adapter).setModelObject(container);
    }

    @Override
    protected void createFormContent() throws Exception {
        form.setText(Messages.ContainerEntryForm_form_title);
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
                Messages.ContainerEntryForm_label_label, null, container,
                ContainerPeer.LABEL.getName(), new NonEmptyStringValidator(
                    MSG_CONTAINER_NAME_EMPTY)));
            labelIsFirstControl = true;
        } else {
            BgcBaseText l = createReadOnlyLabelledField(client, SWT.NONE,
                Messages.ContainerEntryForm_label_label);
            setTextValue(l, container.getLabel());
        }

        Control c = createBoundWidgetWithLabel(client, BgcBaseText.class,
            SWT.NONE, Messages.ContainerEntryForm_barcode_label, null,
            container, ContainerPeer.PRODUCT_BARCODE.getName(), null);
        if (!labelIsFirstControl)
            setFirstControl(c);

        activityStatusComboViewer = createComboViewer(client,
            Messages.ContainerEntryForm_status_label,
            ActivityStatus.valuesList(), container.getActivityStatus(),
            Messages.ContainerEntryForm_status_validation_msg,
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

    private void createContainerTypesSection(Composite client) throws Exception {
        // List<ContainerTypeWrapper> containerTypes;
        ContainerTypeWrapper currentType = container.getContainerType();
        // if (!container.hasParentContainer()) {
        // SiteWrapper currentSite = container.getSite();
        // if (currentSite == null)
        // containerTypes = new ArrayList<ContainerTypeWrapper>();
        // else
        // containerTypes = ContainerTypeWrapper
        // .getTopContainerTypesInSite(SessionManager.getAppService(),
        // currentSite);
        // } else {
        // containerTypes = container.getParentContainer().getContainerType()
        // .getChildContainerTypeCollection();
        // }

        containerTypeComboViewer = createComboViewer(client,
            Messages.ContainerEntryForm_type_label, containerTypes,
            currentType, MSG_CONTAINER_TYPE_EMPTY, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ContainerTypeWrapper ct =
                        (ContainerTypeWrapper) selectedObject;
                    container.setContainerType(ct);
                    if (temperatureWidget != null) {
                        if (ct != null && Boolean.TRUE.equals(ct.getTopLevel())) {
                            Double temp = ct.getDefaultTemperature();
                            if (temp == null) {
                                temperatureWidget.setText(""); //$NON-NLS-1$
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
            tempProperty = Property.concatNames(ContainerPeer.TOP_CONTAINER,
                ContainerPeer.TEMPERATURE);
        temperatureWidget = (BgcBaseText) createBoundWidgetWithLabel(client,
            BgcBaseText.class, SWT.NONE,
            Messages.ContainerEntryForm_temperature_label, null, container,
            tempProperty, new DoubleNumberValidator(
                Messages.ContainerEntryForm_temperature_validation_msg));
        if (container.hasParentContainer())
            temperatureWidget.setEnabled(false);

        if (container.hasChildren() || container.hasSpecimens()) {
            containerTypeComboViewer.getCombo().setEnabled(false);
        }
    }

    private void createCommentSection() {
        Composite client = createSectionWithClient(Messages.Comments_title);
        GridLayout gl = new GridLayout(2, false);

        client.setLayout(gl);
        commentEntryTable = new CommentCollectionInfoTable(client,
            container.getCommentCollection(false));
        GridData gd = new GridData();
        gd.horizontalSpan = 2;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        commentEntryTable.setLayoutData(gd);
        createLabelledWidget(client, BgcBaseText.class, SWT.MULTI,
            Messages.Comments_add);

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
        renamingChildren = container.hasChildren() && oldContainerLabel != null
            && !oldContainerLabel.equals(container.getLabel());
        if (renamingChildren) {
            doSave = BgcPlugin.openConfirm(
                Messages.ContainerEntryForm_renaming_dialog_title,
                Messages.ContainerEntryForm_renaming_dialog_msg);
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

            SessionManager.updateAllSimilarNodes(containerAdapter, true);
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
    public String getNextOpenedFormID() {
        return ContainerViewForm.ID;
    }

    @Override
    public void setValues() throws Exception {
        SiteWrapper site = container.getSite();
        container.reset();
        container.setSite(site);

        if (container.isNew()) {
            container.setActivityStatus(ActivityStatus.ACTIVE);
        }

        if (!container.hasParentContainer()) {
            containerTypes = ContainerTypeWrapper.getTopContainerTypesInSite(
                SessionManager.getAppService(), container.getSite());
        } else {
            containerTypes = container.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }
        containerTypeComboViewer.setInput(containerTypes);
        if (container.isNew() && containerTypes.size() == 1)
            containerTypeComboViewer.setSelection(new StructuredSelection(
                containerTypes.get(0)));

        GuiUtil.reset(activityStatusComboViewer, container.getActivityStatus());
        GuiUtil.reset(containerTypeComboViewer, container.getContainerType());
    }
}
