package edu.ualberta.med.biobank.mvp.view;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;

public class SiteEntryView2 extends EditorPart implements
    SiteEntryPresenter.View {
    private final HasValue<String> name = new TextItem();

    @Override
    public void doSave(IProgressMonitor monitor) {
        // TODO Auto-generated method stub

    }

    @Override
    public void doSaveAs() {
        // TODO Auto-generated method stub

    }

    @Override
    public void init(IEditorSite site, IEditorInput input)
        throws PartInitException {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isDirty() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSaveAsAllowed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void createPartControl(Composite parent) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setFocus() {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public HasClickHandlers getClose() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void create(Composite parent) {
    }

    @Override
    public HasClickHandlers getReload() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasButton getSave() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setAddressEntryView(BaseView view) {
        // TODO Auto-generated method stub

    }

    @Override
    public void setActivityStatusComboView(BaseView view) {
        // TODO Auto-generated method stub

    }

    @Override
    public HasValue<String> getName() {
        return name;
    }

    @Override
    public HasValue<String> getNameShort() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<String> getComment() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public HasValue<List<StudyInfo>> getStudies() {
        // TODO Auto-generated method stub
        return null;
    }

}
