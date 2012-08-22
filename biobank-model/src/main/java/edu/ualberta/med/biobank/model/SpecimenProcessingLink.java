package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import edu.ualberta.med.biobank.model.type.Amount;

/**
 * <h1>REDO DOCs!</h1> This record could be interpreted as a parent-child
 * relationship between two {@link Specimen}s.
 * <p>
 * A {@link SpecimenProcessing} is a record of some process or procedure
 * (optionally represented by a {@link SpecimenProcessingType}) being done on a
 * specific input {@link Specimen} (i.e. {@link #getInput()}) with a resulting
 * output {@link Specimen} (i.e. {@link #getOutput()}). There are two cases:
 * <ol>
 * <li>{@link #getInput()} <em>equals</em> {@link #getOutput()} - when a process
 * modifies the {@link #getInput()} itself.</li>
 * <li>{@link #getInput()} <em>does not</em> equal {@link #getOutput()} - when a
 * process yields a new {@link Specimen}.</li>
 * </ol>
 * 
 * @author Jonathan Ferland
 */
@Audited
@Entity
@Table(name = "SPECIMEN_PROCESSING_LINK")
public class SpecimenProcessingLink
    extends AbstractVersionedModel {
    private static final long serialVersionUID = 1L;

    private Specimen input;
    private Specimen output;
    private SpecimenProcessingLinkType type;
    private Date timeDone;
    private Amount actualInputAmountChange;
    private Amount actualOutputAmountChange;
    private ProcessingEvent processingEvent;
}
