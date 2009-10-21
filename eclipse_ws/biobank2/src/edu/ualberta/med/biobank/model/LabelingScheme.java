package edu.ualberta.med.biobank.model;

public class LabelingScheme {
    public Integer id;
    public String name;

    public LabelingScheme(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (o != null && o instanceof LabelingScheme) {
            LabelingScheme ls = (LabelingScheme) o;
            return (id == null && ls.id == null) || id.equals(ls.id);
        }
        return false;
    }
}
