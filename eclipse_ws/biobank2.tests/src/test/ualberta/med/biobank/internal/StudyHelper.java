package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyHelper extends DbHelper {

    public static StudyWrapper newStudy(String name, String nameShort,
        SiteWrapper site) {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
        study.setNameShort(nameShort);
        study.setSite(site);
        return study;
    }

    public static StudyWrapper newStudy(SiteWrapper site, String name)
        throws Exception {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
        study.setSite(site);
        return study;
    }

    public static StudyWrapper addStudy(String name, String nameShort,
        SiteWrapper site) throws Exception {
        StudyWrapper study = newStudy(name, nameShort, site);
        study.persist();
        return study;
    }

    public static StudyWrapper addStudy(SiteWrapper site, String name)
        throws Exception {
        StudyWrapper study = newStudy(site, name);
        study.persist();
        return study;
    }

    public static void addStudies(SiteWrapper site, String name, int count)
        throws Exception {
        for (int i = 0; i < count; i++) {
            addStudy(site, name + (i + 1));
        }
        site.reload();
    }

    public static int addContactsToStudy(StudyWrapper study, String name)
        throws Exception {
        SiteWrapper site = study.getSite();
        ClinicHelper.addClinics(site, name, r.nextInt(15) + 1, true);
        List<ClinicWrapper> clinics = site.getClinicCollection();
        int nber = r.nextInt(clinics.size() - 1) + 1;
        List<ContactWrapper> contacts = new ArrayList<ContactWrapper>();
        for (int i = 0; i < nber; i++) {
            ClinicWrapper clinic = clinics.get(i);
            ContactWrapper contact = chooseRandomlyInList(clinic
                .getContactCollection());
            contacts.add(contact);
        }
        study.setContactCollection(contacts);
        study.persist();
        return nber;
    }
}
