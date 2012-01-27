package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name = "PRINTED_SS_INV_ITEM")
public class PrintedSsInvItem extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private String txt;

    @Column(name = "TXT", length = 15)
    public String getTxt() {
        return this.txt;
    }

    public void setTxt(String txt) {
        this.txt = txt;
    }
}
