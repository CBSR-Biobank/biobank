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
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;

import edu.ualberta.med.biobank.model.ContainerCell;
import edu.ualberta.med.biobank.model.ContainerPosition;
import edu.ualberta.med.biobank.model.StorageContainer;
import edu.ualberta.med.biobank.model.StorageType;

public class PalettePositionChooserPage extends AbstractContainerChooserPage {

	public static final String NAME = "HOTEL_CONTAINER";
	private ContainerPosition selectedPosition;
	private StorageType storageType;

	private ComboViewer comboViewer;
	private Combo combo;

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

		Label label = new Label(pageContainer, SWT.NONE);
		label.setText("Choose container type:");
		combo = new Combo(pageContainer, SWT.NONE);
		comboViewer = new ComboViewer(combo);
		comboViewer.setContentProvider(new ArrayContentProvider());
		comboViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				StorageType st = (StorageType) element;
				return st.getName();
			}
		});

		comboViewer
			.addSelectionChangedListener(new ISelectionChangedListener() {
				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					// type has been chosen = page complete if position choosen
					if (!textPosition.getText().isEmpty()) {
						setPageComplete(true);
					}
					storageType = (StorageType) ((IStructuredSelection) comboViewer
						.getSelection()).getFirstElement();
				}
			});
	}

	@Override
	public void setCurrentStorageContainer(StorageContainer container) {
		super.setCurrentStorageContainer(container);
		setTitle("Container " + container.getName());
		updateFreezerGrid();
		textPosition.setText("");
		selectedPosition = null;
		Collection<StorageType> types = getCurrentStorageContainer()
			.getStorageType().getChildStorageTypeCollection();
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
		ContainerCell cell = super.positionSelection(e);
		if (cell != null) {
			this.selectedPosition = cell.getPosition();
		}
		if (comboViewer.getSelection() == null
				|| ((IStructuredSelection) comboViewer.getSelection())
					.isEmpty()) {
			setPageComplete(false);
		}
		return cell;
	}

	public ContainerPosition getSelectedPosition() {
		return selectedPosition;
	}

	public StorageType getStorageType() {
		return storageType;
	}

}
