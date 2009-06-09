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
	protected void drawRectangle(PaintEvent e, Rectangle rectangle,
			int indexRow, int indexCol) {
		if (selectedBox != null && selectedBox[0] == indexRow
				&& selectedBox[1] == indexCol) {
			Color color = e.display.getSystemColor(SWT.COLOR_BLUE);
			e.gc.setBackground(color);
			e.gc.fillRectangle(rectangle);
		}
		super.drawRectangle(e, rectangle, indexRow, indexCol);
	}

	public void setSelectedBox(int[] selectedBox) {
		this.selectedBox = selectedBox;
		redraw();
	}

}
