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
        }),
    @TypeDef(
        name = "rank",
        typeClass = GenericEnumUserType.class,
        defaultForType = Rank.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.Rank")
        })
})
package edu.ualberta.med.biobank.model;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import edu.ualberta.med.biobank.model.util.GenericEnumUserType;

