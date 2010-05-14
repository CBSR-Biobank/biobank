package edu.ualberta.med.biobank.server.validator;

import org.hibernate.validator.Validator;

public class SiteNameUniqueValidator implements Validator<SiteNameUnique> {

    @Override
    public void initialize(SiteNameUnique parameters) {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isValid(Object value) {
        // System.out.println("*********************" + value.getClass());
        // HQLCriteria c;
        // if (isNew()) {
        // c = new HQLCriteria("from " + Site.class.getName()
        // + " where name = ?", Arrays.asList(new Object[] { getName() }));
        // } else {
        // c = new HQLCriteria("from " + Site.class.getName()
        // + " where id <> ? and name = ?", Arrays.asList(new Object[] {
        // getId(), getName() }));
        // }
        //
        // List<Object> results = appService.query(c);
        // return (results.size() == 0);
        return true;
    }

}
