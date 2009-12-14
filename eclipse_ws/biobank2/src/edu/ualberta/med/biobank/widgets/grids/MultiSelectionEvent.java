package edu.ualberta.med.biobank.widgets.grids;

import java.util.EventObject;

public class MultiSelectionEvent extends EventObject {

	private static final long serialVersionUID = -1688078720650398637L;
	public int selections;

	public MultiSelectionEvent(Object source, int selections) {
		super(source);
		this.selections = selections;
	}

}
