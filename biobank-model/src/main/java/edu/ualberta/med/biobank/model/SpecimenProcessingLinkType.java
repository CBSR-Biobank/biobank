package edu.ualberta.med.biobank.model;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Amount;
import edu.ualberta.med.biobank.validator.constraint.NotUsed;
import edu.ualberta.med.biobank.validator.constraint.Unique;
import edu.ualberta.med.biobank.validator.group.PreDelete;
import edu.ualberta.med.biobank.validator.group.PrePersist;

/**
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_LINK_TYPE", uniqueConstraints = {
    @UniqueConstraint(columnNames = {
        "PROCESSING_TYPE_ID",
        "INPUT_SPECIMEN_GROUP_ID",
        "OUTPUT_SPECIMEN_GROUP_ID"
    })
})
@Unique(properties = { "processingType", "inputGroup", "outputGroup" }, groups = PrePersist.class)
@NotUsed(by = SpecimenProcessingLink.class, property = "specimenProcessingLinkType", groups = PreDelete.class)
public class SpecimenProcessingLinkType
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private ProcessingType type;
    private SpecimenGroup inputGroup;
    private SpecimenGroup outputGroup;
    private Amount expectedInputChange;
    private Amount expectedOutputChange;
    private Vessel outputVessel;
    private Integer outputCount;
}
