package edu.ualberta.med.biobank.model.type;

@SuppressWarnings("nls")
public enum AnnotationValueType {
    STRING("ST"),
    NUMBER("NM"),
    DATE("DT"),
    SELECT("SL");

    private final String id;

    private AnnotationValueType(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }
}
