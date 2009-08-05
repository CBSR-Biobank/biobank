package edu.ualberta.med.biobank.wizard;

import java.util.Collection;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.model.ContainerType;

public class PalletPositionChooserPage extends AbstractContainerChooserPage {

    public static final String NAME = "HOTEL_CONTAINER";
    private ContainerPosition selectedPosition;
    private ContainerType containerType;

    private ComboViewer comboViewer;
    private Combo combo;

    protected PalletPositionChooserPage() {
        super(NAME);
        setDescription("Choose position in container");
        gridWidth = 60;
        defaultDim1 = 19;
        defaultDim2 = 1;
    }

    @Override
    protected void initComponent() {
        super.initComponent();
        pageContainer.layout(true, true);
        containerWidget.setLegendOnSide(true);
        containerWidget.setFirstColSign(null);
        containerWidget.setFirstRowSign(1);
        containerWidget.setShowNullStatusAsEmpty(true);

        Label label = new Label(pageContainer, SWT.NONE);
        label.setText("Choose container type:");
        combo = new Combo(pageContainer, SWT.NONE);
        GridData gd = new GridData();
        gd.grabExcessHorizontalSpace = true;
        gd.horizontalAlignment = SWT.FILL;
        combo.setLayoutData(gd);
        comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                ContainerType st = (ContainerType) element;
                return st.getName();
            }
        });

        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    // type has been chosen = page complete if position
                    // choosen
                    if (!textPosition.getText().isEmpty()) {
                        setPageComplete(true);
                    }
                    containerType = (ContainerType) ((IStructuredSelection) comboViewer
                        .getSelection()).getFirstElement();
                }
            });
    }

    @Override
    public void setCurrentContainer(Container container) {
        super.setCurrentContainer(container);
        setTitle("Container " + container.getLabel());
        updateFreezerGrid();
        textPosition.setText("");
        selectedPosition = null;
        Collection<ContainerType> types = getCurrentContainer()
            .getContainerType().getChildContainerTypeCollection();
        // TODO do not include type not active
        comboViewer.setInput(types);
        if (types.size() == 1) {
            comboViewer
                .setSelection(new StructuredSelection(types.toArray()[0]));
        }
        setPageComplete(false);
    }

    @Override
    protected ContainerCell positionSelection(MouseEvent e) {
        boolean complete = false;
        ContainerCell cell = containerWidget.getPositionAtCoordinates(e.x, e.y);
        if (cell.getStatus() == ContainerStatus.EMPTY) {
            this.selectedPosition = cell.getPosition();
            int positionText = selectedPosition.getPositionDimensionOne() + 1;
            textPosition.setText(String.valueOf(positionText));
            complete = true;
        } else {
            textPosition.setText("");
            complete = false;
        }
        if (complete) {
            if (comboViewer.getSelection() == null
                || ((IStructuredSelection) comboViewer.getSelection())
                    .isEmpty()) {
                setPageComplete(false);
            }
        }
        setPageComplete(complete);
        return cell;
    }

    public ContainerPosition getSelectedPosition() {
        return selectedPosition;
    }

    public ContainerType getContainerType() {
        return containerType;
    }

    @Override
    protected void setStatus(ContainerCell cell, Container occupiedContainer) {
        if (occupiedContainer == null) {
            cell.setStatus(ContainerStatus.EMPTY);
        } else {
            cell.setStatus(ContainerStatus.FILLED);
        }
    }

    @Override
    protected void initEmptyCells(ContainerCell[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j] == null) {
                    ContainerCell cell = new ContainerCell(
                        newContainerPosition(i, j));
                    cell.setStatus(ContainerStatus.EMPTY);
                    cells[i][j] = cell;
                }
            }
        }
    }

}
