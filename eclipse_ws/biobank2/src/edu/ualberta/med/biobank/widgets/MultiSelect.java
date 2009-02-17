package edu.ualberta.med.biobank.widgets;

import java.util.HashMap;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.jface.viewers.ViewerDropAdapter;

import edu.ualberta.med.biobank.forms.FormUtils;

public class MultiSelect extends Composite {
	private TreeViewer selTree;
	
	private TreeViewer availTree;
	
	private MultiSelectNode selTreeRootNode = new MultiSelectNode(null, 0, "selRoot");
	
	private MultiSelectNode availTreeRootNode = new MultiSelectNode(null, 0, "availRoot");
	
	private int minHeight;

	public MultiSelect(Composite parent, int style, String leftLabel, 
			String rightLabel, int minHeight) {
		super(parent, style);
		
		this.minHeight = minHeight;
		
		setLayout(new GridLayout(2, false));
		setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		selTree = createLabelledTree(this, leftLabel);
		selTree.setInput(selTreeRootNode);
		availTree = createLabelledTree(this, rightLabel);
		availTree.setInput(availTreeRootNode);
		
		dragAndDropSupport(availTree, selTree);
		dragAndDropSupport(selTree, availTree);
	}
	
	private TreeViewer createLabelledTree(Composite parent, String label) {
		Composite selComposite = new Composite(parent, SWT.NONE);
		selComposite.setLayout(new GridLayout(1, true));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		selComposite.setLayoutData(gd);
		
		TreeViewer tv = new TreeViewer(selComposite);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = minHeight;
		gd.widthHint = 180;
		tv.getTree().setLayoutData(gd);
		
		Label l = new Label(selComposite, SWT.NONE);
		l.setText(label);
		l.setFont(FormUtils.getHeadingFont());
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 2;
		gd.horizontalAlignment = SWT.CENTER;
		l.setLayoutData(gd);

		tv.setLabelProvider(new ILabelProvider(){

			@Override
			public Image getImage(Object element) {
				return null;
			}

			@Override
			public String getText(Object element) {
				return ((MultiSelectNode) element).getName();
			}

			@Override
			public void addListener(ILabelProviderListener listener) {				
			}

			@Override
			public void dispose() {				
			}

			@Override
			public boolean isLabelProperty(Object element, String property) {
				return false;
			}

			@Override
			public void removeListener(ILabelProviderListener listener) {				
			}
		});
		
		tv.setContentProvider(new MultiSelectNodeContentProvider());
		
		return tv;
	}
	
	private void dragAndDropSupport(TreeViewer fromList, TreeViewer toList) {
		new TreeViewerDragListener(fromList);
		new TreeViewerDropListener(toList);
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
			availTreeRootNode.addChild(new MultiSelectNode(availTreeRootNode, key, available.get(key)));
		}
	}
}

/**
 * Drag support for moving items between TreeViewers in this widget.
 *
 */
class TreeViewerDragListener implements DragSourceListener {
	private TreeViewer viewer;
	
	private MultiSelectNode[] dragData;

	public TreeViewerDragListener(TreeViewer viewer) {
		this.viewer = viewer;
		
		viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
				new Transfer[] { MultiSelectNodeTransfer.getInstance() },
				this);
	}

	public void dragStart(DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
		System.out.println("dragStart: " + event.toString());
	}

	public void dragSetData(DragSourceEvent event) {
		Object[] selections = ((IStructuredSelection) viewer.getSelection()).toArray();
		
		int count = 0;
		MultiSelectNode[] nodes = new MultiSelectNode[selections.length];
		for (Object sel : selections) {
			nodes[count] = (MultiSelectNode) sel;
			++count;
		}
		event.data = nodes;
		dragData = nodes;
		System.out.println("dragSetData: " + event.toString());
	}

	public void dragFinished(DragSourceEvent event) {
		if (!event.doit) return;

		MultiSelectNode rootNode = (MultiSelectNode) viewer.getInput();
		for (MultiSelectNode node : dragData) {
			rootNode.removeChild(node);
			System.out.println("removed " + node.getName()
					+ " from " + rootNode.getName()
					+ ", event: " + event.toString());
		}
	}
}

/**
 * Drop support for moving items between TreeViewers in this widget.
 *
 */
class TreeViewerDropListener extends ViewerDropAdapter {	
	public TreeViewerDropListener(TreeViewer viewer) {
		super(viewer);
		viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, 
				new Transfer[] { MultiSelectNodeTransfer.getInstance() },
				this);
	}

	@Override
	public boolean performDrop(Object data) {
		boolean result = true;
		
		System.out.println("performDrop: event: " + data.toString());
		MultiSelectNode target = (MultiSelectNode) getCurrentTarget();
		if (target == null)
			target = (MultiSelectNode) getViewer().getInput();
		
		MultiSelectNode[] nodes = (MultiSelectNode[]) data;
		
		TreeViewer viewer = (TreeViewer) getViewer();
	
		for (MultiSelectNode node : nodes) {
			System.out.println("target: " + target + ", node_parent: " + node.getParent());
			
			if (target.getParent() == null) {
				target.addChild(node);
				System.out.println("added " + node.getName()
						+ " to " + target.getName());
			}
			else {
				target.getParent().insertAfter(target, node);
				System.out.println("inserted " + node.getName()
						+ " after " + target.getName() 
						+ " on "+ target.getParent().getName());
			}
			viewer.reveal(node);
		}
		return result;	
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return MultiSelectNodeTransfer.getInstance().isSupportedType(transferType);
	}

}

