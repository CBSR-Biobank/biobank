package edu.ualberta.med.biobank.widgets;

import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
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
import edu.ualberta.med.biobank.treeview.Node;
import edu.ualberta.med.biobank.treeview.NodeContentProvider;
import edu.ualberta.med.biobank.treeview.NodeLabelProvider;
import edu.ualberta.med.biobank.treeview.NodeTransfer;

public class MultiSelect extends Composite {
	private TreeViewer selTree;
	
	private TreeViewer availTree;
	
	private Node selTreeRootNode = new Node(null, 0, "selRoot");
	
	private Node availTreeRootNode = new Node(null, 0, "availRoot");
	
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
		
		TreeViewer tv = new TreeViewer(selComposite);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
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

		tv.setLabelProvider(new NodeLabelProvider());
		tv.setContentProvider(new NodeContentProvider());
		
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
			availTreeRootNode.addChild(new Node(availTreeRootNode, key, available.get(key)));
		}
	}
}

class TreeViewerDragListener implements DragSourceListener {
	private TreeViewer viewer;
	
	private Node[] dragData;

	public TreeViewerDragListener(TreeViewer viewer) {
		this.viewer = viewer;
		
		viewer.addDragSupport(DND.DROP_MOVE | DND.DROP_COPY,
				new Transfer[] { NodeTransfer.getInstance() },
				this);
	}

	public void dragStart(DragSourceEvent event) {
		event.doit = !viewer.getSelection().isEmpty();
		System.out.println(event.toString());
	}

	public void dragSetData(DragSourceEvent event) {
		Object[] selections = ((IStructuredSelection) viewer.getSelection()).toArray();
		
		int count = 0;
		Node[] nodes = new Node[selections.length];
		for (Object sel : selections) {
			nodes[count] = (Node) sel;
			++count;
		}
		event.data = nodes;
		dragData = nodes;
		System.out.println(event.toString());
	}

	public void dragFinished(DragSourceEvent event) {
		if (!event.doit) return;

		Node rootNode = (Node) viewer.getInput();
		for (Node node : dragData) {
			rootNode.removeChild(node);
			System.out.println("removed " + node.getName()
					+ " from " + rootNode.getName());
		}
	}
}

class TreeViewerDropListener extends ViewerDropAdapter {	
	public TreeViewerDropListener(TreeViewer viewer) {
		super(viewer);
		viewer.addDropSupport(DND.DROP_MOVE | DND.DROP_COPY, 
				new Transfer[] { NodeTransfer.getInstance() },
				this);
	}

	@Override
	public boolean performDrop(Object data) {
		Node target = (Node) getCurrentTarget();
		if (target == null)
			target = (Node) getViewer().getInput();
		
		Node[] nodes = (Node[]) data;
		
		TreeViewer viewer = (TreeViewer) getViewer();
	
		for (Node node : nodes) {
			if (target.getParent() == null) {
				target.addChild(node);
				viewer.reveal(node);
				System.out.println("added " + node.getName()
						+ " to " + target.getName());
			}
			else {
				target.getParent().insertAfter(target, node);
				viewer.reveal(node);
				System.out.println("added " + node.getName()
						+ " to " + target.getParent().getName());
				
			}
		}
		return true;	
	}

	@Override
	public boolean validateDrop(Object target, int operation,
			TransferData transferType) {
		return NodeTransfer.getInstance().isSupportedType(transferType);
	}

}

