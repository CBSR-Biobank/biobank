package edu.ualberta.med.biobank.mvp.view;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;

import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import edu.ualberta.med.biobank.common.action.site.GetSiteStudyInfoAction.StudyInfo;
import edu.ualberta.med.biobank.mvp.presenter.impl.SiteEntryPresenter;
import edu.ualberta.med.biobank.mvp.user.ui.HasButton;
import edu.ualberta.med.biobank.mvp.view.item.ButtonItem;
import edu.ualberta.med.biobank.mvp.view.item.TableItem;
import edu.ualberta.med.biobank.mvp.view.item.TextItem;

public class SiteEntryView implements SiteEntryPresenter.View {
    private Widget widget;

    private final ButtonItem save = new ButtonItem();
    private final ButtonItem reload = new ButtonItem();
    private final ButtonItem close = new ButtonItem();
    private final TextItem name = new TextItem();
    private final TextItem nameShort = new TextItem();
    private final TextItem comment = new TextItem();
    private final TableItem<List<StudyInfo>> studies = new TableItem<List<StudyInfo>>();

    private BaseView addressEntryView;
    private BaseView activityStatusComboView;

    @Override
    public HasButton getSave() {
        return save;
    }

    @Override
    public HasValue<String> getName() {
        return name;
    }

    @Override
    public HasValue<String> getNameShort() {
        return nameShort;
    }

    @Override
    public HasValue<String> getComment() {
        return comment;
    }

    @Override
    public HasValue<List<StudyInfo>> getStudies() {
        return studies;
    }

    @Override
    public void close() {
        widget.getParent().dispose();
    }

    @Override
    public HasClickHandlers getClose() {
        return close;
    }

    @Override
    public HasClickHandlers getReload() {
        return reload;
    }

    @Override
    public void create(Composite parent) {
        widget = new Widget(parent, SWT.NONE);

        // create the inner widgets
        addressEntryView.create(widget);
        activityStatusComboView.create(widget);
    }

    @Override
    public void setAddressEntryView(BaseView view) {
        this.addressEntryView = view;
    }

    @Override
    public void setActivityStatusComboView(BaseView view) {
        this.activityStatusComboView = view;
    }

    public static class Widget extends Composite {
        public final Text name;

        public Widget(Composite parent, int style) {
            super(parent, style);

            name = new Text(this, SWT.NONE);
        }
    }
}
