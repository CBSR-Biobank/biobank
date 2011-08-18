package edu.ualberta.med.biobank.gui.common.forms;

import org.eclipse.ui.forms.widgets.ScrolledForm;

public interface IBgcEntryForm {

    public ScrolledForm getScrolledForm();

    public void confirm();

    public void cancel();

    public void reset();

    public boolean print();

}
