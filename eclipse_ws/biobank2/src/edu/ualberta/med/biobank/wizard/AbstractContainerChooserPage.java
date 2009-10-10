package edu.ualberta.med.biobank.wizard;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import edu.ualberta.med.biobank.common.wrappers.ContainerPositionWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.widgets.ContainerDisplayWidget;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractContainerChooserPage extends WizardPage {

    private static Logger LOGGER = Logger
        .getLogger(AbstractContainerChooserPage.class.getName());

    private ContainerWrapper currentContainer;

    protected ContainerDisplayWidget containerWidget;

    protected Composite pageContainer;

    protected Text textPosition;

    protected Integer gridWidth;
    protected Integer gridHeight;

    protected int defaultDim1 = 6;
    protected int defaultDim2 = 8;

    public AbstractContainerChooserPage(String pageName) {
        super(pageName);
    }

    public AbstractContainerChooserPage(String pageName, String title,
        ImageDescriptor titleImage) {
        super(pageName, title, titleImage);
    }

    @Override
    public void createControl(Composite parent) {
        pageContainer = new Composite(parent, SWT.NULL);
        GridLayout layout = new GridLayout(2, false);
        pageContainer.setLayout(layout);
        initComponent();
        setControl(pageContainer);
        setPageComplete(false);

        try {
            updateFreezerGrid();
        } catch (Exception e) {
            LOGGER.error("Could not save site preferences", e);
        }
    }

    protected void initComponent() {
        Composite gridParent = new Composite(pageContainer, SWT.NONE);
        gridParent.setLayout(new GridLayout(1, false));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        gridParent.setLayoutData(gd);
        containerWidget = new ContainerDisplayWidget(gridParent);
        List<ContainerStatus> legend = new ArrayList<ContainerStatus>();
        legend.add(ContainerStatus.FREE_LOCATIONS);
        legend.add(ContainerStatus.FULL);
        legend.add(ContainerStatus.NOT_INITIALIZED);
        containerWidget.setLegend(legend);
        containerWidget.setDefaultStatus(ContainerStatus.NOT_INITIALIZED);
        containerWidget.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseDown(MouseEvent e) {
                positionSelection(e);
            }
        });

        Label label = new Label(pageContainer, SWT.NONE);
        label.setText("Choosen position:");
        textPosition = new Text(pageContainer, SWT.READ_ONLY | SWT.BORDER
            | SWT.SINGLE);
        gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        textPosition.setLayoutData(gd);
    }

    protected ContainerCell positionSelection(MouseEvent e) {
        ContainerCell cell = containerWidget.getPositionAtCoordinates(e.x, e.y);
        if (cell == null || cell.getStatus() == ContainerStatus.NOT_INITIALIZED) {
            textPosition.setText("");
            setPageComplete(false);
        } else {
            ContainerPositionWrapper cp = cell.getPosition();
            if (cp != null && cp.getContainer() != null) {
                String code = cp.getContainer().getLabel();
                if (code != null) {
                    textPosition.setText(code);
                } else {
                    textPosition.setText(cp.getContainer().getLabel());
                }
                setPageComplete(true);
            }
        }
        return cell;
    }

    /**
     * Update grid representation according to the container displayed
     * 
     * @throws Exception
     */
    protected void updateFreezerGrid() {
        ContainerCell[][] cells = initGridSize();
        if (currentContainer != null) {
            // get cells informations
            for (ContainerPositionWrapper position : currentContainer
                .getChildPositionCollection()) {
                int positionDim1 = position.getRow();
                int positionDim2 = position.getCol();
                ContainerCell cell = new ContainerCell(position);
                ContainerWrapper occupiedContainer = position.getContainer();
                setStatus(cell, occupiedContainer);
                cells[positionDim1][positionDim2] = cell;
            }
            initEmptyCells(cells);
            containerWidget.setContainersStatus(cells);
        }
        containerWidget.setVisible(true);
    }

    @SuppressWarnings("unused")
    protected void initEmptyCells(ContainerCell[][] cells) {
        // do nothing per default : positions should be initialized in another
        // way
    }

    protected abstract void setStatus(ContainerCell cell,
        ContainerWrapper occupiedContainer);

    private ContainerCell[][] initGridSize() {
        int rowCap;
        int colCap;
        if (currentContainer == null) {
            rowCap = defaultDim1;
            colCap = defaultDim2;
        } else {
            rowCap = currentContainer.getContainerType().getRowCapacity();
            colCap = currentContainer.getContainerType().getColCapacity();
            Assert.isNotNull(rowCap, "row capacity is null");
            Assert.isNotNull(colCap, "column capacity is null");
        }
        int width;
        if (gridWidth == null) {
            width = pageContainer.getSize().x - 13;
        } else {
            width = gridWidth;
        }
        int height;
        ;
        if (gridHeight == null) {
            height = 300;
        } else {
            height = gridHeight;
        }
        containerWidget.setGridSizes(rowCap, colCap, width, height);
        return new ContainerCell[rowCap][colCap];
    }

    public void setCurrentContainer(ContainerWrapper container) {
        this.currentContainer = container;
    }

    public ContainerWrapper getCurrentContainer() {
        return currentContainer;
    }

    public SiteWrapper getSite() {
        return ((ContainerChooserWizard) getWizard()).getSite();
    }

    public WritableApplicationService getAppService() {
        return ((ContainerChooserWizard) getWizard()).getAppService();
    }

    protected ContainerPositionWrapper newContainerPosition(int dim1, int dim2) {
        ContainerPositionWrapper position = new ContainerPositionWrapper(
            getAppService());
        position.setParentContainer(currentContainer);
        position.setRow(dim1);
        position.setCol(dim2);
        return position;
    }

}