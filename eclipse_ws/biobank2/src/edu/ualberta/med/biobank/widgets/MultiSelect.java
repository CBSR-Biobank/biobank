package edu.ualberta.med.biobank.widgets;

import java.util.HashMap;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.viewers.ViewerDropAdapter;

import edu.ualberta.med.biobank.forms.FormUtils;

public class MultiSelect extends Composite {
	private ListViewer selList;
	
	private ListViewer availList;
	
	private int minHeight;

	public MultiSelect(Composite parent, int style, String leftLabel, 
			String rightLabel, int minHeight) {
		super(parent, style);
		
		this.minHeight = minHeight;
		
		setLayout(new GridLayout(3, false));
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		Composite buttons = new Composite(this, SWT.NONE);
		buttons.setLayout(new GridLayout(1, true));
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = SWT.TOP;
		buttons.setLayoutData(gd);
		
		Button upButton = new Button(buttons, SWT.PUSH);
		upButton.setText("Move Up");
		upButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
		
		Button downButton = new Button(buttons, SWT.PUSH);
		downButton.setText("Move Down");
		downButton.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));

		selList = createLabelledList(this, leftLabel);
		availList = createLabelledList(this, rightLabel);
		
		dragAndDropSupport(availList, selList);
		dragAndDropSupport(selList, availList);
	}
	
	private ListViewer createLabelledList(Composite parent, String label) {
		Composite selComposite = new Composite(parent, SWT.NONE);
		selComposite.setLayout(new GridLayout(1, true));
		
		ListViewer lv = new ListViewer(selComposite);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = minHeight;
		gd.widthHint = 180;
		lv.getList().setLayoutData(gd);
		
		Label l = new Label(selComposite, SWT.NONE);
		l.setText(label);
		l.setFont(FormUtils.getHeadingFont());
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.CENTER;
		l.setLayoutData(gd);
		
		return lv;
	}
	
	private void dragAndDropSupport(ListViewer fromList, ListViewer toList) {
		new ListViewerDragListener(fromList);
		new ListViewerDropListener(toList);
	}

	public void adaptToToolkit(FormToolkit toolkit) {
		adaptAllChildren(this, toolkit);
	}
	
	private void adaptAllChildren(Composite container, FormToolkit toolkit) {
		Control[] children = container.getChildren();
		for (Control aChild : children) {
			toolkit.adapt(aChild, true, true);
			if (aChild instanceof Composite) {
				adaptAllChildren((Composite) aChild, toolkit);
			}
		}
	}
	
	public void addAvailable(HashMap<Integer, String> available) {
		for (int key : available.keySet()) {
			availList.add(available.get(key));
		}
	}
}

class ListViewerDragListener implements DragSourceListener {
	private ListViewer viewer;

	public ListViewerDragListener(ListViewer viewer) {
		this.viewer = viewer;
		
		viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
				new Transfer[] { TextTransfer.getInstance() },
				this);
	}

	public void dragStart(DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
	}

	public void dragFinished(DragSourceEvent event) {
		if (event.doit) {
			viewer.remove(((IStructuredSelection) 
					viewer.getSelection()).getFirstElement());
		}
	}

}

class ListViewerDropListener extends ViewerDropAdapter {	
	public ListViewerDropListener(ListViewer viewer) {
		super(viewer);
		viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, 
				new Transfer[] { TextTransfer.getInstance() },
				this);
	}

	@Override
	public boolean performDrop(Object data) {
		String target = (String) getCurrentTarget();
		if (target == null)
			target = (String) getViewer().getInput();
		String toDrop = (String)data;
		ListViewer viewer = (ListViewer) getViewer();

		if (toDrop.equals(target)) return false;
		
		viewer.add(toDrop);
		viewer.reveal(toDrop);
		return true;	
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return TextTransfer.getInstance().isSupportedType(transferType);
	}

}
