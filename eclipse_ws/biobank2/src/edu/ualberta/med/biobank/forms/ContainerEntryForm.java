package edu.ualberta.med.biobank.forms;

import java.util.ArrayList;
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
import edu.ualberta.med.biobank.LabelingScheme;
import edu.ualberta.med.biobank.RowColPos;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.treeview.AdaptorBase;
import edu.ualberta.med.biobank.treeview.ContainerAdapter;
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
    public void init(AdaptorBase adaptor) {
        Assert.isTrue((adaptor instanceof ContainerAdapter),
            "Invalid editor input: object of type "
                + adaptor.getClass().getName());
        containerAdapter = (ContainerAdapter) adaptor;
        appService = containerAdapter.getAppService();
        container = containerAdapter.getContainer();
        site = containerAdapter.getSite();
        position = container.getPosition();
        viewFormId = ContainerViewForm.ID;

        if (position != null) {
            RowColPos rcp = new RowColPos();
            rcp.row = position.getPositionDimensionOne();
            rcp.col = position.getPositionDimensionTwo();

            container.setLabel(position.getParentContainer().getLabel()
                + LabelingScheme.rowColToTwoCharAlpha(rcp, position
                    .getParentContainer().getContainerType()));
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

    private List<ContainerType> getTopContainerTypes() {
        List<ContainerType> results = new ArrayList<ContainerType>();
        HQLCriteria c = new HQLCriteria(
            "from edu.ualberta.med.biobank.model.ContainerType as cttop"
                + " where cttop.id not in (select child.id"
                + " from edu.ualberta.med.biobank.model.ContainerType as ct"
                + " left join ct.childContainerTypeCollection as child "
                + " where child.id!=null)");
        try {
            results = appService.query(c);
        } catch (Exception e) {
            System.out.println("Query Failed.");
        }
        return results;
    }

    private void createContainerTypesSection(Composite client) {
        Collection<ContainerType> containerTypes;
        if ((position == null) || (position.getParentContainer() == null)) {
            containerTypes = getTopContainerTypes();
        } else {
            containerTypes = position.getParentContainer().getContainerType()
                .getChildContainerTypeCollection();
        }
        containerTypeComboViewer = createComboViewerWithNoSelectionValidator(
            client, "Container Type", containerTypes, MSG_CONTAINER_TYPE_EMPTY);
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
    protected void cancelForm() {
        // TODO Auto-generated method stub
    }
}
