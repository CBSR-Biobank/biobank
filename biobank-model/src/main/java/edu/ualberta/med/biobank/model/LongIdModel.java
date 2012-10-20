package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;

import edu.ualberta.med.biobank.model.security.User;

/**
 * A superlcass that entities can extend if they want an automatically generated
 * id, determined by a table generation strategy (nearly equivalent to
 * sequences, but table based).
 * 
 * @author Jonathan Ferland
 */
@MappedSuperclass
public abstract class LongIdModel
    implements IBiobankModel, HasTimeInserted, HasInsertedBy {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Long timeInserted;
    private User insertedBy;

    // TODO: up the increment param to ~1000, because why not? Should also
    // change to Long.
    @Override
    @Id
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator",
        strategy = "edu.ualberta.med.biobank.model.id.CustomTableGenerator",
        parameters = @Parameter(name = TableGenerator.INCREMENT_PARAM, value = "50"))
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * Overridden only for documentation. Note that Hibernate guarantees that,
     * within the same session, referential equality exists between entities.
     * Therefore, it is easiest to use the default implementations of
     * {@link #equals(Object)} and {@link #hashCode()}.
     * 
     * {@inheritDoc}
     * 
     * @see https://community.jboss.org/wiki/EqualsAndHashCode
     */
    @Override
    public boolean equals(Object that) {
        return super.equals(that);
    }

    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    @Column(name = "TIME_INSERTED", updatable = false, nullable = false)
    public Long getTimeInserted() {
        return timeInserted;
    }

    @Override
    public void setTimeInserted(Long timeInserted) {
        this.timeInserted = timeInserted;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSERTED_BY_USER_ID", updatable = false, nullable = false)
    public User getInsertedBy() {
        return insertedBy;
    }

    @Override
    public void setInsertedBy(User insertedBy) {
        this.insertedBy = insertedBy;
    }

}
