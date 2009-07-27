package edu.ualberta.med.biobank.wizard;

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

import edu.ualberta.med.biobank.model.Capacity;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.model.Site;
import edu.ualberta.med.biobank.widgets.ChooseContainerWidget;
import gov.nih.nci.system.applicationservice.WritableApplicationService;

public abstract class AbstractContainerChooserPage extends WizardPage {

    private Container currentContainer;

    protected ChooseContainerWidget containerWidget;

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
        updateFreezerGrid();
    }

    protected void initComponent() {
        Composite gridParent = new Composite(pageContainer, SWT.NONE);
        gridParent.setLayout(new GridLayout(1, false));
        GridData gd = new GridData();
        gd.horizontalAlignment = SWT.CENTER;
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalSpan = 2;
        gridParent.setLayoutData(gd);
        containerWidget = new ChooseContainerWidget(gridParent);
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
        if (cell == null || cell.getStatus() == ContainerStatus.FILLED) {
            textPosition.setText("");
            setPageComplete(false);
        } else {
            ContainerPosition cp = cell.getPosition();
            if (cp != null && cp.getContainer() != null) {
                String code = cp.getContainer().getProductBarcode();
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
     */
    protected void updateFreezerGrid() {
        ContainerCell[][] cells = initGridSize();
        if (currentContainer != null) {
            // get cells informations
            for (ContainerPosition position : currentContainer
                .getChildPositionCollection()) {
                int positionDim1 = position.getPositionDimensionOne() - 1;
                int positionDim2 = position.getPositionDimensionTwo() - 1;
                ContainerCell cell = new ContainerCell(position);
                Container occupiedContainer = position.getContainer();
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
        Container occupiedContainer);

    private ContainerCell[][] initGridSize() {
        int dim1;
        int dim2;
        if (currentContainer == null) {
            dim1 = defaultDim1;
            dim2 = defaultDim2;
        } else {
            Capacity capacity = currentContainer.getContainerType()
                .getCapacity();
            dim1 = capacity.getDimensionOneCapacity();
            dim2 = capacity.getDimensionTwoCapacity();
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
        containerWidget.setGridSizes(dim1, dim2, width, height);
        return new ContainerCell[dim1][dim2];
    }

    public void setCurrentContainer(Container container) {
        this.currentContainer = container;
    }

    public Container getCurrentContainer() {
        return currentContainer;
    }

    public Site getSite() {
        return ((ContainerChooserWizard) getWizard()).getSite();
    }

    public WritableApplicationService getAppService() {
        return ((ContainerChooserWizard) getWizard()).getAppService();
    }

    protected ContainerPosition newContainerPosition(int dim1, int dim2) {
        ContainerPosition position = new ContainerPosition();
        position.setParentContainer(currentContainer);
        position.setPositionDimensionOne(dim1);
        position.setPositionDimensionTwo(dim2);
        return position;
    }

}