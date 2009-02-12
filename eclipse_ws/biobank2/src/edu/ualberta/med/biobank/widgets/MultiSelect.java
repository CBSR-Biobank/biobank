package edu.ualberta.med.biobank.widgets;

import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
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
		//dragAndDropSupport(selList, availList);
	}
	
	private ListViewer createLabelledList(Composite parent, String label) {
		Composite selComposite = new Composite(parent, SWT.NONE);
		selComposite.setLayout(new GridLayout(1, true));
		
		ListViewer lv = new ListViewer(selComposite);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = minHeight;
		gd.widthHint = 180;
		lv.getList().setLayoutData(gd);
		lv.setContentProvider(new ContentProvider());
		lv.setLabelProvider(new LabelProvider());
		
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
		fromList.addDragSupport(DND.DROP_MOVE,
				new Transfer[] { TextTransfer.getInstance() },
				new ListViewerDragListener(fromList));
		
		toList.addDropSupport(DND.DROP_MOVE, 
				new Transfer[] { TextTransfer.getInstance() },
				new ListViewerDropListener(toList));
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

class ContentProvider implements IStructuredContentProvider {
	
	@SuppressWarnings("unchecked")
	public Object[] getElements(Object element) {
		return ((HashMap<Integer, String>) element).entrySet().toArray();
	}
	public void dispose() {
		// do nothing
	}
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		// do nothing
	}
}

class LabelProvider implements ILabelProvider {
	public Image getImage(Object element) {
		return null;
	}
	@Override
	public String getText(Object element) {
		return (String) element;
	}
	@Override
	public void addListener(ILabelProviderListener listener) {
		// do nothing
	}
	@Override
	public void dispose() {
		// do nothing
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// do nothing		
	}
	
}

class ListViewerDragListener implements DragSourceListener {
	private ListViewer viewer;

	public ListViewerDragListener(ListViewer viewer) {
		this.viewer = viewer;
	}

	public void dragStart(DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
	}

	public void dragSetData(DragSourceEvent event) {
		event.data = viewer.getSelection();
	}

	public void dragFinished(DragSourceEvent event) {
	}

}

class ListViewerDropListener extends ViewerDropAdapter {
	public ListViewerDropListener(ListViewer viewer) {
		super(viewer);
	}

	@Override
	public boolean performDrop(Object data) {
		String target = (String) getCurrentTarget();
		if (target == null)
			target = (String) getViewer().getInput();
		String[] toDrop = (String[])data;
		ListViewer viewer = (ListViewer) getViewer();

		for (int i = 0; i < toDrop.length; i++)
			if (toDrop[i].equals(target))
				return false;
		for (int i = 0; i < toDrop.length; i++) {
			viewer.add(toDrop[i]);
			viewer.reveal(toDrop[i]);
		}
		return true;	
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return TextTransfer.getInstance().isSupportedType(transferType);
	}

}
