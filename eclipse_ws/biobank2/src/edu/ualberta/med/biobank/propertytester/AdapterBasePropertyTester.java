package edu.ualberta.med.biobank.propertytester;

import org.eclipse.core.expressions.PropertyTester;

import edu.ualberta.med.biobank.gui.common.BgcLogger;
import edu.ualberta.med.biobank.treeview.AbstractAdapterBase;

public class AdapterBasePropertyTester extends PropertyTester {

    @SuppressWarnings("nls")
    public static final String CAN_DELETE = "canDelete";

    private static BgcLogger log = BgcLogger
        .getLogger(AdapterBasePropertyTester.class.getName());

    @SuppressWarnings("nls")
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
                log.error("Problem testing menus enablement", ex);
            }
        }
        return false;
    }
}
