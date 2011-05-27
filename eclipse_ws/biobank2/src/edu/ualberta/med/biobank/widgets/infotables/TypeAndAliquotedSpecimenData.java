package edu.ualberta.med.biobank.widgets.infotables;

import edu.ualberta.med.biobank.common.wrappers.ActivityStatusWrapper;
import edu.ualberta.med.biobank.common.wrappers.AliquotedSpecimenWrapper;
import edu.ualberta.med.biobank.common.wrappers.SpecimenTypeWrapper;

public class TypeAndAliquotedSpecimenData {
    private boolean used = false;
    private SpecimenTypeWrapper type;
    private Double volume;
    private Integer quantity;
    private ActivityStatusWrapper status;

    public TypeAndAliquotedSpecimenData(SpecimenTypeWrapper type) {
        this.type = type;
    }

    public TypeAndAliquotedSpecimenData(AliquotedSpecimenWrapper asw) {
        used = true;
        type = asw.getSpecimenType();
        volume = asw.getVolume();
        quantity = asw.getQuantity();
        status = asw.getActivityStatus();
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public SpecimenTypeWrapper getType() {
        return type;
    }

    public void setType(SpecimenTypeWrapper type) {
        this.type = type;
    }

    public Double getVolume() {
        return volume;
    }

    public void setVolume(Double volume) {
        this.volume = volume;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public ActivityStatusWrapper getStatus() {
        return status;
    }

    public void setStatus(ActivityStatusWrapper status) {
        this.status = status;
    }
}
