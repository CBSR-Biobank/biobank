package edu.ualberta.med.biobank.widgets.infotables;

import java.util.Collection;

import org.eclipse.swt.widgets.Composite;
import org.springframework.remoting.RemoteConnectFailureException;

import edu.ualberta.med.biobank.BioBankPlugin;
import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.model.Contact;
import edu.ualberta.med.biobank.model.Study;
import edu.ualberta.med.biobank.model.StudyContactInfo;

public class StudyContactEntryInfoTable extends InfoTableWidget<Contact> {

    private static final String[] headings = new String[] { "Clinic",
        "Contact Name", "Title", "Email", "Phone #", "Fax #" };

    private static final int[] bounds = new int[] { 150, 150, 100, 100, 100,
        100 };

    public StudyContactEntryInfoTable(Composite parent, Study study) {
        super(parent, null, headings, bounds);
        setCollection(study.getContactCollection());
    }

    @Override
    public void setCollection(final Collection<Contact> collection) {
        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    model.clear();
                    BiobankCollectionModel item;
                    for (Contact contact : collection) {
                        if (getTableViewer().getTable().isDisposed()) {
                            return;
                        }
                        item = new BiobankCollectionModel();
                        StudyContactInfo info = new StudyContactInfo();
                        item.o = info;
                        model.add(item);

                        info.contact = contact;
                        info.clinicName = contact.getClinic().getName();
                    }

                    getTableViewer().getTable().getDisplay().asyncExec(
                        new Runnable() {

                            public void run() {
                                getTableViewer().refresh();
                            }

                        });
                } catch (final RemoteConnectFailureException exp) {
                    BioBankPlugin.openRemoteConnectErrorMessage();
                } catch (Exception e) {
                    SessionManager.getLogger().error(
                        "Error while retrieving the clinic", e);
                }
            }
        };
        t.start();
    }
}
