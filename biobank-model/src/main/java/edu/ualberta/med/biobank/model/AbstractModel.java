package edu.ualberta.med.biobank.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;

import edu.ualberta.med.biobank.model.util.ProxyUtil;

@MappedSuperclass
public abstract class AbstractModel
    implements IBiobankModel, HasTimeInserted, HasInsertedBy {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private Date timeInserted;
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

    @Override
    public boolean equals(Object that) {
        if (that == this) return true;
        if (ProxyUtil.sameClass(this, that)) return false;

        if (that instanceof IBiobankModel) {
            Integer thatId = ((IBiobankModel) that).getId();
            if (getId() != null && getId().equals(thatId)) return true;
        }
        return false;
    }

    @Override
    public int hashCode() {
        if (getId() == null) return 0;
        return getId().hashCode();
    }

    @Transient
    public boolean isNew() {
        return getId() == null;
    }

    @Override
    @Column(name = "TIME_INSERTED", updatable = false, nullable = false)
    public Date getTimeInserted() {
        return timeInserted;
    }

    @Override
    public void setTimeInserted(Date timeInserted) {
        this.timeInserted = timeInserted;
    }

    @Override
    @ManyToOne(fetch = FetchType.LAZY)
    @Column(name = "INSERTED_BY_USER_ID", updatable = false, nullable = false)
    public User getInsertedBy() {
        return insertedBy;
    }

    @Override
    public void setInsertedBy(User insertedBy) {
        this.insertedBy = insertedBy;
    }

}
