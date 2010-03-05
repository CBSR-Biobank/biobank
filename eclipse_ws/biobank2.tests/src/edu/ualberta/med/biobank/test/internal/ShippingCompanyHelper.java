package edu.ualberta.med.biobank.test.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ShippingCompanyWrapper;

public class ShippingCompanyHelper extends DbHelper {

    public static List<ShippingCompanyWrapper> createdCompanies = new ArrayList<ShippingCompanyWrapper>();

    public static ShippingCompanyWrapper newShippingCompany(String name) {
        ShippingCompanyWrapper company = new ShippingCompanyWrapper(appService);
        company.setName(name);
        return company;
    }

    public static ShippingCompanyWrapper addShippingCompany(String name,
        boolean addToCreatedList) throws Exception {
        ShippingCompanyWrapper company = newShippingCompany(name);
        company.persist();
        if (addToCreatedList) {
            createdCompanies.add(company);
        }
        return company;
    }

    public static ShippingCompanyWrapper addShippingCompany(String name)
        throws Exception {
        return addShippingCompany(name, true);
    }

    public static void deleteCreateShippingCompanies() throws Exception {
        for (ShippingCompanyWrapper company : createdCompanies) {
            company.reload();
            company.delete();
        }
        createdCompanies.clear();
    }

}
