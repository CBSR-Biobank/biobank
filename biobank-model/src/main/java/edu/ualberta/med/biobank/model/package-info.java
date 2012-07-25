@TypeDefs({
    @TypeDef(
        name = "activityStatus",
        typeClass = GenericEnumUserType.class,
        defaultForType = ActivityStatus.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.ActivityStatus")
        }),
    @TypeDef(
        name = "attributeValueType",
        typeClass = GenericEnumUserType.class,
        defaultForType = AttributeValueType.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.AttributeValueType")
        }),
    @TypeDef(
        name = "dispatchSpecimenState",
        typeClass = GenericEnumUserType.class,
        defaultForType = DispatchSpecimenState.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.type.DispatchSpecimenState")
        }),
    @TypeDef(
        name = "dispatchState",
        typeClass = GenericEnumUserType.class,
        defaultForType = DispatchState.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.type.DispatchState")
        }),
    @TypeDef(
        name = "permissionEnum",
        typeClass = GenericEnumUserType.class,
        defaultForType = PermissionEnum.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.PermissionEnum")
        }),
    @TypeDef(
        name = "requestSpecimenState",
        typeClass = GenericEnumUserType.class,
        defaultForType = RequestSpecimenState.class,
        parameters = {
            @Parameter(name = "enumClass", value = "edu.ualberta.med.biobank.model.type.RequestSpecimenState")
        })
})
package edu.ualberta.med.biobank.model;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import edu.ualberta.med.biobank.model.type.DispatchSpecimenState;
import edu.ualberta.med.biobank.model.type.DispatchState;
import edu.ualberta.med.biobank.model.type.RequestSpecimenState;
import edu.ualberta.med.biobank.model.util.GenericEnumUserType;

