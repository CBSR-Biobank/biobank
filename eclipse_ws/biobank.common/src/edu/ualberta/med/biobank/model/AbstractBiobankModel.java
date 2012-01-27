package edu.ualberta.med.biobank.model;

public abstract class AbstractBiobankModel implements IBiobankModel {
    private static final long serialVersionUID = 1L;

    private Integer version;
    private Integer id;

    @Override
    public final Integer getId() {
        return id;
    }

    @Override
    public final void setId(Integer id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
    
    /**
     * Set by Hibernate via field access.
     * 
     * @return version
     */
    public final Integer getVersion() {
        return version;
    }

    // TODO: does this actually work? Test (especially with hibernate proxies)
    @Override
    public boolean equals(Object obj) {
        return equals(obj, getClass());
    }

    protected <T extends IBiobankModel> boolean equals(Object o, Class<T> klazz) {
        if (o == this) return true;
        if (o == null) return false;
        if (klazz.isAssignableFrom(getClass())) {
            T t = klazz.cast(o);
            if (getId() != null && getId().equals(t.getId())) {
                return true;
            }
        }
        return false;
    }
}
