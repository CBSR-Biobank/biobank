package edu.ualberta.med.biobank.widgets.listener;

import java.util.EventObject;

public class ScanPaletteModificationEvent extends EventObject {

	private static final long serialVersionUID = -1688078720650398637L;
	public int selections;

	public ScanPaletteModificationEvent(Object source, int selections) {
		super(source);
		this.selections = selections;
	}

}
