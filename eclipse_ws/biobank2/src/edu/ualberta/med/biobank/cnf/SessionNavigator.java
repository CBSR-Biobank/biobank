package edu.ualberta.med.biobank.cnf;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.navigator.CommonNavigator;

import edu.ualberta.med.biobank.model.NavigatorRoot;

public class SessionNavigator extends CommonNavigator {

	@Override
	protected IAdaptable getInitialInput() {
		this.getCommonViewer().refresh();
		return new NavigatorRoot();
	}

}
