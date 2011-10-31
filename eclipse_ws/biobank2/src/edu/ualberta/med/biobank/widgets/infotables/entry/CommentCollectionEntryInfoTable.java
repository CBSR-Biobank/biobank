package edu.ualberta.med.biobank.widgets.infotables.entry;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Composite;

import edu.ualberta.med.biobank.SessionManager;
import edu.ualberta.med.biobank.common.wrappers.CommentWrapper;
import edu.ualberta.med.biobank.forms.CommentAdapter;
import edu.ualberta.med.biobank.forms.CommentEntryForm;
import edu.ualberta.med.biobank.forms.input.FormInput;
import edu.ualberta.med.biobank.treeview.AdapterBase;
import edu.ualberta.med.biobank.widgets.infotables.BiobankTableSorter;
import edu.ualberta.med.biobank.widgets.infotables.CommentCollectionInfoTable;

public class CommentCollectionEntryInfoTable extends CommentCollectionInfoTable {

    protected List<CommentWrapper> addedorModifiedComments = new ArrayList<CommentWrapper>();
    protected List<CommentWrapper> removedComments = new ArrayList<CommentWrapper>();

    protected List<CommentWrapper> currentComments;

    public CommentCollectionEntryInfoTable(Composite parent,
        List<CommentWrapper> comments) {
        super(parent, comments);
        currentComments = new ArrayList<CommentWrapper>();

        if (comments != null) {
            currentComments.addAll(comments);
        }
    }

    @Override
    protected boolean isEditMode() {
        return true;
    }

    @Override
    public CommentWrapper getSelection() {
        return ((TableRowData) super.getSelection()).comment;
    }

    @SuppressWarnings("serial")
    @Override
    protected BiobankTableSorter getComparator() {
        return new BiobankTableSorter() {
            @Override
            public int compare(Object e1, Object e2) {
                try {
                    TableRowData i1 = (TableRowData) getCollectionModelObject(e1);
                    TableRowData i2 = (TableRowData) getCollectionModelObject(e2);
                    return super.compare(i1.date, i2.date);
                } catch (Exception e) {
                    return 0;
                }
            }
        };
    }

    public void reload(List<CommentWrapper> comments) {
        currentComments = comments;
        if (currentComments == null) {
            currentComments = new ArrayList<CommentWrapper>();
        }
        reloadCollection(currentComments);
        addedorModifiedComments = new ArrayList<CommentWrapper>();
        removedComments = new ArrayList<CommentWrapper>();
    }

    public List<CommentWrapper> getAddedOrModifiedComments() {
        return addedorModifiedComments;
    }

    public List<CommentWrapper> getRemovedComments() {
        return removedComments;
    }

    public void addComment() {
        CommentWrapper wrapper = new CommentWrapper(
            SessionManager.getAppService());
        CommentAdapter adapter = new CommentAdapter(null, wrapper);
        AdapterBase.openForm(new FormInput(adapter), CommentEntryForm.ID);

    }
}