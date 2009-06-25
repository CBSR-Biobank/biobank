package edu.ualberta.med.biobank.widgets;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;

public class ViewStorageContainerWidget extends AbstractGridContainerWidget {

	private Point selectedBox;

	public ViewStorageContainerWidget(Composite parent) {
		super(parent);
	}

	@Override
	protected void drawRectangle(PaintEvent e, Rectangle rectangle,
			int indexRow, int indexCol) {
		if (selectedBox != null && selectedBox.x == indexRow
				&& selectedBox.y == indexCol) {
			Color color = e.display.getSystemColor(SWT.COLOR_RED);
			e.gc.setBackground(color);
			e.gc.fillRectangle(rectangle);
		}
		super.drawRectangle(e, rectangle, indexRow, indexCol);
	}

	/**
	 * selection start at 0:0
	 */
	public void setSelectedBox(Point selectedBox) {
		this.selectedBox = selectedBox;
		redraw();
	}

}
