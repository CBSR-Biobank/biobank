@TypeDefs({
    @TypeDef(
        typeClass = GenericEnumUserType.class,
        defaultForType = ActivityStatus.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.ActivityStatus"),
            @Parameter(name = "identifierMethod", value = "getId"),
            @Parameter(name = "valueOfMethod", value = "fromId")
        })
})
package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.model.util.GenericEnumUserType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

