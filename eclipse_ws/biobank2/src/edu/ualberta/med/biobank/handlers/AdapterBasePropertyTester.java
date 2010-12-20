package edu.ualberta.med.biobank.handlers;

import org.eclipse.core.expressions.PropertyTester;

import edu.ualberta.med.biobank.treeview.AdapterBase;

public class AdapterBasePropertyTester extends PropertyTester {

    public AdapterBasePropertyTester() {
    }

    @Override
    public boolean test(Object receiver, String property, Object[] args,
        Object expectedValue) {
        if (receiver instanceof AdapterBase) {
            AdapterBase adapter = (AdapterBase) receiver;
            if (property.equals("canDelete")) {
                boolean can = adapter.isDeletable();
                return can;
            }
        }
        return false;
    }
}
