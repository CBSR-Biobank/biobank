package edu.ualberta.med.biobank.widgets.listener;

import java.util.EventListener;

public interface ScanPaletteModificationListener extends EventListener {

	public void modification(ScanPaletteModificationEvent spme);
}
