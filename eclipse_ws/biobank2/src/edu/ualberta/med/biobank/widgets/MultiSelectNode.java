package edu.ualberta.med.biobank.widgets;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.Assert;

public class MultiSelectNode {	
	private int id;
	
	private String name;
	
	protected MultiSelectNode parent;

	protected List<MultiSelectNode> children;
	
	public MultiSelectNode(MultiSelectNode parent) {
		this.parent = parent;
		children = new ArrayList<MultiSelectNode>();
	}
	
	public MultiSelectNode(MultiSelectNode parent, int id, String name) {
		this(parent);
		setId(id);
		setName(name);
	}
	
	public void setParent(MultiSelectNode parent) {
		this.parent = parent;
	}
	
	public MultiSelectNode getParent() {
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
	
	public List<MultiSelectNode> getChildren() {
		return children;
	}
	
	public void addChild(MultiSelectNode child) {
		child.setParent(this);
		children.add(child);
	}
	
	public void insertAfter(MultiSelectNode existingNode, MultiSelectNode newNode) {
		int pos = children.indexOf(existingNode);
		Assert.isTrue(pos >= 0, "existing node not found: " + existingNode.getName());
		newNode.setParent(this);
		children.add(pos + 1, newNode);
	}
	
	public void removeChild(MultiSelectNode item) {		
		if (children.size() == 0) return;
		
		MultiSelectNode itemToRemove = null;

		for (MultiSelectNode child : children) {
			if ((child.getId() == item.getId()) 
					&& child.getName().equals(item.getName()))
				itemToRemove = child;
		}
		
		if (itemToRemove != null) {
			children.remove(itemToRemove);
			System.out.println("removeChild: removed child: " + item.getName());
		}
	}
	
	public int getChildCount() {
		return children.size();
	}
}
