package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.id.enhanced.TableGenerator;
import org.hibernate.proxy.HibernateProxyHelper;

@MappedSuperclass
public class AbstractModel implements IBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer id;

    @Override
    @Id
    @GeneratedValue(generator = "id-generator")
    @GenericGenerator(name = "id-generator",
        strategy = "edu.ualberta.med.biobank.model.id.SmartTableGenerator",
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
        if (that == null) return false;

        // note that HibernateProxyHelper.getClassWithoutInitializingProxy(o)
        // does not seem to work properly in terms of returning the actual
        // class, it may return a superclass, such as, Center. However,
        // Hibernate.getClass() seems to always return the correct instance.
        Class<?> thisClass = Hibernate.getClass(this);
        Class<?> thatClass = Hibernate.getClass(that);
        if (thisClass != thatClass) return false;

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

    @SuppressWarnings("unused")
    private static Class<?> proxiedClass(Object o) {
        return HibernateProxyHelper.getClassWithoutInitializingProxy(o);
    }
}
