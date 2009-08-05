package edu.ualberta.med.biobank.widgets.listener;

import java.util.EventListener;

public interface ScanPalletModificationListener extends EventListener {

	public void modification(ScanPalletModificationEvent spme);
}
