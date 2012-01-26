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
