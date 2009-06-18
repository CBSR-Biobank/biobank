package edu.ualberta.med.biobank.wizard;

import org.eclipse.swt.events.MouseEvent;

import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.StorageContainer;

public class PalettePositionChooserPage extends AbstractContainerChooserPage {

	public static final String NAME = "HOTEL_CONTAINER";
	private ContainerPosition selectedPosition;

	protected PalettePositionChooserPage() {
		super(NAME);
		setDescription("Choose position in container");
		gridWidth = 60;
		defaultDim1 = 19;
		defaultDim2 = 13;
	}

	@Override
	protected void initComponent() {
		super.initComponent();
		pageContainer.layout(true, true);
		containerWidget.setLegendOnSide(true);
		containerWidget.setFirstColSign(null);
		containerWidget.setFirstRowSign(1);
	}

	@Override
	public void setCurrentStorageContainer(StorageContainer container) {
		super.setCurrentStorageContainer(container);
		setTitle("Container " + container.getName());
		updateFreezerGrid();
	}

	@Override
	protected ContainerCell positionSelection(MouseEvent e) {
		ContainerCell cell = super.positionSelection(e);
		if (cell != null) {
			this.selectedPosition = cell.getPosition();
		}
		return cell;
	}

	public ContainerPosition getSelectedPosition() {
		return selectedPosition;
	}
}
