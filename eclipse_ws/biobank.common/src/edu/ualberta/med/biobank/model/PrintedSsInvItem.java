package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.validator.constraints.NotEmpty;

import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreInsert;
import edu.ualberta.med.biobank.validator.group.PreUpdate;

@Entity
@Table(name = "PRINTED_SS_INV_ITEM")
@Unique(properties = { "pnumber" },
    groups = { PreInsert.class, PreUpdate.class },
    message = "{edu.ualberta.med.biobank.model.PrintedSsInvItem.txt.Unique}")
public class PrintedSsInvItem extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String txt;

    @NotEmpty(message = "{edu.ualberta.med.biobank.model.PrintedSsInvItem.txt.NotEmpty}")
    @Column(name = "TXT", unique = true, length = 15)
    public String getTxt() {
        return this.txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
