package edu.ualberta.med.biobank.treeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class Node {
	
	private int id;
	
	private String name;
	
	protected Node parent;
	
	protected boolean hasChildren;

	protected List<Node> children;
	
	public Node(Node parent) {
		this.parent = parent;
		children = new ArrayList<Node>();
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
	
	public Node getChild(int i) {
		Assert.isTrue(i < children.size(), "Invalid index: " + i);
		return children.get(i);
	}
	
	public void addChild(Node child) {	
		if (contains(child)) {
			// don't add - assume our model is up to date 
			return;
		}

		Node namedChild = getChildByName(child.getName());
		if (namedChild != null) {
			// may have inserted a new object into database
			// replace current object with new one
			int index = children.indexOf(namedChild);
			children.remove(index);
			children.add(index, child);
			return;
		}
			
		children.add(child);
	}
	
	public void remove(Node item) {		
		if (children.size() == 0) return;
		
		Node itemToRemove = null;

		for (Node child : children) {
			if ((child.getId() == item.getId()) 
					&& child.getName().equals(item.getName()))
				itemToRemove = child;
		}
		
		if (itemToRemove != null)
			children.remove(itemToRemove);
	}

	public void removeByName(String name) {	
		if (children.size() == 0) return;
		
		Node itemToRemove = null;

		for (Node child : children) {
			if (child.getName().equals(name))
				itemToRemove = child;
		}
		
		if (itemToRemove != null)
			children.remove(itemToRemove);
	}
	
	public boolean contains(Node item) {		
		if (children.size() == 0) return false;

		for (Node child : children) {
			if ((child.getId() == item.getId()) 
					&& child.getName().equals(item.getName()))
				return true;
		}
		return false;
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
}
