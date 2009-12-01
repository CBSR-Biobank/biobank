package edu.ualberta.med.biobank.widgets.grids;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.LabelingScheme;
import edu.ualberta.med.biobank.common.RowColPos;
import edu.ualberta.med.biobank.common.wrappers.ContainerTypeWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContainerWrapper;

/**
 * This class is there to give a common parent class to grid container widgets
 * and drawers widgets
 */
public abstract class AbstractContainerDisplayWidget extends Canvas {

    protected ContainerWrapper container;

    protected ContainerTypeWrapper containerType;
    /**
     * true if we want the container to display full info in each box displayed
     */
    protected boolean displayFullInfoString = false;

    public AbstractContainerDisplayWidget(Composite parent, int style) {
        super(parent, style);
    }

    public abstract void setSelection(RowColPos selection);

    public abstract Object getObjectAtCoordinates(int x, int y);

    public abstract void setInput(Object object);

    public abstract void initLegend();

    public void displayFullInfoString(boolean display) {
        this.displayFullInfoString = display;
    }

    public void setContainer(ContainerWrapper container) {
        this.container = container;
        if (container != null) {
            setContainerType(container.getContainerType());
        }
    }

    public void setContainerType(ContainerTypeWrapper type) {
        this.containerType = type;
    }

    /**
     * Get the text to write inside the cell. This default implementation use
     * the cell position and the containerType.
     */
    protected String getDefaultTextForBox(int indexRow, int indexCol) {
        RowColPos rowcol = new RowColPos();
        rowcol.row = indexRow;
        rowcol.col = indexCol;
        String parentLabel = "";
        if (displayFullInfoString && container != null) {
            parentLabel = container.getLabel();
        }
        if (containerType != null) {
            return parentLabel
                + LabelingScheme.getPositionString(rowcol, containerType);
        }
        return "";
    }

}
