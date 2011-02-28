package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.StudyWrapper;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.SampleStorageInfoTable;

/**
 * Displays the current sample storage collection and allows the user to add
 * additional sample storage to the collection.
 */
@SuppressWarnings("unused")
@Deprecated
public class SampleStorageEntryInfoTable extends SampleStorageInfoTable {

    public SampleStorageEntryInfoTable(Composite parent, StudyWrapper study) {
        super(parent, null);
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    public void addSampleStorage() {
    }

    public List<AliquotedSpecimenWrapper> getAddedOrModifiedSampleStorages() {
        return null;
    }

    public List<AliquotedSpecimenWrapper> getDeletedSampleStorages() {
        return null;
    }

    public void reload() {
    }

    @Override
    public BiobankTableSorter getComparator() {
        return null;
    }

}
