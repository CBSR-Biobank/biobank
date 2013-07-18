package edu.ualberta.med.biobank.model;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.ForeignKey;

@Entity
@Table(name = "DNA")
public class Dna extends AbstractBiobankModel {
    private static final long serialVersionUID = 1L;

    private Specimen specimen;
    private BigDecimal concentrationAbs;
    private BigDecimal concentrationFluor;
    private BigDecimal od260Over280;
    private BigDecimal od260Over230;
    private BigDecimal aliquotYield;

    @NotNull(message = "{edu.ualberta.med.biobank.model.Dna.specimen.NotNull}")
    @OneToOne(fetch = FetchType.LAZY)
    @ForeignKey(name = "FK_Dna_specimen")
    @JoinColumn(name = "SPECIMEN_ID", nullable = false, unique = true)
    public Specimen getSpecimen() {
        return this.specimen;
    }

    public void setSpecimen(Specimen specimen) {
        this.specimen = specimen;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.Specimen.concentrationAbs.Digits}")
    @Column(name = "CONCENTRATION_ABS", precision = 10, scale = 10)
    public BigDecimal getConcentrationAbs() {
        return this.concentrationAbs;
    }

    public void setConcentrationAbs(BigDecimal concentrationAbs) {
        this.concentrationAbs = concentrationAbs;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.Specimen.concentrationFluor.Digits}")
    @Column(name = "CONCENTRATION_FLUOR", precision = 10, scale = 10)
    public BigDecimal getConcentrationFluor() {
        return this.concentrationFluor;
    }

    public void setConcentrationFluor(BigDecimal concentrationFluor) {
        this.concentrationFluor = concentrationFluor;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.Specimen.od260Over280.Digits}")
    @Column(name = "OD_260_OVER_280", precision = 10, scale = 10)
    public BigDecimal getOd260Over280() {
        return this.od260Over280;
    }

    public void setOd260Over280(BigDecimal od260Over280) {
        this.od260Over280 = od260Over280;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.Specimen.od260Over230.Digits}")
    @Column(name = "OD_260_OVER_230", precision = 10, scale = 10)
    public BigDecimal getOd260Over230() {
        return this.od260Over230;
    }

    public void setOd260Over230(BigDecimal od260Over230) {
        this.od260Over230 = od260Over230;
    }

    @Digits(integer = 10, fraction = 10, message = "{edu.ualberta.med.biobank.model.Specimen.aliquotYield.Digits}")
    @Column(name = "ALIQUOT_YIELD", precision = 10, scale = 10)
    public BigDecimal getAliquotYield() {
        return this.aliquotYield;
    }

    public void setAliquotYield(BigDecimal aliquotYield) {
        this.aliquotYield = aliquotYield;
    }
}
