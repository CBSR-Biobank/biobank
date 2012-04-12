package edu.ualberta.med.biobank.expressions;

import org.eclipse.core.expressions.PropertyTester;

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;

public class AdapterBasePropertyTester extends PropertyTester {

    public static final String CAN_DELETE = "canDelete"; //$NON-NLS-1$

    private static BgcLogger logger = BgcLogger
        .getLogger(AdapterBasePropertyTester.class.getName());

    @Override
    public boolean test(Object receiver, String property, Object[] args,
        Object expectedValue) {
        if (receiver instanceof AbstractAdapterBase) {
            AbstractAdapterBase adapter =
                ((AbstractAdapterBase) receiver);
            try {
                if (CAN_DELETE.equals(property))
                    return adapter.isDeletable();
            } catch (Exception ex) {
                logger.error("Problem testing menus enablement", ex); //$NON-NLS-1$
            }
        }
        return false;
    }
}
