package edu.ualberta.med.biobank.forms;


public class SiteEntryForm {
    // public class SiteEntryForm extends BiobankEntryForm implements
    // SiteEditPresenter.Display {
    //    public static final String ID = "edu.ualberta.med.biobank.forms.SiteEntryForm"; //$NON-NLS-1$
    //
    // private static final String MSG_NEW_SITE_OK =
    // Messages.SiteEntryForm_creation_msg;
    // private static final String MSG_SITE_OK =
    // Messages.SiteEntryForm_edition_msg;
    //
    // private ButtonItem save;
    // private ButtonItem reload;
    // private ButtonItem close;
    // private TextItem name;
    // private TextItem nameShort;
    // private TextItem comment;
    // private TextItem street1;
    // private TextItem street2;
    // private TextItem city;
    // private TextItem province;
    // private TextItem postalCode;
    // private TextItem phoneNumber;
    // private TextItem faxNumber;
    // private TextItem country;
    // private ComboItem<ActivityStatus> activityStatus;
    //
    // @Override
    // public void init() throws Exception {
    // Assert.isTrue((adapter instanceof SiteAdapter),
    //            "Invalid editor input: object of type " //$NON-NLS-1$
    // + adapter.getClass().getName());
    //
    // siteAdapter = (SiteAdapter) adapter;
    // site = (SiteWrapper) getModelObject();
    //
    // String tabName;
    // if (site.isNew()) {
    // tabName = Messages.SiteEntryForm_title_new;
    // site.setActivityStatus(ActivityStatusWrapper
    // .getActiveActivityStatus(appService));
    // } else {
    // tabName = NLS.bind(Messages.SiteEntryForm_title_edit,
    // site.getNameShort());
    // }
    // setPartName(tabName);
    // }
    //
    // @Override
    // protected void createFormContent() throws ApplicationException {
    // form.setText(Messages.SiteEntryForm_main_title);
    // page.setLayout(new GridLayout(1, false));
    // createSiteSection();
    // createAddressArea(site);
    // createStudySection();
    //
    // // When adding help uncomment line below
    // // PlatformUI.getWorkbench().getHelpSystem().setHelp(composite,
    // // IJavaHelpContextIds.XXXXX);
    // }
    //
    // private void createSiteSection() throws ApplicationException {
    // toolkit.createLabel(page, Messages.SiteEntryForm_main_description,
    // SWT.LEFT);
    //
    // Composite client = toolkit.createComposite(page);
    // GridLayout layout = new GridLayout(2, false);
    // layout.horizontalSpacing = 10;
    // client.setLayout(layout);
    // client.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
    // toolkit.paintBordersFor(client);
    //
    // setFirstControl(createBoundWidgetWithLabel(client, BgcBaseText.class,
    // SWT.NONE, Messages.label_name, null, site, SitePeer.NAME.getName(),
    // new NonEmptyStringValidator(
    // Messages.SiteEntryForm_field_name_validation_msg)));
    //
    // createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.NONE,
    // Messages.label_nameShort, null, site,
    // SitePeer.NAME_SHORT.getName(), new NonEmptyStringValidator(
    // Messages.SiteEntryForm_field_nameShort_validation_msg));
    //
    // activityStatusComboViewer = createComboViewer(client,
    // Messages.label_activity,
    // ActivityStatusWrapper.getAllActivityStatuses(appService),
    // site.getActivityStatus(),
    // Messages.SiteEntryForm_field_activity_validation_msg,
    // new ComboSelectionUpdate() {
    // @Override
    // public void doSelection(Object selectedObject) {
    // site.setActivityStatus((ActivityStatusWrapper) selectedObject);
    // }
    // });
    //
    // createBoundWidgetWithLabel(client, BgcBaseText.class, SWT.MULTI,
    // Messages.label_comments, null, site, SitePeer.COMMENT.getName(),
    // null);
    // }
    //
    // private void createStudySection() {
    // Section section = createSection(Messages.SiteEntryForm_studies_title);
    // // boolean superAdmin = SessionManager.getUser().isSuperAdmin();
    // // if (superAdmin) {
    // // addSectionToolbar(section, Messages.SiteEntryForm_studies_add,
    // // new SelectionAdapter() {
    // // @Override
    // // public void widgetSelected(SelectionEvent e) {
    // // studiesTable.createStudyDlg();
    // // }
    // // }, ContactWrapper.class);
    // // }
    // // studiesTable = new StudyAddInfoTable(section, site, superAdmin);
    // // studiesTable.adaptToToolkit(toolkit, true);
    // // studiesTable.addClickListener(collectionDoubleClickListener);
    // // // TODO: the new style info table needs to support editing of items
    // // // via the context menu
    // // // studiesTable.createDefaultEditItem();
    // // studiesTable.addSelectionChangedListener(listener);
    // // section.setClient(studiesTable);
    // }
    //
    // @Override
    // protected String getOkMessage() {
    // // if (site.getId() == null) {
    // // return MSG_NEW_SITE_OK;
    // // }
    // return MSG_SITE_OK;
    // }
    //
    // @Override
    // protected void saveForm() throws Exception {
    // // site.persist();
    // // siteAdapter.getParent().performExpand();
    // // SessionManager.getUser().updateCurrentCenter(site);
    // }
    //
    // @Override
    // public String getNextOpenedFormID() {
    // return SiteViewForm.ID;
    // }
    //
    // @Override
    // protected void onReset() throws Exception {
    // }
    //
    // @Override
    // public void close() {
    // }
    //
    // @Override
    // public HasClickHandlers getClose() {
    // return close;
    // }
    //
    // @Override
    // public HasClickHandlers getReload() {
    // return reload;
    // }
    //
    // @Override
    // public HasClickHandlers getSave() {
    // return save;
    // }
    //
    // @Override
    // public HasSelectedValue<ActivityStatus> getActivityStatus() {
    // return activityStatus;
    // }
    //
    // @Override
    // public HasValue<String> getStreet1() {
    // return street1;
    // }
    //
    // @Override
    // public HasValue<String> getStreet2() {
    // return street2;
    // }
    //
    // @Override
    // public HasValue<String> getCity() {
    // return city;
    // }
    //
    // @Override
    // public HasValue<String> getProvince() {
    // return province;
    // }
    //
    // @Override
    // public HasValue<String> getPostalCode() {
    // return postalCode;
    // }
    //
    // @Override
    // public HasValue<String> getPhoneNumber() {
    // return phoneNumber;
    // }
    //
    // @Override
    // public HasValue<String> getFaxNumber() {
    // return faxNumber;
    // }
    //
    // @Override
    // public HasValue<String> getCountry() {
    // return country;
    // }
    //
    // @Override
    // public void setGeneralErrors(Collection<Object> errors) {
    // // TODO Auto-generated method stub
    //
    // }
    //
    // @Override
    // public HasValue<String> getName() {
    // return name;
    // }
    //
    // @Override
    // public HasValue<String> getNameShort() {
    // return nameShort;
    // }
    //
    // @Override
    // public HasValue<String> getComment() {
    // return comment;
    // }
    //
    // @Override
    // public HasValue<Collection<StudyInfo>> getStudies() {
    // // TODO Auto-generated method stub
    // return null;
    // }
}
