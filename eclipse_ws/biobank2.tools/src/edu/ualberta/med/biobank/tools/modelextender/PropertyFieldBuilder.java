package edu.ualberta.med.biobank.tools.modelextender;

import edu.ualberta.med.biobank.tools.utils.CamelCase;

public class PropertyFieldBuilder {
    private enum Token {
        PROPERTY_CLASS,
        MODEL_CLASS,
        VARIABLE_NAME,
        PROPERTY_NAME,
        ACCESSOR_SUFFIX;

        @Override
        public String toString() {
            return "{" + this.name() + "}";
        }
    }

    // @formatter:off    
    private static final String FIELD_TEMPLATE = 
        "\tpublic static final Property<" + Token.PROPERTY_CLASS + ", " + Token.MODEL_CLASS + "> " + Token.VARIABLE_NAME + " = Property.create("
        + "\n\t\t\"" + Token.PROPERTY_NAME + "\""
        + "\n\t\t, new TypeReference<" + Token.PROPERTY_CLASS + ">() {}"
        + "\n\t\t, new Property.Accessor<" + Token.PROPERTY_CLASS + ", " + Token.MODEL_CLASS + ">() { private static final long serialVersionUID = 1L;"
        + "\n\t\t\t@Override"
        + "\n\t\t\tpublic " + Token.PROPERTY_CLASS + " get(" + Token.MODEL_CLASS + " model) {"
        + "\n\t\t\t\treturn model.get" + Token.ACCESSOR_SUFFIX + "();"
        + "\n\t\t\t}"
        + "\n\t\t\t@Override"
        + "\n\t\t\tpublic void set(" + Token.MODEL_CLASS + " model, " + Token.PROPERTY_CLASS + " value) {"
        + "\n\t\t\t\tmodel.set" + Token.ACCESSOR_SUFFIX + "(value);"
        + "\n\t\t\t}"
        + "\n\t\t});";
    // @formatter:on

    public static String getField(String propertyClass, String modelClass,
        String propertyName) {

        String variableName = CamelCase.toTitleCase(propertyName);
        String accessorSuffix = CamelCase.toCamelCase(propertyName, true);

        // @formatter:off    
        String field = FIELD_TEMPLATE
            .replace(Token.PROPERTY_CLASS.toString(), propertyClass)
            .replace(Token.MODEL_CLASS.toString(), modelClass)
            .replace(Token.VARIABLE_NAME.toString(), variableName)
            .replace(Token.PROPERTY_NAME.toString(), propertyName)
            .replace(Token.ACCESSOR_SUFFIX.toString(), accessorSuffix);
        // @formatter:on

        return field;
    }
}
