package edu.ualberta.med.biobank.model;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.hibernate.annotations.GenericGenerator;

@MappedSuperclass
public abstract class AbstractBiobankModel implements IBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer version;
    private Integer id;

    @Override
    @GenericGenerator(name = "generator", strategy = "increment")
    @Id
    @GeneratedValue(generator = "generator")
    @Column(name = "ID", nullable = false)
    public Integer getId() {
        return this.id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Version
    @Column(name = "VERSION", nullable = false)
    public Integer getVersion() {
        return this.version;
    }

    /**
     * DO NOT CALL this method unless, maybe, for tests. Hibernate manages
     * setting this value.
     * 
     * @param version
     */
    @Deprecated
    public void setVersion(Integer version) {
        this.version = version;
    }

    // TODO: does this actually work? Test (especially with hibernate proxies)
    @Override
    public boolean equals(Object obj) {
        return equals(obj, getClass());
    }

    protected <T extends IBiobankModel> boolean equals(Object o, Class<T> klazz) {
        if (o == this) return true;
        if (o == null) return false;
        if (klazz.isAssignableFrom(o.getClass())) {
            T t = klazz.cast(o);
            if (getId() != null && getId().equals(t.getId())) {
                return true;
            }
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
}
