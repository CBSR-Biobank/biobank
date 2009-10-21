package test.ualberta.med.biobank.internal;

import java.util.ArrayList;
import java.util.List;

import edu.ualberta.med.biobank.common.wrappers.ClinicWrapper;
import edu.ualberta.med.biobank.common.wrappers.ContactWrapper;
import edu.ualberta.med.biobank.common.wrappers.SampleSourceWrapper;
import edu.ualberta.med.biobank.common.wrappers.SiteWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;

public class StudyHelper extends DbHelper {

    public static StudyWrapper newStudy(SiteWrapper site, String name)
        throws Exception {
        StudyWrapper study = new StudyWrapper(appService);
        study.setName(name);
        study.setSite(site);
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
            addStudy(site, name + i);
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

    public static int addSampleSourcesToStudy(StudyWrapper study, String name)
        throws Exception {
        int nber = r.nextInt(15) + 1;
        List<SampleSourceWrapper> sources = new ArrayList<SampleSourceWrapper>();
        for (int i = 0; i < nber; i++) {
            sources.add(SampleSourceHelper.addSampleSource(name + i));
        }
        study.setSampleSourceCollection(sources);
        study.persist();
        return nber;
    }
}
