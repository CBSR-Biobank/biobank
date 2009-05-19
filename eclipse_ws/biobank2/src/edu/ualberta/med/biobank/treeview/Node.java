package edu.ualberta.med.biobank.treeview;

import edu.ualberta.med.biobank.forms.input.FormInput;
import gov.nih.nci.system.applicationservice.WritableApplicationService;
import org.eclipse.core.runtime.Assert;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.jface.viewers.TreeViewer;

import java.util.ArrayList;
import java.util.List;

public class Node {
	
	protected IDeltaListener listener = NullDeltaListener.getSoleInstance();
	
	private int id;
	
	private String name;
	
	protected Node parent;
	
	protected boolean hasChildren;

	protected List<Node> children;
	
	public Node(Node parent) {
		this.parent = parent;
		children = new ArrayList<Node>();
        if (parent != null) {
            addListener(parent.listener);
        }
	}
	
	public Node(Node parent, int id, String name) {
		this(parent);
		setId(id);
		setName(name);
	}
	
	public Node(Node parent, int id, String name, boolean hasChildren) {
		this(parent, id, name);
		setHasChildren(hasChildren);
	}
	
	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public Node getParent() {
		return parent;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public List<Node> getItems() {
		return children;
	}
	
	public List<Node> getChildren() {
		return children;
	}
	
	public Node getChild(int id) {
		if (children.size() == 0) return null;
		
		for (Node child : children) {
			if (child.getId() == id) return child;	
		}	
		return null;
	}
	
	public void addChild(Node child) {	
		hasChildren = true;
		Node existingNode = contains(child); 
		if (existingNode != null) {
			// don't add - assume our model is up to date 
			return;
		}

		Node namedChild = getChildByName(child.getName());
		if (namedChild != null) {
			// may have inserted a new object into database
			// replace current object with new one
			int index = children.indexOf(namedChild);
			children.remove(index);
		}

		child.setParent(this);
		children.add(child);
		child.addListener(listener);
		fireAdd(child);
	}
	
	public void insertAfter(Node existingNode, Node newNode) {
		int pos = children.indexOf(existingNode);
		Assert.isTrue(pos >= 0, "existing node not found: " + existingNode.getName());
		newNode.setParent(this);
		children.add(pos + 1, newNode);
		newNode.addListener(listener);
		fireAdd(newNode);
	}
	
	public void removeChild(Node item) {		
		if (children.size() == 0) return;
		
		Node itemToRemove = null;

		for (Node child : children) {
			if ((child.getId() == item.getId()) 
					&& child.getName().equals(item.getName()))
				itemToRemove = child;
		}
		
		if (itemToRemove != null) {
			children.remove(itemToRemove);
			fireRemove(itemToRemove);
		}
	}

	public void removeByName(String name) {	
		if (children.size() == 0) return;
		
		Node itemToRemove = null;

		for (Node child : children) {
			if (child.getName().equals(name))
				itemToRemove = child;
		}
		
		if (itemToRemove != null) {
			children.remove(itemToRemove);
			fireRemove(itemToRemove);
		}
	}
	
	public Node contains(Node item) {		
		if (children.size() == 0) return null;

		for (Node child : children) {
			if ((child.getId() == item.getId()) 
					&& child.getName().equals(item.getName()))
				return child;
		}
		return null;
	}
	
	public Node getChildByName(String name) {
		if (children.size() == 0) return null;
		
		for (Node child : children) {
			if (child.getName().equals(name)) return child;	
		}	
		return null;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}

	public boolean hasChildren() {
		return hasChildren;
	}
	
	public WritableApplicationService getAppService() {
		Assert.isNotNull(parent, "parent is null");
		return parent.getAppService();
	}
	
	public void addListener(IDeltaListener listener) {
		this.listener = listener;
	}	
	
	public void removeListener(IDeltaListener listener) {
		if(this.listener.equals(listener)) {
			this.listener = NullDeltaListener.getSoleInstance();
		}
	}
	
	protected void fireAdd(Object added) {
		listener.add(new DeltaEvent(added));
	}

	protected void fireRemove(Object removed) {
		listener.remove(new DeltaEvent(removed));
	}
    
    public void performDoubleClick() {
        Assert.isTrue(false, "should be implemented by derived class: "
            + this.getName());
    }
    
    public void performExpand() {
        Assert.isTrue(false, "should be implemented by derived class: "
            + this.getName());
    }
    
    @SuppressWarnings("unused")
    public void popupMenu(TreeViewer tv, Tree tree,  Menu menu) {
        Assert.isTrue(false, "should be implemented by derived class: "
            + this.getName());
    }
    
    public void closeEditor(FormInput input) {
        IWorkbenchPage page = 
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        IEditorPart part = page.findEditor(input);
        if (part != null) {
            page.closeEditor(part, true);
        }
    }
    
    public void openForm(FormInput input, String id) {
        closeEditor(input);
        try {
            PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
            .openEditor(input, id, true);
        } 
        catch (PartInitException e) {
            // handle error
            e.printStackTrace();                
        }
    }
}
