//package edu.ualberta.med.biobank.mvp.presenter.impl;
//
//import java.util.Collection;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.Set;
//
//import com.google.gwt.user.client.ui.HasValue;
//import com.google.inject.Inject;
//import com.google.web.bindery.event.shared.EventBus;
//import com.pietschy.gwt.pectin.client.form.FieldModel;
//import com.pietschy.gwt.pectin.client.form.ListFieldModel;
//import com.pietschy.gwt.pectin.client.form.validation.ValidationPlugin;
//import com.pietschy.gwt.pectin.client.form.validation.component.ValidationDisplay;
//import com.pietschy.gwt.pectin.client.form.validation.validator.NotEmptyValidator;
//
//import edu.ualberta.med.biobank.common.action.ActionCallback;
//import edu.ualberta.med.biobank.common.action.Dispatcher;
//import edu.ualberta.med.biobank.common.action.info.OriginInfoSaveInfo;
//import edu.ualberta.med.biobank.common.action.info.ShipmentFormReadInfo;
//import edu.ualberta.med.biobank.common.action.info.ShipmentInfoSaveInfo;
//import edu.ualberta.med.biobank.common.action.info.ShippingMethodInfo;
//import edu.ualberta.med.biobank.common.action.info.SiteInfo;
//import edu.ualberta.med.biobank.common.action.info.StudyInfo;
//import edu.ualberta.med.biobank.common.action.shipment.ShipmentGetInfoAction;
//import edu.ualberta.med.biobank.common.action.site.SiteGetInfoAction;
//import edu.ualberta.med.biobank.common.action.site.SiteSaveAction;
//import edu.ualberta.med.biobank.model.ActivityStatus;
//import edu.ualberta.med.biobank.model.Address;
//import edu.ualberta.med.biobank.model.Center;
//import edu.ualberta.med.biobank.model.ShippingMethod;
//import edu.ualberta.med.biobank.model.Specimen;
//import edu.ualberta.med.biobank.mvp.event.ExceptionEvent;
//import edu.ualberta.med.biobank.mvp.event.model.site.SiteChangedEvent;
//import edu.ualberta.med.biobank.mvp.event.presenter.site.SiteViewPresenterShowEvent;
//import edu.ualberta.med.biobank.mvp.model.AbstractModel;
//import edu.ualberta.med.biobank.mvp.presenter.impl.ShipmentEntryPresenter.View;
//import edu.ualberta.med.biobank.mvp.user.ui.HasSelectedValue;
//import edu.ualberta.med.biobank.mvp.view.IEntryFormView;
//import edu.ualberta.med.biobank.mvp.view.IView;
//
///**
// * 
// * @author jferland
// * 
// */
//public class ShipmentEntryPresenter extends AbstractEntryFormPresenter<View> {
//    private final Dispatcher dispatcher;
//    private final Model model;
//    private Integer shipId;
//
//    public interface View extends IEntryFormView, ValidationDisplay {
//        void setActivityStatusComboView(IView view);
//
//        HasSelectedValue<Center> getSenderCenter();
//        HasSelectedValue<Center> getReceiverCenter();
//        HasValue<String> getComment();
//        HasValue<Collection<Specimen>> getSpecimens();
//        HasSelectedValue<ShippingMethod> getShippingMethod();
//        HasValue<String> getWaybill();
//        HasValue<String> getBoxNumber();
//        HasValue<Date> getPackedAt();
//        HasValue<Date>  getReceivedAt();
//    }
//
//    @Inject
//    public ShipmentEntryPresenter(View view, EventBus eventBus,
//        Dispatcher dispatcher) {
//        super(view, eventBus);
//        this.dispatcher = dispatcher;
//
//        this.model = new Model();
//
//    }
//
//    @Override
//    public void onBind() {
//        super.onBind();
//
//        binder.bind(model.siteId).to(view.getIdentifier());
//        binder.bind(model.name).to(view.getName());
//        binder.bind(model.nameShort).to(view.getNameShort());
//        binder.bind(model.studies).to(view.getStudies());
//        binder.bind(model.activityStatus).to(
//            activityStatusComboPresenter.getActivityStatus());
//
//        binder.bind(model.dirty()).to(view.getDirty());
//
//        model.bind();
//
//        model.bindValidationTo(view);
//
//        binder.enable(view.getSave()).when(model.validAndDirty());
//    }
//
//    @Override
//    protected void onUnbind() {
//        model.unbind();
//
//    }
//
//    @Override
//    public void doReload() {
//        if (siteId != null) {
//            editSite(siteId);
//        } else {
//            createSite();
//        }
//    }
//
//    @Override
//    public void doSave() {
//        if (!model.validAndDirty().getValue()) return;
//
//        SiteSaveAction saveSite = new SiteSaveAction();
//        saveSite.setId(model.siteId.getValue());
//        saveSite.setName(model.name.getValue());
//        saveSite.setNameShort(model.nameShort.getValue());
//        // saveSite.setComment(model.comment.getValue());
//        saveSite.setAddress(model.address.getValue());
//        saveSite.setActivityStatusId(model.getActivityStatusId());
//        saveSite.setStudyIds(model.getStudyIds());
//
//        dispatcher.exec(saveSite, new ActionCallback<Integer>() {
//            @Override
//            public void onFailure(Throwable caught) {
//                eventBus.fireEvent(new ExceptionEvent(caught));
//            }
//
//            @Override
//            public void onSuccess(Integer siteId) {
//                // clear dirty state (so form can close without prompt to save)
//                model.checkpoint();
//
//                eventBus.fireEvent(new SiteChangedEvent(siteId));
//                eventBus.fireEvent(new SiteViewPresenterShowEvent(siteId));
//                close();
//            }
//        });
//    }
//
//    public void createShipment() {
//        SiteInfo siteInfo = new SiteInfo.Builder().build();
//        editShipment(siteInfo);
//    }
//
//    public boolean editShipment(Integer shipId) {
//        this.shipId = shipId;
//
//        ShipmentGetInfoAction shipGetInfoAction = new ShipmentGetInfoAction(shipId);
//
//        boolean success = dispatcher.exec(shipGetInfoAction,
//            new ActionCallback<ShipmentFormReadInfo>() {
//                @Override
//                public void onFailure(Throwable caught) {
//                    eventBus.fireEvent(new ExceptionEvent(caught));
//                    close();
//                }
//
//                @Override
//                public void onSuccess(ShipmentFormReadInfo shipInfo) {
//                    editShipment(shipInfo);
//                }
//            });
//
//        return success;
//    }
//
//    private void editShipment(ShipmentFormReadInfo shipInfo) {
//        InnerModel innerModel = new InnerModel();
//        innerModel.smInfo=new ShippingMethodInfo(shipInfo.oi.getShipmentInfo().getShippingMethod().getId());
//        innerModel.oisInfo=new OriginInfoSaveInfo(shipInfo.oi.id, shipInfo.oi.getCenter().getId(), shipInfo.oi.getReceiverSite().getId(), null, null, null);
//        innerModel.sisInfo=new ShipmentInfoSaveInfo(shipInfo.oi.getShipmentInfo().id, shipInfo.oi.getShipmentInfo().boxNumber, shipInfo.oi.getShipmentInfo().packedAt,shipInfo.oi.getShipmentInfo().receivedAt, shipInfo.oi.getShipmentInfo().waybill, innerModel.smInfo); 
//        
//        model.setValue(innerModel);
//    }
//
//    /**
//     * The {@link Model} holds the data that the {@link View} needs and supplies
//     * validation.
//     * 
//     * @author jferland
//     * 
//     */
//    
//    
//    
//    
//    public static class Model extends AbstractModel<ShipmentFormReadInfo> {
//
//        
//        final FieldModel<Integer> shipId;
//        final FieldModel<Center> sendingCenter;
//        final FieldModel<Center> receivingCenter;
//        final ListFieldModel<Specimen> specimens;
//        final FieldModel<String> comment;
//
//        final FieldModel<String> waybill;
//        final FieldModel<String> boxNumber;
//        final FieldModel<Date> packedAt;
//        final FieldModel<Date> receivedAt;
//        
//        final FieldModel<ShippingMethod> shippingMethod;
//        
//        @SuppressWarnings("unchecked")
//        private Model() {
//            super(ShipmentFormReadInfo.class);
//
//            shipId = fieldOfType(Integer.class)
//                .boundTo(provider, "oi.id");
//            sendingCenter = fieldOfType(Center.class)
//                .boundTo(provider, "oi.sendingCenter");
//            receivingCenter = fieldOfType(Center.class)
//                .boundTo(provider, "oi.sendingCenter");
//            specimens = listOfType(Specimen.class)
//                .boundTo(provider, "specimens");
//            comment = fieldOfType(String.class)
//                .boundTo(provider, "oi.comment");
//            
//            
//            waybill = fieldOfType(String.class)
//                .boundTo(provider, "oi.shipmentInfo.waybill");
//            boxNumber = fieldOfType(String.class)
//                .boundTo(provider, "oi.shipmentInfo.boxNumber");
//            packedAt = fieldOfType(Date.class)
//                .boundTo(provider, "oi.shipmentInfo.packedAt");
//            receivedAt = fieldOfType(Date.class)
//                .boundTo(provider, "oi.shipmentInfo.receivedAt");
//
//            shippingMethod = fieldOfType(ShippingMethod.class)
//                .boundTo(provider, "oi.shipmentInfo.shippingMethod");
//            
//            ValidationPlugin.validateField(name)
//                .using(new NotEmptyValidator("Name is required"));
//            ValidationPlugin.validateField(nameShort)
//                .using(new NotEmptyValidator("Name Short is required"));
//        }
//
//
//        @Override
//        public void onBind() {
//            bind(address, addressModel);
//        }
//
//        @Override
//        public void onUnbind() {
//        }
//    }
//}
