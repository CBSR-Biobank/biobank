package edu.ualberta.med.biobank.wizard;

import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ComboViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Container;
import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerStatus;
import edu.ualberta.med.biobank.model.ContainerType;
import edu.ualberta.med.biobank.model.ModelUtils;
import gov.nih.nci.system.applicationservice.ApplicationException;

public class TopContainerChooserPage extends AbstractContainerChooserPage {

    public static final String NAME = "FIRST_CONTAINER";

    public TopContainerChooserPage() {
        super(NAME);
        setTitle("Main container");
        setDescription("Choose main container");
        gridHeight = 100;
    }

    @Override
    protected void initComponent() {
        Label label = new Label(pageContainer, SWT.NULL);
        label.setText("Choose first container:");
        Combo combo = new Combo(pageContainer, SWT.NONE);
        final ComboViewer comboViewer = new ComboViewer(combo);
        comboViewer.setContentProvider(new ArrayContentProvider());
        comboViewer.setLabelProvider(new LabelProvider() {
            @Override
            public String getText(Object element) {
                Container sc = (Container) element;
                return sc.getLabel() + " (" + sc.getContainerType().getName()
                    + ')';
            }
        });
        try {
            comboViewer.setInput(ModelUtils.getTopContainersForSite(
                getAppService(), getSite()));
        } catch (ApplicationException e) {
            BioBankPlugin.openError("Error",
                "Error retrieving containers informations from database");
        }
        comboViewer
            .addSelectionChangedListener(new ISelectionChangedListener() {
                @Override
                public void selectionChanged(SelectionChangedEvent event) {
                    setCurrentContainer((Container) ((IStructuredSelection) comboViewer
                        .getSelection()).getFirstElement());
                    updateFreezerGrid();
                    pageContainer.layout(true, true);
                    textPosition.setText("");
                    setPageComplete(false);
                }
            });

        super.initComponent();
        containerWidget.setVisible(false);
        try {
            // FIXME - homogenise
            List<ContainerType> types = ModelUtils.queryProperty(
                getAppService(), ContainerType.class, "name", "Freezer", false);
            if (types.size() > 0) {
                containerWidget.setParams(types.get(0), null);
            }
        } catch (ApplicationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected ContainerCell positionSelection(MouseEvent e) {
        ContainerCell cell = super.positionSelection(e);
        if (cell != null) {
            PalletPositionChooserPage nextPage = (PalletPositionChooserPage) getNextPage();
            try {
                nextPage.setCurrentContainer(cell.getPosition().getContainer());
            } catch (ArrayIndexOutOfBoundsException aiobe) {
                setPageComplete(false);
                SessionManager.getLogger().error("Index error", aiobe);
            }
        }
        return cell;
    }

    @Override
    protected void setStatus(ContainerCell cell, Container occupiedContainer) {
        Boolean full = occupiedContainer.getFull();
        if (full == null) {
            full = Boolean.FALSE;
        }
        int total = 0;
        if (!full) {
            // check if we can add a pallet in the hotel
            if (occupiedContainer.getChildPositionCollection() != null) {
                total = occupiedContainer.getChildPositionCollection().size();
            }
            int capacityTotal = occupiedContainer.getContainerType()
                .getCapacity().getDimensionOneCapacity()
                * occupiedContainer.getContainerType().getCapacity()
                    .getDimensionTwoCapacity();
            full = (total == capacityTotal);
        }
        if (full) {
            cell.setStatus(ContainerStatus.FULL);
        } else {
            cell.setStatus(ContainerStatus.FREE_LOCATIONS);
        }
    }
}
