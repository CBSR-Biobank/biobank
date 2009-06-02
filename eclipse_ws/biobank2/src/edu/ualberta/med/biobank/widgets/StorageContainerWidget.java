package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class StorageContainerWidget extends AbstractGridContainerWidget {

	private int[] selectedBox;

	public StorageContainerWidget(Composite parent) {
		super(parent);
	}

	@Override
	protected void specificDrawing(PaintEvent e, int indexRow, int indexCol,
			Rectangle rectangle) {
		if (selectedBox != null && selectedBox[0] == indexRow
				&& selectedBox[1] == indexCol) {
			Color color = e.display.getSystemColor(SWT.COLOR_BLUE);
			e.gc.setBackground(color);
			e.gc.fillRectangle(rectangle);
		}
	}

	public void setSelectedBox(int[] selectedBox) {
		this.selectedBox = selectedBox;
		redraw();
	}

}
