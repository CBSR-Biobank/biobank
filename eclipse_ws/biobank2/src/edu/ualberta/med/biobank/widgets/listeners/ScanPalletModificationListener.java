package edu.ualberta.med.biobank.widgets.listeners;

import java.util.EventListener;

public interface ScanPalletModificationListener extends EventListener {

	public void modification(ScanPalletModificationEvent spme);
}
