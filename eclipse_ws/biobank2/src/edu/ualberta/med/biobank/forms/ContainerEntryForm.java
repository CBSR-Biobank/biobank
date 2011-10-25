package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.forms.widgets.Section;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.peer.ContainerPeer;
import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
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
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.admin.ContainerAdapter;
import edu.ualberta.med.biobank.treeview.admin.SiteAdapter;
import edu.ualberta.med.biobank.validators.DoubleNumberValidator;
import edu.ualberta.med.biobank.widgets.infotables.entry.CommentCollectionEntryInfoTable;
import edu.ualberta.med.biobank.widgets.utils.GuiUtil;

public class ContainerEntryForm extends BiobankEntryForm {
    public static final String ID = "edu.ualberta.med.biobank.forms.ContainerEntryForm"; //$NON-NLS-1$

    public static final String MSG_STORAGE_CONTAINER_NEW_OK = Messages.ContainerEntryForm_new_ok_msg;

    public static final String MSG_STORAGE_CONTAINER_OK = Messages.ContainerEntryForm_edit_ok_msg;

    public static final String MSG_CONTAINER_NAME_EMPTY = Messages.ContainerEntryForm_name_validation_msg;

    public static final String MSG_CONTAINER_TYPE_EMPTY = Messages.ContainerEntryForm_type_validation_msg;

    public static final String MSG_INVALID_POSITION = Messages.ContainerEntryForm_position_validation_msg;

    private ContainerAdapter containerAdapter;

    private ContainerWrapper container;

    private BgcBaseText tempWidget;

    private ComboViewer containerTypeComboViewer;

    private String oldContainerLabel;

    private ComboViewer activityStatusComboViewer;

    private boolean doSave;

    protected List<ContainerTypeWrapper> containerTypes;

    private boolean renamingChildren;

    private BgcEntryFormWidgetListener listener = new BgcEntryFormWidgetListener() {
        @Override
        public void selectionChanged(MultiSelectEvent event) {
            setDirty(true);
        }
    };

    private CommentCollectionEntryInfoTable commentEntryTable;

    @Override
    public void init() throws Exception {
        Assert.isTrue((adapter instanceof ContainerAdapter),
            "Invalid editor input: object of type " //$NON-NLS-1$
                + adapter.getClass().getName());
        containerAdapter = (ContainerAdapter) adapter;
        container = (ContainerWrapper) getModelObject();

        String tabName;
        if (container.isNew()) {
            tabName = Messages.ContainerEntryForm_new_title;
            container.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService()));
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

        if (adapter.getParent() == null)
            adapter.setParent(((SiteAdapter) SessionManager.searchFirstNode(
                Site.class, container.getSite().getId()))
                .getContainersGroupNode());

        setPartName(tabName);
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
                SessionManager.getAppService(), container.getSite());
        } else {
            containerTypes = container.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }

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
            ActivityStatusWrapper.getAllActivityStatuses(SessionManager
                .getAppService()), container.getActivityStatus(),
            Messages.ContainerEntryForm_status_validation_msg,
            new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    container
                        .setActivityStatus((ActivityStatusWrapper) selectedObject);
                }
            });

        createCommentSection();

        createContainerTypesSection(client);

    }

    private void createCommentSection() {
        Section section = createSection(Messages.Comments_title);
        commentEntryTable = new CommentCollectionEntryInfoTable(section,
            container.getCommentCollection(false)
            );
        commentEntryTable.adaptToToolkit(toolkit, true);
        commentEntryTable.addSelectionChangedListener(listener);

        addSectionToolbar(section, Messages.Comments_button_add,
            new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    commentEntryTable.addComment();
                }
            });
        section.setClient(commentEntryTable);
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
                    .getTopContainerTypesInSite(SessionManager.getAppService(),
                        currentSite);
        } else {
            containerTypes = container.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }
        if (container.isNew() && containerTypes.size() == 1)
            currentType = containerTypes.get(0);

        containerTypeComboViewer = createComboViewer(client,
            Messages.ContainerEntryForm_type_label, containerTypes,
            currentType, MSG_CONTAINER_TYPE_EMPTY, new ComboSelectionUpdate() {
                @Override
                public void doSelection(Object selectedObject) {
                    ContainerTypeWrapper ct = (ContainerTypeWrapper) selectedObject;
                    container.setContainerType(ct);
                    if (tempWidget != null) {
                        if (ct != null && Boolean.TRUE.equals(ct.getTopLevel())) {
                            Double temp = ct.getDefaultTemperature();
                            if (temp == null) {
                                tempWidget.setText(""); //$NON-NLS-1$
                            } else {
                                tempWidget.setText(temp.toString());
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
        tempWidget = (BgcBaseText) createBoundWidgetWithLabel(client,
            BgcBaseText.class, SWT.NONE,
            Messages.ContainerEntryForm_temperature_label, null, container,
            tempProperty, new DoubleNumberValidator(
                Messages.ContainerEntryForm_temperature_validation_msg));
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
            container.persist();
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
    protected void onReset() throws Exception {
        SiteWrapper site = container.getSite();
        container.reset();
        container.setSite(site);

        if (container.isNew()) {
            container.setActivityStatus(ActivityStatusWrapper
                .getActiveActivityStatus(SessionManager.getAppService()));
        }

        GuiUtil.reset(activityStatusComboViewer, container.getActivityStatus());
        GuiUtil.reset(containerTypeComboViewer, container.getContainerType());
    }
}
