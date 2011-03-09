package edu.ualberta.med.biobank.treeview.patient;

import java.util.Collection;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;

import edu.ualberta.med.biobank.common.wrappers.CollectionEventWrapper;
import edu.ualberta.med.biobank.common.wrappers.ModelWrapper;
import edu.ualberta.med.biobank.common.wrappers.PatientWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.forms.CollectionEventEntryForm;
import edu.ualberta.med.biobank.forms.CollectionEventViewForm;
import edu.ualberta.med.biobank.logs.BiobankLogger;
import edu.ualberta.med.biobank.treeview.AdapterBase;

public class CollectionEventAdapter extends AdapterBase {

    private static BiobankLogger logger = BiobankLogger
        .getLogger(CollectionEventAdapter.class.getName());

    public CollectionEventAdapter(AdapterBase parent,
        CollectionEventWrapper collectionEventWrapper) {
        super(parent, collectionEventWrapper);
        setEditable(parent instanceof PatientAdapter || parent == null);
    }

    public CollectionEventWrapper getWrapper() {
        return (CollectionEventWrapper) modelObject;
    }

    @Override
    protected String getLabelInternal() {
        CollectionEventWrapper cevent = getWrapper();
        Assert.isNotNull(cevent, "collection event is null");
        String name = cevent.getPatient().getPnumber() + " - #"
            + cevent.getVisitNumber();

        long count = -1;
        try {
            count = cevent.getSourceSpecimensCount(true);
        } catch (Exception e) {
            logger.error("Problem counting specimens");
        }
        return name + " [" + count + "]";
    }

    @Override
    public String getTooltipText() {
        CollectionEventWrapper visit = getWrapper();
        if (visit != null) {
            PatientWrapper patient = visit.getPatient();
            if (patient != null) {
                StudyWrapper study = patient.getStudy();
                Assert.isNotNull(study, "study is null");
                return study.getNameShort() + " - " + patient.getPnumber()
                    + " - " + getTooltipText("Collection Event");

            }
        }
        return getTooltipText("Patient Visit");
    }

    @Override
    public void popupMenu(TreeViewer tv, Tree tree, Menu menu) {
        addEditMenu(menu, "Collection Event");
        addViewMenu(menu, "Collection Event");
        addDeleteMenu(menu, "Collection Event");
    }

    @Override
    protected String getConfirmDeleteMessage() {
        return "Are you sure you want to delete this collection event?";
    }

    @Override
    protected AdapterBase createChildNode() {
        return null;
    }

    @Override
    protected AdapterBase createChildNode(ModelWrapper<?> child) {
        return null;
    }

    @Override
    protected Collection<? extends ModelWrapper<?>> getWrapperChildren()
        throws Exception {
        return null;
    }

    @Override
    protected int getWrapperChildCount() throws Exception {
        return 0;
    }

    @Override
    public String getEntryFormId() {
        return CollectionEventEntryForm.ID;
    }

    @Override
    public String getViewFormId() {
        return CollectionEventViewForm.ID;
    }

    @Override
    public boolean isDeletable() {
        return internalIsDeletable();
    }

}
