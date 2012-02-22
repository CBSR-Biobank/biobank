@TypeDefs({
    @TypeDef(
        name = "activityStatus", 
        typeClass = GenericEnumUserType.class,
        defaultForType = ActivityStatus.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.ActivityStatus")
        }),
    @TypeDef(
        name = "permissionEnum",
        typeClass = GenericEnumUserType.class,
        defaultForType = PermissionEnum.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.PermissionEnum")
        })
})
package edu.ualberta.med.biobank.model;

import edu.ualberta.med.biobank.model.util.GenericEnumUserType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

