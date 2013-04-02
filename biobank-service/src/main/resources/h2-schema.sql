
    create table ANATOMICAL_SOURCE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION TEXT,
        NAME varchar(100) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table ANATOMICAL_SOURCE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION TEXT,
        NAME varchar(100),
        primary key (ID, REV)
    );

    create table ANNOTATION_OPTION (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        VALUE varchar(50) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        ANNOTATION_TYPE_ID integer not null,
        primary key (ID)
    );

    create table ANNOTATION_OPTION_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        VALUE varchar(50),
        ANNOTATION_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table ANNOTATION_TYPE (
        DISCRIMINATOR CHAR(4) not null,
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(5000),
        MAX_VALUE_COUNT integer not null,
        NAME varchar(50) not null,
        VALUE_TYPE varchar(255) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        STUDY_ID integer not null,
        primary key (ID)
    );

    create table ANNOTATION_TYPE_AUD (
        DISCRIMINATOR CHAR(4) not null,
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(5000),
        MAX_VALUE_COUNT integer,
        NAME varchar(50),
        VALUE_TYPE varchar(255),
        STUDY_ID integer,
        primary key (ID, REV)
    );

    create table CENTER (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(50) not null,
        IS_ENABLED boolean,
        NAME varchar(50) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table CENTER_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(50),
        IS_ENABLED boolean,
        NAME varchar(50),
        primary key (ID, REV)
    );

    create table CENTER_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        CENTER_ID integer not null,
        primary key (ID)
    );

    create table CENTER_LOCATION (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        PO_BOX_NUMBER varchar(50),
        CITY varchar(150),
        COUNTRY_ISO_CODE varchar(3),
        NAME varchar(100) not null,
        POSTAL_CODE varchar(20),
        PROVINCE varchar(50),
        STREET varchar(255),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CENTER_ID integer not null,
        primary key (ID)
    );

    create table CENTER_LOCATION_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        PO_BOX_NUMBER varchar(50),
        CITY varchar(150),
        COUNTRY_ISO_CODE varchar(3),
        NAME varchar(100),
        POSTAL_CODE varchar(20),
        PROVINCE varchar(50),
        STREET varchar(255),
        CENTER_ID integer,
        primary key (ID, REV)
    );

    create table CENTER_MEMBERSHIP (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        IS_MANAGER boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        PRINCIPAL_ID integer not null,
        CENTER_ID integer not null,
        primary key (ID)
    );

    create table CENTER_MEMBERSHIP_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        IS_MANAGER boolean,
        PRINCIPAL_ID integer,
        CENTER_ID integer,
        primary key (ID, REV)
    );

    create table CENTER_MEMBERSHIP_ROLE (
        CENTER_MEMBERSHIP_ID integer not null,
        CENTER_ROLE_ID integer not null,
        primary key (CENTER_MEMBERSHIP_ID, CENTER_ROLE_ID)
    );

    create table CENTER_MEMBERSHIP_ROLE_AUD (
        REV bigint not null,
        CENTER_MEMBERSHIP_ID integer not null,
        CENTER_ROLE_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, CENTER_MEMBERSHIP_ID, CENTER_ROLE_ID)
    );

    create table CENTER_MEMBERSHIP_STUDY (
        CENTER_MEMBERSHIP_ID integer not null,
        STUDY_ID integer not null,
        primary key (CENTER_MEMBERSHIP_ID, STUDY_ID)
    );

    create table CENTER_MEMBERSHIP_STUDY_AUD (
        REV bigint not null,
        CENTER_MEMBERSHIP_ID integer not null,
        STUDY_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, CENTER_MEMBERSHIP_ID, STUDY_ID)
    );

    create table CENTER_ROLE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        NAME varchar(255) not null,
        INSERTED_BY_USER_ID integer,
        primary key (ID)
    );

    create table CENTER_ROLE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        NAME varchar(255),
        primary key (ID, REV)
    );

    create table CENTER_ROLE_PERMISSION (
        CENTER_ROLE_ID integer not null,
        CENTER_PERMISSION_ID integer not null,
        primary key (CENTER_ROLE_ID, CENTER_PERMISSION_ID)
    );

    create table CENTER_ROLE_PERMISSION_AUD (
        REV bigint not null,
        CENTER_ROLE_ID integer not null,
        CENTER_PERMISSION_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, CENTER_ROLE_ID, CENTER_PERMISSION_ID)
    );

    create table COLLECTION_EVENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        TIME_DONE timestamp not null,
        VISIT_NUMBER integer not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        PATIENT_ID integer not null,
        COLLECTION_EVENT_TYPE_ID integer not null,
        primary key (ID)
    );

    create table COLLECTION_EVENT_ANNOTATION (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DECIMAL_SCALE integer not null,
        DECIMAL_VALUE decimal(27,9) not null,
        STRING_VALUE varchar(100),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        SELECTED_VALUE integer,
        ANNOTATION_TYPE_ID integer not null,
        COLLECTION_EVENT_ID integer,
        primary key (ID)
    );

    create table COLLECTION_EVENT_ANNOTATION_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DECIMAL_SCALE integer,
        DECIMAL_VALUE decimal(27,9),
        STRING_VALUE varchar(100),
        SELECTED_VALUE integer,
        ANNOTATION_TYPE_ID integer,
        COLLECTION_EVENT_ID integer,
        primary key (ID, REV)
    );

    create table COLLECTION_EVENT_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        TIME_DONE timestamp,
        VISIT_NUMBER integer,
        PATIENT_ID integer,
        COLLECTION_EVENT_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table COLLECTION_EVENT_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        COLLECTION_EVENT_ID integer not null,
        primary key (ID)
    );

    create table COLLECTION_EVENT_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(10000),
        NAME varchar(50),
        IS_RECURRING boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        STUDY_ID integer,
        primary key (ID)
    );

    create table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        IS_REQUIRED boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        ANNOTATION_TYPE_ID integer not null,
        COLLECTION_EVENT_TYPE_ID integer not null,
        primary key (ID)
    );

    create table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        IS_REQUIRED boolean,
        ANNOTATION_TYPE_ID integer,
        COLLECTION_EVENT_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table COLLECTION_EVENT_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(10000),
        NAME varchar(50),
        IS_RECURRING boolean,
        STUDY_ID integer,
        primary key (ID, REV)
    );

    create table CONTAINER (
        DISCRIMINATOR varchar(31) not null,
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        "DEPTH" integer not null,
        INVENTORY_ID varchar(255) not null,
        LABEL varchar(255) not null,
        "LEFT" integer not null,
        "RIGHT" integer not null,
        IS_ENABLED boolean,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CONTAINER_TYPE_ID integer,
        PARENT_CONTAINER_ID integer,
        CONTAINER_SCHEMA_POSITION_ID integer,
        CONTAINER_TREE_ID integer not null,
        CONTAINER_CONSTRAINTS_ID integer,
        primary key (ID)
    );

    create table CONTAINER_AUD (
        DISCRIMINATOR varchar(31) not null,
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        "DEPTH" integer,
        INVENTORY_ID varchar(255),
        LABEL varchar(255),
        "LEFT" integer,
        "RIGHT" integer,
        CONTAINER_TYPE_ID integer,
        PARENT_CONTAINER_ID integer,
        CONTAINER_SCHEMA_POSITION_ID integer,
        CONTAINER_TREE_ID integer,
        IS_ENABLED boolean,
        CONTAINER_CONSTRAINTS_ID integer,
        primary key (ID, REV)
    );

    create table CONTAINER_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        CONTAINER_ID integer not null,
        primary key (ID)
    );

    create table CONTAINER_CONSTRAINTS (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(5000),
        NAME varchar(50) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CENTER_ID integer not null,
        primary key (ID)
    );

    create table CONTAINER_CONSTRAINTS_ANATOMICAL_SOURCE (
        CONTAINER_CONSTRAINTS_ID integer not null,
        ANATOMICAL_SOURCE_ID integer not null,
        primary key (CONTAINER_CONSTRAINTS_ID, ANATOMICAL_SOURCE_ID)
    );

    create table CONTAINER_CONSTRAINTS_ANATOMICAL_SOURCE_AUD (
        REV bigint not null,
        CONTAINER_CONSTRAINTS_ID integer not null,
        ANATOMICAL_SOURCE_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, CONTAINER_CONSTRAINTS_ID, ANATOMICAL_SOURCE_ID)
    );

    create table CONTAINER_CONSTRAINTS_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(5000),
        NAME varchar(50),
        CENTER_ID integer,
        primary key (ID, REV)
    );

    create table CONTAINER_CONSTRAINTS_PRESERVATION_TYPE (
        CONTAINER_CONSTRAINTS_ID integer not null,
        PRESERVATION_TYPE_ID integer not null,
        primary key (CONTAINER_CONSTRAINTS_ID, PRESERVATION_TYPE_ID)
    );

    create table CONTAINER_CONSTRAINTS_PRESERVATION_TYPE_AUD (
        REV bigint not null,
        CONTAINER_CONSTRAINTS_ID integer not null,
        PRESERVATION_TYPE_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, CONTAINER_CONSTRAINTS_ID, PRESERVATION_TYPE_ID)
    );

    create table CONTAINER_CONSTRAINTS_SPECIMEN_TYPE (
        CONTAINER_CONSTRAINTS_ID integer not null,
        SPECIMEN_TYPE_ID integer not null,
        primary key (CONTAINER_CONSTRAINTS_ID, SPECIMEN_TYPE_ID)
    );

    create table CONTAINER_CONSTRAINTS_SPECIMEN_TYPE_AUD (
        REV bigint not null,
        CONTAINER_CONSTRAINTS_ID integer not null,
        SPECIMEN_TYPE_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, CONTAINER_CONSTRAINTS_ID, SPECIMEN_TYPE_ID)
    );

    create table CONTAINER_SCHEMA (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(5000),
        NAME varchar(30) not null,
        IS_SHARED boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CENTER_ID integer not null,
        primary key (ID)
    );

    create table CONTAINER_SCHEMA_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(5000),
        NAME varchar(30),
        IS_SHARED boolean,
        CENTER_ID integer,
        primary key (ID, REV)
    );

    create table CONTAINER_SCHEMA_POSITION (
        ID integer not null,
        TIME_INSERTED bigint not null,
        LABEL varchar(4) not null,
        INSERTED_BY_USER_ID integer,
        CONTAINER_SCHEMA_ID integer not null,
        primary key (ID)
    );

    create table CONTAINER_SCHEMA_POSITION_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        LABEL varchar(4),
        CONTAINER_SCHEMA_ID integer,
        primary key (ID, REV)
    );

    create table CONTAINER_TREE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        TEMPERATURE decimal(5,2) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CENTER_LOCATION_ID integer not null,
        OWNING_CENTER_ID integer not null,
        primary key (ID)
    );

    create table CONTAINER_TREE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        TEMPERATURE decimal(5,2),
        CENTER_LOCATION_ID integer,
        OWNING_CENTER_ID integer,
        primary key (ID, REV)
    );

    create table CONTAINER_TYPE (
        DISCRIMINATOR varchar(31) not null,
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(5000),
        IS_ENABLED boolean,
        NAME varchar(50) not null,
        IS_SHARED boolean not null,
        IS_TOP_LEVEL boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CENTER_ID integer not null,
        CONTAINER_SCHEMA_ID integer not null,
        primary key (ID)
    );

    create table CONTAINER_TYPE_AUD (
        DISCRIMINATOR varchar(31) not null,
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(5000),
        IS_ENABLED boolean,
        NAME varchar(50),
        IS_SHARED boolean,
        CENTER_ID integer,
        CONTAINER_SCHEMA_ID integer,
        IS_TOP_LEVEL boolean,
        primary key (ID, REV)
    );

    create table CONTAINER_TYPE_CONTAINER_TYPE (
        PARENT_CONTAINER_TYPE_ID integer not null,
        CHILD_CONTAINER_TYPE_ID integer not null,
        primary key (PARENT_CONTAINER_TYPE_ID, CHILD_CONTAINER_TYPE_ID)
    );

    create table CONTAINER_TYPE_CONTAINER_TYPE_AUD (
        REV bigint not null,
        PARENT_CONTAINER_TYPE_ID integer not null,
        CHILD_CONTAINER_TYPE_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, PARENT_CONTAINER_TYPE_ID, CHILD_CONTAINER_TYPE_ID)
    );

    create table ENTITY (
        ID integer not null,
        TIME_INSERTED bigint not null,
        CLASS_NAME varchar(255),
        NAME varchar(255),
        INSERTED_BY_USER_ID integer,
        primary key (ID)
    );

    create table ENTITY_COLUMN (
        ID integer not null,
        TIME_INSERTED bigint not null,
        NAME varchar(255),
        INSERTED_BY_USER_ID integer,
        ENTITY_PROPERTY_ID integer not null,
        primary key (ID)
    );

    create table ENTITY_FILTER (
        ID integer not null,
        TIME_INSERTED bigint not null,
        FILTER_TYPE integer,
        NAME varchar(255),
        INSERTED_BY_USER_ID integer,
        ENTITY_PROPERTY_ID integer not null,
        primary key (ID)
    );

    create table ENTITY_PROPERTY (
        ID integer not null,
        TIME_INSERTED bigint not null,
        PROPERTY varchar(255),
        INSERTED_BY_USER_ID integer,
        PROPERTY_TYPE_ID integer not null,
        ENTITY_ID integer,
        primary key (ID)
    );

    create table GROUP_AUD (
        ID integer not null,
        REV bigint not null,
        NAME varchar(255),
        primary key (ID, REV)
    );

    create table PATIENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        PNUMBER varchar(100) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        STUDY_ID integer not null,
        primary key (ID)
    );

    create table PATIENT_ANNOTATION (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DECIMAL_SCALE integer not null,
        DECIMAL_VALUE decimal(27,9) not null,
        STRING_VALUE varchar(100),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        SELECTED_VALUE integer,
        ANNOTATION_TYPE_ID integer not null,
        PATIENT_ID integer,
        primary key (ID)
    );

    create table PATIENT_ANNOTATION_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DECIMAL_SCALE integer,
        DECIMAL_VALUE decimal(27,9),
        STRING_VALUE varchar(100),
        SELECTED_VALUE integer,
        ANNOTATION_TYPE_ID integer,
        PATIENT_ID integer,
        primary key (ID, REV)
    );

    create table PATIENT_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        PNUMBER varchar(100),
        STUDY_ID integer,
        primary key (ID, REV)
    );

    create table PATIENT_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        PATIENT_ID integer not null,
        primary key (ID)
    );

    create table PRESERVATION_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION TEXT,
        NAME varchar(100) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table PRESERVATION_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION TEXT,
        NAME varchar(100),
        primary key (ID, REV)
    );

    create table PRINCIPAL (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        IS_ADMIN boolean not null,
        IS_ENABLED boolean,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table PRINCIPAL_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        IS_ADMIN boolean,
        IS_ENABLED boolean,
        primary key (ID, REV)
    );

    create table PROCESSING_EVENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        TIME_DONE bigint not null,
        WORKSHEET varchar(100),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CENTER_ID integer not null,
        primary key (ID)
    );

    create table PROCESSING_EVENT_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        TIME_DONE bigint,
        WORKSHEET varchar(100),
        CENTER_ID integer,
        primary key (ID, REV)
    );

    create table PROCESSING_EVENT_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        PROCESSING_EVENT_ID integer not null,
        primary key (ID)
    );

    create table PROCESSING_EVENT_INPUT_SPECIMEN (
        ID integer not null,
        TIME_INSERTED bigint not null,
        INSERTED_BY_USER_ID integer,
        PROCESSING_EVENT_ID integer not null,
        SPECIMEN_ID integer not null,
        primary key (ID)
    );

    create table PROCESSING_EVENT_INPUT_SPECIMEN_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        PROCESSING_EVENT_ID integer,
        SPECIMEN_ID integer,
        primary key (ID, REV)
    );

    create table PROCESSING_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(10000),
        enabled boolean,
        NAME varchar(50) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        STUDY_ID integer not null,
        primary key (ID)
    );

    create table PROCESSING_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(10000),
        enabled boolean,
        NAME varchar(50),
        STUDY_ID integer,
        primary key (ID, REV)
    );

    create table PROPERTY_MODIFIER (
        ID integer not null,
        TIME_INSERTED bigint not null,
        NAME varchar(255),
        PROPERTY_MODIFIER TEXT,
        INSERTED_BY_USER_ID integer,
        PROPERTY_TYPE_ID integer,
        primary key (ID)
    );

    create table PROPERTY_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        NAME varchar(255),
        INSERTED_BY_USER_ID integer,
        primary key (ID)
    );

    create table REPORT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        DESCRIPTION TEXT,
        IS_COUNT boolean not null,
        IS_PUBLIC boolean not null,
        NAME varchar(255),
        USER_ID integer,
        INSERTED_BY_USER_ID integer,
        ENTITY_ID integer not null,
        primary key (ID)
    );

    create table REPORT_COLUMN (
        ID integer not null,
        TIME_INSERTED bigint not null,
        POSITION integer,
        INSERTED_BY_USER_ID integer,
        COLUMN_ID integer not null,
        PROPERTY_MODIFIER_ID integer,
        REPORT_ID integer,
        primary key (ID)
    );

    create table REPORT_FILTER (
        ID integer not null,
        TIME_INSERTED bigint not null,
        OPERATOR integer,
        POSITION integer,
        INSERTED_BY_USER_ID integer,
        ENTITY_FILTER_ID integer not null,
        REPORT_ID integer,
        primary key (ID)
    );

    create table REPORT_FILTER_VALUE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        POSITION integer,
        SECOND_VALUE TEXT,
        VALUE TEXT,
        INSERTED_BY_USER_ID integer,
        REPORT_FILTER_ID integer,
        primary key (ID)
    );

    create table REQUEST (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        TIME_SUBMITTED bigint,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        STUDY_ID integer not null,
        TO_CENTER_LOCATION_ID integer not null,
        primary key (ID)
    );

    create table REQUEST_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        TIME_SUBMITTED bigint,
        STUDY_ID integer,
        TO_CENTER_LOCATION_ID integer,
        primary key (ID, REV)
    );

    create table REQUEST_SHIPMENT (
        REQUEST_ID integer not null,
        SHIPMENT_ID integer not null,
        primary key (REQUEST_ID, SHIPMENT_ID)
    );

    create table REQUEST_SHIPMENT_AUD (
        REV bigint not null,
        REQUEST_ID integer not null,
        SHIPMENT_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, REQUEST_ID, SHIPMENT_ID)
    );

    create table REQUEST_SPECIMEN (
        ID integer not null,
        TIME_INSERTED bigint not null,
        STATE integer not null,
        INSERTED_BY_USER_ID integer,
        CLAIMED_BY_USER_ID integer,
        REQUEST_ID integer not null,
        SPECIMEN_ID integer not null,
        primary key (ID)
    );

    create table REQUEST_SPECIMEN_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        STATE integer,
        REQUEST_ID integer,
        SPECIMEN_ID integer,
        primary key (ID, REV)
    );

    create table REVISION (
        ID bigint not null,
        COMMITTED_AT bigint,
        GENERATED_AT bigint,
        REVISION_TIMESTAMP bigint,
        USER_ID integer,
        primary key (ID)
    );

    create table REVISION_MODIFIED_TYPE (
        REVISION_ID bigint not null,
        MODIFIED_TYPE varchar(255) not null,
        primary key (REVISION_ID, MODIFIED_TYPE)
    );

    create table SETTING (
        "KEY" varchar(127) not null,
        VALUE varchar(1000) not null,
        primary key ("KEY")
    );

    create table SETTING_AUD (
        "KEY" varchar(127) not null,
        REV bigint not null,
        REVTYPE tinyint,
        VALUE varchar(1000),
        primary key ("KEY", REV)
    );

    create table SHIPMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        BOX_NUMBER varchar(255),
        WAYBILL varchar(255),
        STATE varchar(1),
        TIME_PACKED bigint,
        TIME_RECEIVED bigint,
        TIME_SENT bigint,
        TIME_UNPACKED bigint,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        SHIPPING_METHOD_ID integer not null,
        FROM_CENTER_LOCATION_ID integer not null,
        TO_CENTER_LOCATION_ID integer not null,
        primary key (ID)
    );

    create table SHIPMENT_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        BOX_NUMBER varchar(255),
        WAYBILL varchar(255),
        STATE varchar(1),
        TIME_PACKED bigint,
        TIME_RECEIVED bigint,
        TIME_SENT bigint,
        TIME_UNPACKED bigint,
        SHIPPING_METHOD_ID integer,
        FROM_CENTER_LOCATION_ID integer,
        TO_CENTER_LOCATION_ID integer,
        primary key (ID, REV)
    );

    create table SHIPMENT_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        SHIPMENT_ID integer not null,
        primary key (ID)
    );

    create table SHIPMENT_CONTAINER (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        STATE varchar(4),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CONTAINER_ID integer not null,
        SHIPMENT_ID integer not null,
        primary key (ID)
    );

    create table SHIPMENT_CONTAINER_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        STATE varchar(4),
        CONTAINER_ID integer,
        SHIPMENT_ID integer,
        primary key (ID, REV)
    );

    create table SHIPMENT_CONTAINER_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        SHIPMENT_CONTAINER_ID integer not null,
        primary key (ID)
    );

    create table SHIPMENT_SPECIMEN (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        STATE varchar(4),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        SHIPMENT_ID integer not null,
        SHIPMENT_CONTAINER_ID integer not null,
        SPECIMEN_ID integer not null,
        primary key (ID)
    );

    create table SHIPMENT_SPECIMEN_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        STATE varchar(4),
        SHIPMENT_ID integer,
        SHIPMENT_CONTAINER_ID integer,
        SPECIMEN_ID integer,
        primary key (ID, REV)
    );

    create table SHIPMENT_SPECIMEN_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        SHIPMENT_SPECIMEN_ID integer not null,
        primary key (ID)
    );

    create table SHIPPING_METHOD (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        NAME varchar(255) not null,
        IS_WAYBILL_REQUIRED boolean,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table SHIPPING_METHOD_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        NAME varchar(255),
        IS_WAYBILL_REQUIRED boolean,
        primary key (ID, REV)
    );

    create table SPECIMEN (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        AMOUNT_SCALE integer,
        AMOUNT_VALUE decimal(19,2),
        TIME_CREATED bigint not null,
        IS_USABLE boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        CONTAINER_ID integer not null,
        SPECIMEN_GROUP_ID integer not null,
        CENTER_LOCATION_ID integer,
        ORIGIN_CENTER_LOCATION_ID integer not null,
        CONTAINER_SCHEMA_POSITION_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        AMOUNT_SCALE integer,
        AMOUNT_VALUE decimal(19,2),
        TIME_CREATED bigint,
        IS_USABLE boolean,
        CONTAINER_ID integer,
        SPECIMEN_GROUP_ID integer,
        CENTER_LOCATION_ID integer,
        ORIGIN_CENTER_LOCATION_ID integer,
        CONTAINER_SCHEMA_POSITION_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_COLLECTION_EVENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        COLLECTION_EVENT_ID integer not null,
        SPECIMEN_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_COLLECTION_EVENT_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        COLLECTION_EVENT_ID integer,
        SPECIMEN_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_COMMENT (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        MESSAGE varchar(5000) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        USER_ID integer not null,
        SPECIMEN_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_GROUP (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION TEXT,
        NAME varchar(50) not null,
        TEMPERATURE decimal(5,2) not null,
        PRESERVATION_TYPE_ID binary(255) not null,
        UNIT varchar(20) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        ANATOMICAL_SOURCE_ID integer not null,
        SPECIMEN_TYPE_ID integer not null,
        STUDY_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_GROUP_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION TEXT,
        NAME varchar(50),
        TEMPERATURE decimal(5,2),
        PRESERVATION_TYPE_ID binary(255),
        UNIT varchar(20),
        ANATOMICAL_SOURCE_ID integer,
        SPECIMEN_TYPE_ID integer,
        STUDY_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DECIMAL_SCALE integer not null,
        DECIMAL_VALUE decimal(27,9) not null,
        "COUNT" integer,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        SPECIMEN_CONTAINER_TYPE_ID integer,
        SPECIMEN_GROUP_ID integer not null,
        COLLECTION_EVENT_TYPE_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DECIMAL_SCALE integer,
        DECIMAL_VALUE decimal(27,9),
        "COUNT" integer,
        SPECIMEN_CONTAINER_TYPE_ID integer,
        SPECIMEN_GROUP_ID integer,
        COLLECTION_EVENT_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_LINK (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        ACTUAL_INPUT_CHANGE_SCALE integer,
        ACTUAL_INPUT_CHANGE_VALUE decimal(19,2),
        ACTUAL_OUTPUT_CHANGE_SCALE integer,
        ACTUAL_OUTPUT_CHANGE_VALUE decimal(19,2),
        TIME_DONE bigint not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        INPUT_SPECIMEN_ID integer not null,
        OUTPUT_SPECIMEN_ID integer not null,
        PROCESSING_EVENT_ID integer,
        SPECIMEN_PROCESSING_LINK_TYPE_ID integer,
        primary key (ID)
    );

    create table SPECIMEN_LINK_ANNOTATION (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DECIMAL_SCALE integer not null,
        DECIMAL_VALUE decimal(27,9) not null,
        STRING_VALUE varchar(100),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        SELECTED_VALUE integer,
        ANNOTATION_TYPE_ID integer not null,
        SPECIMEN_LINK_ID integer,
        primary key (ID)
    );

    create table SPECIMEN_LINK_ANNOTATION_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DECIMAL_SCALE integer,
        DECIMAL_VALUE decimal(27,9),
        STRING_VALUE varchar(100),
        SELECTED_VALUE integer,
        ANNOTATION_TYPE_ID integer,
        SPECIMEN_LINK_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_LINK_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        ACTUAL_INPUT_CHANGE_SCALE integer,
        ACTUAL_INPUT_CHANGE_VALUE decimal(19,2),
        ACTUAL_OUTPUT_CHANGE_SCALE integer,
        ACTUAL_OUTPUT_CHANGE_VALUE decimal(19,2),
        TIME_DONE bigint,
        INPUT_SPECIMEN_ID integer,
        OUTPUT_SPECIMEN_ID integer,
        PROCESSING_EVENT_ID integer,
        SPECIMEN_PROCESSING_LINK_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_LINK_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        EXPECTED_INPUT_CHANGE_SCALE integer,
        EXPECTED_INPUT_CHANGE_VALUE decimal(19,2),
        EXPECTED_OUTPUT_CHANGE_SCALE integer,
        EXPECTED_OUTPUT_CHANGE_VALUE decimal(19,2),
        inputContainerType binary(255),
        INPUT_COUNT integer,
        OUTPUT_CONTAINER_TYPE_ID binary(255),
        OUTPUT_COUNT integer,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        INPUT_SPECIMEN_GROUP_ID integer not null,
        OUTPUT_SPECIMEN_GROUP_ID integer not null,
        PROCESSING_TYPE_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        IS_REQUIRED boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        ANNOTATION_TYPE_ID integer not null,
        SPECIMEN_LINK_TYPE_ID integer not null,
        primary key (ID)
    );

    create table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        IS_REQUIRED boolean,
        ANNOTATION_TYPE_ID integer,
        SPECIMEN_LINK_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_LINK_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        EXPECTED_INPUT_CHANGE_SCALE integer,
        EXPECTED_INPUT_CHANGE_VALUE decimal(19,2),
        EXPECTED_OUTPUT_CHANGE_SCALE integer,
        EXPECTED_OUTPUT_CHANGE_VALUE decimal(19,2),
        inputContainerType binary(255),
        INPUT_COUNT integer,
        OUTPUT_CONTAINER_TYPE_ID binary(255),
        OUTPUT_COUNT integer,
        INPUT_SPECIMEN_GROUP_ID integer,
        OUTPUT_SPECIMEN_GROUP_ID integer,
        PROCESSING_TYPE_ID integer,
        primary key (ID, REV)
    );

    create table SPECIMEN_TYPE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION TEXT,
        NAME varchar(100) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table SPECIMEN_TYPE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION TEXT,
        NAME varchar(100),
        primary key (ID, REV)
    );

    create table STUDY (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        DESCRIPTION varchar(50) not null,
        IS_ENABLED boolean,
        NAME varchar(50) not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table STUDY_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        DESCRIPTION varchar(50),
        IS_ENABLED boolean,
        NAME varchar(50),
        primary key (ID, REV)
    );

    create table STUDY_CENTER (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        CENTER_ID binary(255),
        STUDY_ID binary(255),
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        primary key (ID)
    );

    create table STUDY_CENTER_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        CENTER_ID binary(255),
        STUDY_ID binary(255),
        primary key (ID, REV)
    );

    create table STUDY_MEMBERSHIP (
        ID integer not null,
        TIME_INSERTED bigint not null,
        TIME_UPDATED bigint not null,
        VERSION integer not null,
        IS_MANAGER boolean not null,
        INSERTED_BY_USER_ID integer,
        UPDATED_BY_USER_ID integer,
        PRINCIPAL_ID integer not null,
        STUDY_ID integer not null,
        primary key (ID)
    );

    create table STUDY_MEMBERSHIP_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        IS_MANAGER boolean,
        PRINCIPAL_ID integer,
        STUDY_ID integer,
        primary key (ID, REV)
    );

    create table STUDY_MEMBERSHIP_PERMISSION (
        ID integer not null,
        PERMISSION_ID integer not null,
        primary key (ID, PERMISSION_ID)
    );

    create table STUDY_MEMBERSHIP_PERMISSION_AUD (
        REV bigint not null,
        ID integer not null,
        PERMISSION_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, ID, PERMISSION_ID)
    );

    create table STUDY_MEMBERSHIP_ROLE (
        STUDY_MEMBERSHIP_ID integer not null,
        STUDY_ROLE_ID integer not null,
        primary key (STUDY_MEMBERSHIP_ID, STUDY_ROLE_ID)
    );

    create table STUDY_MEMBERSHIP_ROLE_AUD (
        REV bigint not null,
        STUDY_MEMBERSHIP_ID integer not null,
        STUDY_ROLE_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, STUDY_MEMBERSHIP_ID, STUDY_ROLE_ID)
    );

    create table STUDY_ROLE (
        ID integer not null,
        TIME_INSERTED bigint not null,
        NAME varchar(255) not null,
        INSERTED_BY_USER_ID integer,
        primary key (ID)
    );

    create table STUDY_ROLE_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        NAME varchar(255),
        primary key (ID, REV)
    );

    create table STUDY_ROLE_PERMISSION (
        STUDY_ROLE_ID integer not null,
        STUDY_PERMISSION_ID integer not null,
        primary key (STUDY_ROLE_ID, STUDY_PERMISSION_ID)
    );

    create table STUDY_ROLE_PERMISSION_AUD (
        REV bigint not null,
        STUDY_ROLE_ID integer not null,
        STUDY_PERMISSION_ID integer not null,
        REVTYPE tinyint,
        primary key (REV, STUDY_ROLE_ID, STUDY_PERMISSION_ID)
    );

    create table USER_GROUP (
        ID integer not null,
        TIME_INSERTED bigint not null,
        INSERTED_BY_USER_ID integer,
        GROUP_ID integer not null,
        USER_ID integer not null,
        primary key (ID)
    );

    create table USER_GROUP_AUD (
        ID integer not null,
        REV bigint not null,
        REVTYPE tinyint,
        GROUP_ID integer,
        USER_ID integer,
        primary key (ID, REV)
    );

    create table User (
        EMAIL varchar(255) not null,
        FULL_NAME varchar(255),
        LOGIN varchar(255) not null,
        IS_MAILING_LIST_SUBSCRIBER boolean not null,
        IS_PASSWORD_CHANGE_NEEDED boolean not null,
        ID integer not null,
        primary key (ID)
    );

    create table User_AUD (
        ID integer not null,
        REV bigint not null,
        EMAIL varchar(255),
        FULL_NAME varchar(255),
        LOGIN varchar(255),
        IS_MAILING_LIST_SUBSCRIBER boolean,
        IS_PASSWORD_CHANGE_NEEDED boolean,
        primary key (ID, REV)
    );

    create table "GROUP" (
        NAME varchar(255) not null,
        ID integer not null,
        primary key (ID)
    );

    alter table ANATOMICAL_SOURCE 
        add constraint NAME_ unique (NAME);

    alter table ANATOMICAL_SOURCE 
        add constraint FK222949178B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ANATOMICAL_SOURCE 
        add constraint FK22294917C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table ANATOMICAL_SOURCE_AUD 
        add constraint FKEBCD1368A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table ANNOTATION_OPTION 
        add constraint key1 unique (ANNOTATION_TYPE_ID, VALUE);

    alter table ANNOTATION_OPTION 
        add constraint FK997AFF858B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ANNOTATION_OPTION 
        add constraint FK997AFF85C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table ANNOTATION_OPTION 
        add constraint FK997AFF8577EC08A4 
        foreign key (ANNOTATION_TYPE_ID) 
        references ANNOTATION_TYPE;

    alter table ANNOTATION_OPTION_AUD 
        add constraint FK5D61E2D6A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table ANNOTATION_TYPE 
        add constraint ANNOTATION_TYPE_KEY unique (STUDY_ID, NAME);

    alter table ANNOTATION_TYPE 
        add constraint FKB2EA8E4A8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ANNOTATION_TYPE 
        add constraint FKB2EA8E4AC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table ANNOTATION_TYPE 
        add constraint FKB2EA8E4A9F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table ANNOTATION_TYPE_AUD 
        add constraint FK332871BA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER 
        add constraint NAME_ unique (NAME);

    alter table CENTER 
        add constraint FK7645C0558B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CENTER 
        add constraint FK7645C055C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CENTER_AUD 
        add constraint FK623FBBA6A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER_COMMENT 
        add constraint FKDF3FBC558B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CENTER_COMMENT 
        add constraint FKDF3FBC55C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CENTER_COMMENT 
        add constraint FKDF3FBC55A1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table CENTER_COMMENT 
        add constraint FKDF3FBC555C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table CENTER_LOCATION 
        add constraint CENTER_LOCATION_KEY unique (CENTER_ID, NAME);

    alter table CENTER_LOCATION 
        add constraint FK9DEF905F8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CENTER_LOCATION 
        add constraint FK9DEF905FC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CENTER_LOCATION 
        add constraint FK9DEF905F5C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table CENTER_LOCATION_AUD 
        add constraint FK10E76B0A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER_MEMBERSHIP 
        add constraint key1 unique (PRINCIPAL_ID, CENTER_ID);

    alter table CENTER_MEMBERSHIP 
        add constraint FK3483F2008B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CENTER_MEMBERSHIP 
        add constraint FK3483F200C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CENTER_MEMBERSHIP 
        add constraint FK3483F20089FFC277 
        foreign key (PRINCIPAL_ID) 
        references PRINCIPAL;

    alter table CENTER_MEMBERSHIP 
        add constraint FK3483F2005C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table CENTER_MEMBERSHIP_AUD 
        add constraint FK8D671FD1A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER_MEMBERSHIP_ROLE 
        add constraint FK1F847F55FBEFFA28 
        foreign key (CENTER_ROLE_ID) 
        references CENTER_ROLE;

    alter table CENTER_MEMBERSHIP_ROLE 
        add constraint FK1F847F55C66DE3A8 
        foreign key (CENTER_MEMBERSHIP_ID) 
        references CENTER_MEMBERSHIP;

    alter table CENTER_MEMBERSHIP_ROLE_AUD 
        add constraint FKC006FAA6A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER_MEMBERSHIP_STUDY 
        add constraint FKD11BEAAA9F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table CENTER_MEMBERSHIP_STUDY 
        add constraint FKD11BEAAAC66DE3A8 
        foreign key (CENTER_MEMBERSHIP_ID) 
        references CENTER_MEMBERSHIP;

    alter table CENTER_MEMBERSHIP_STUDY_AUD 
        add constraint FKB69EB37BA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER_ROLE 
        add constraint NAME_ unique (NAME);

    alter table CENTER_ROLE 
        add constraint FKE5BF5E208B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CENTER_ROLE_AUD 
        add constraint FK1CCF7BF1A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CENTER_ROLE_PERMISSION 
        add constraint FKCCBC954EFBEFFA28 
        foreign key (CENTER_ROLE_ID) 
        references CENTER_ROLE;

    alter table CENTER_ROLE_PERMISSION_AUD 
        add constraint FK46E16C1FA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table COLLECTION_EVENT 
        add constraint key1 unique (PATIENT_ID, COLLECTION_EVENT_TYPE_ID, VISIT_NUMBER);

    alter table COLLECTION_EVENT 
        add constraint FKEDAD89998B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT 
        add constraint FKEDAD8999C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT 
        add constraint FKEDAD899969F314AA 
        foreign key (PATIENT_ID) 
        references PATIENT;

    alter table COLLECTION_EVENT 
        add constraint FKEDAD8999F5A814C0 
        foreign key (COLLECTION_EVENT_TYPE_ID) 
        references COLLECTION_EVENT_TYPE;

    alter table COLLECTION_EVENT_ANNOTATION 
        add constraint FK5FF6D9958B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_ANNOTATION 
        add constraint FK5FF6D995C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_ANNOTATION 
        add constraint FK5FF6D9958EAACEFC 
        foreign key (SELECTED_VALUE) 
        references ANNOTATION_OPTION;

    alter table COLLECTION_EVENT_ANNOTATION 
        add constraint FK5FF6D9952DC6F505 
        foreign key (ANNOTATION_TYPE_ID) 
        references ANNOTATION_TYPE;

    alter table COLLECTION_EVENT_ANNOTATION 
        add constraint FK5FF6D9955318490D 
        foreign key (COLLECTION_EVENT_ID) 
        references COLLECTION_EVENT;

    alter table COLLECTION_EVENT_ANNOTATION_AUD 
        add constraint FK6F4234E6A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table COLLECTION_EVENT_AUD 
        add constraint FKE25942EAA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table COLLECTION_EVENT_COMMENT 
        add constraint FK1CFC01998B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_COMMENT 
        add constraint FK1CFC0199C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_COMMENT 
        add constraint FK1CFC0199A1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table COLLECTION_EVENT_COMMENT 
        add constraint FK1CFC01995318490D 
        foreign key (COLLECTION_EVENT_ID) 
        references COLLECTION_EVENT;

    alter table COLLECTION_EVENT_TYPE 
        add constraint key1 unique (STUDY_ID, NAME);

    alter table COLLECTION_EVENT_TYPE 
        add constraint FK68D7CE208B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_TYPE 
        add constraint FK68D7CE20C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_TYPE 
        add constraint FK68D7CE209F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE 
        add constraint key1 unique (COLLECTION_EVENT_TYPE_ID, ANNOTATION_TYPE_ID);

    alter table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE 
        add constraint FK8C3722B8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE 
        add constraint FK8C3722BC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE 
        add constraint FK8C3722B2DC6F505 
        foreign key (ANNOTATION_TYPE_ID) 
        references ANNOTATION_TYPE 
        on delete cascade;

    alter table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE 
        add constraint FK8C3722BF5A814C0 
        foreign key (COLLECTION_EVENT_TYPE_ID) 
        references COLLECTION_EVENT_TYPE 
        on delete cascade;

    alter table COLLECTION_EVENT_TYPE_ANNOTATION_TYPE_AUD 
        add constraint FK3844927CA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table COLLECTION_EVENT_TYPE_AUD 
        add constraint FK7E2FEBF1A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER 
        add constraint CONTAINER_KEY_2 unique (CONTAINER_TREE_ID, LABEL);

    alter table CONTAINER 
        add constraint CONTAINER_KEY_3 unique (PARENT_CONTAINER_ID, CONTAINER_SCHEMA_POSITION_ID);

    alter table CONTAINER 
        add constraint INVENTORY_ID_ unique (INVENTORY_ID);

    alter table CONTAINER 
        add constraint FK8D995C618B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER 
        add constraint FK8D995C61C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CONTAINER 
        add constraint FK8D995C61C1EAEDEF 
        foreign key (CONTAINER_TYPE_ID) 
        references CONTAINER_TYPE;

    alter table CONTAINER 
        add constraint FK8D995C61A1DAFE02 
        foreign key (PARENT_CONTAINER_ID) 
        references CONTAINER;

    alter table CONTAINER 
        add constraint FK8D995C612E02C12E 
        foreign key (CONTAINER_SCHEMA_POSITION_ID) 
        references CONTAINER_SCHEMA_POSITION;

    alter table CONTAINER 
        add constraint FK8D995C61B55DE36F 
        foreign key (CONTAINER_TREE_ID) 
        references CONTAINER_TREE;

    alter table CONTAINER 
        add constraint FK8D995C61AE8281E5 
        foreign key (CONTAINER_CONSTRAINTS_ID) 
        references CONTAINER_CONSTRAINTS;

    alter table CONTAINER_AUD 
        add constraint FK2F0E71B2A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_COMMENT 
        add constraint FK9A6C8C618B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER_COMMENT 
        add constraint FK9A6C8C61C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CONTAINER_COMMENT 
        add constraint FK9A6C8C61A1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table CONTAINER_COMMENT 
        add constraint FK9A6C8C61DFE7732C 
        foreign key (CONTAINER_ID) 
        references CONTAINER;

    alter table CONTAINER_CONSTRAINTS 
        add constraint FK61906B388B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER_CONSTRAINTS 
        add constraint FK61906B38C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CONTAINER_CONSTRAINTS 
        add constraint FK61906B385C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table CONTAINER_CONSTRAINTS_ANATOMICAL_SOURCE 
        add constraint FK65FD3FD01F8C95AC 
        foreign key (ANATOMICAL_SOURCE_ID) 
        references ANATOMICAL_SOURCE;

    alter table CONTAINER_CONSTRAINTS_ANATOMICAL_SOURCE 
        add constraint FK65FD3FD0AE8281E5 
        foreign key (CONTAINER_CONSTRAINTS_ID) 
        references CONTAINER_CONSTRAINTS;

    alter table CONTAINER_CONSTRAINTS_ANATOMICAL_SOURCE_AUD 
        add constraint FKA2E505A1A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_CONSTRAINTS_AUD 
        add constraint FKA43D09A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_CONSTRAINTS_PRESERVATION_TYPE 
        add constraint FK1B92AAF61CEA22BE 
        foreign key (PRESERVATION_TYPE_ID) 
        references PRESERVATION_TYPE;

    alter table CONTAINER_CONSTRAINTS_PRESERVATION_TYPE 
        add constraint FK1B92AAF6AE8281E5 
        foreign key (CONTAINER_CONSTRAINTS_ID) 
        references CONTAINER_CONSTRAINTS;

    alter table CONTAINER_CONSTRAINTS_PRESERVATION_TYPE_AUD 
        add constraint FK6BE46DC7A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_CONSTRAINTS_SPECIMEN_TYPE 
        add constraint FKB3EA2D4A38445996 
        foreign key (SPECIMEN_TYPE_ID) 
        references SPECIMEN_TYPE;

    alter table CONTAINER_CONSTRAINTS_SPECIMEN_TYPE 
        add constraint FKB3EA2D4AAE8281E5 
        foreign key (CONTAINER_CONSTRAINTS_ID) 
        references CONTAINER_CONSTRAINTS;

    alter table CONTAINER_CONSTRAINTS_SPECIMEN_TYPE_AUD 
        add constraint FK2D4AA61BA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_SCHEMA 
        add constraint CONTAINER_SCHEMA_KEY unique (CENTER_ID, NAME);

    alter table CONTAINER_SCHEMA 
        add constraint FK1F9D4B3F8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER_SCHEMA 
        add constraint FK1F9D4B3FC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CONTAINER_SCHEMA 
        add constraint FK1F9D4B3F5C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table CONTAINER_SCHEMA_AUD 
        add constraint FK2DA3C190A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_SCHEMA_POSITION 
        add constraint key1 unique (CONTAINER_SCHEMA_ID, LABEL);

    alter table CONTAINER_SCHEMA_POSITION 
        add constraint FK23D7A6298B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER_SCHEMA_POSITION 
        add constraint FK23D7A629C7EA374F 
        foreign key (CONTAINER_SCHEMA_ID) 
        references CONTAINER_SCHEMA;

    alter table CONTAINER_SCHEMA_POSITION_AUD 
        add constraint FK8602977AA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_TREE 
        add constraint FKB2C85CBC8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER_TREE 
        add constraint FKB2C85CBCC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CONTAINER_TREE 
        add constraint FKB2C85CBC6AFE3653 
        foreign key (CENTER_LOCATION_ID) 
        references CENTER_LOCATION;

    alter table CONTAINER_TREE 
        add constraint FKB2C85CBCAE40FBE5 
        foreign key (OWNING_CENTER_ID) 
        references CENTER;

    alter table CONTAINER_TREE_AUD 
        add constraint FK29BFCC8DA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_TYPE 
        add constraint key1 unique (CENTER_ID, NAME);

    alter table CONTAINER_TYPE 
        add constraint FKB2C878588B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table CONTAINER_TYPE 
        add constraint FKB2C87858C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table CONTAINER_TYPE 
        add constraint FKB2C878585C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table CONTAINER_TYPE 
        add constraint FKB2C87858C7EA374F 
        foreign key (CONTAINER_SCHEMA_ID) 
        references CONTAINER_SCHEMA;

    alter table CONTAINER_TYPE_AUD 
        add constraint FKAED0BA29A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table CONTAINER_TYPE_CONTAINER_TYPE 
        add constraint FK5991B31F45213D8C 
        foreign key (CHILD_CONTAINER_TYPE_ID) 
        references CONTAINER_TYPE;

    alter table CONTAINER_TYPE_CONTAINER_TYPE 
        add constraint FK5991B31F1162767B 
        foreign key (PARENT_CONTAINER_TYPE_ID) 
        references CONTAINER_TYPE;

    alter table CONTAINER_TYPE_CONTAINER_TYPE_AUD 
        add constraint FK5613970A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table ENTITY 
        add constraint FK7A30D7E38B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ENTITY_COLUMN 
        add constraint FK16BD7328B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ENTITY_COLUMN 
        add constraint FK16BD73226399BB0 
        foreign key (ENTITY_PROPERTY_ID) 
        references ENTITY_PROPERTY;

    alter table ENTITY_FILTER 
        add constraint FK635CF548B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ENTITY_FILTER 
        add constraint FK635CF5426399BB0 
        foreign key (ENTITY_PROPERTY_ID) 
        references ENTITY_PROPERTY;

    alter table ENTITY_PROPERTY 
        add constraint FK3FC956B18B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table ENTITY_PROPERTY 
        add constraint FK3FC956B12416F9B4 
        foreign key (PROPERTY_TYPE_ID) 
        references PROPERTY_TYPE;

    alter table ENTITY_PROPERTY 
        add constraint FK3FC956B11077DD49 
        foreign key (ENTITY_ID) 
        references ENTITY;

    alter table GROUP_AUD 
        add constraint FK4DC6E4B0C91FDBA 
        foreign key (ID, REV) 
        references PRINCIPAL_AUD;

    alter table PATIENT 
        add constraint key1 unique (STUDY_ID, PNUMBER);

    alter table PATIENT 
        add constraint FKFB9F76E58B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PATIENT 
        add constraint FKFB9F76E5C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PATIENT 
        add constraint FKFB9F76E59F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table PATIENT_ANNOTATION 
        add constraint FKA03BE4C98B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PATIENT_ANNOTATION 
        add constraint FKA03BE4C9C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PATIENT_ANNOTATION 
        add constraint FKA03BE4C98EAACEFC 
        foreign key (SELECTED_VALUE) 
        references ANNOTATION_OPTION;

    alter table PATIENT_ANNOTATION 
        add constraint FKA03BE4C9D90C3DAE 
        foreign key (ANNOTATION_TYPE_ID) 
        references ANNOTATION_TYPE;

    alter table PATIENT_ANNOTATION 
        add constraint FKA03BE4C969F314AA 
        foreign key (PATIENT_ID) 
        references PATIENT;

    alter table PATIENT_ANNOTATION_AUD 
        add constraint FKA2E6861AA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PATIENT_AUD 
        add constraint FK9FBBAA36A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PATIENT_COMMENT 
        add constraint FK901E2E58B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PATIENT_COMMENT 
        add constraint FK901E2E5C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PATIENT_COMMENT 
        add constraint FK901E2E5A1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table PATIENT_COMMENT 
        add constraint FK901E2E569F314AA 
        foreign key (PATIENT_ID) 
        references PATIENT;

    alter table PRESERVATION_TYPE 
        add constraint NAME_ unique (NAME);

    alter table PRESERVATION_TYPE 
        add constraint FKD7BEB43D8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PRESERVATION_TYPE 
        add constraint FKD7BEB43DC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PRESERVATION_TYPE_AUD 
        add constraint FKB4CC7B8EA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PRINCIPAL 
        add constraint FK3A16800E8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PRINCIPAL 
        add constraint FK3A16800EC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PRINCIPAL_AUD 
        add constraint FK4BC7F6DFA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PROCESSING_EVENT 
        add constraint FK327B1E4E8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PROCESSING_EVENT 
        add constraint FK327B1E4EC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PROCESSING_EVENT 
        add constraint FK327B1E4E5C5F21C8 
        foreign key (CENTER_ID) 
        references CENTER;

    alter table PROCESSING_EVENT_AUD 
        add constraint FK2833751FA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PROCESSING_EVENT_COMMENT 
        add constraint FKA958114E8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PROCESSING_EVENT_COMMENT 
        add constraint FKA958114EC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PROCESSING_EVENT_COMMENT 
        add constraint FKA958114EA1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table PROCESSING_EVENT_COMMENT 
        add constraint FKA958114E9E271765 
        foreign key (PROCESSING_EVENT_ID) 
        references PROCESSING_EVENT;

    alter table PROCESSING_EVENT_INPUT_SPECIMEN 
        add constraint key1 unique (SPECIMEN_ID, PROCESSING_EVENT_ID);

    alter table PROCESSING_EVENT_INPUT_SPECIMEN 
        add constraint FK1BA3EE0E8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PROCESSING_EVENT_INPUT_SPECIMEN 
        add constraint FK1BA3EE0E9E271765 
        foreign key (PROCESSING_EVENT_ID) 
        references PROCESSING_EVENT;

    alter table PROCESSING_EVENT_INPUT_SPECIMEN 
        add constraint FK1BA3EE0ECC6E99AA 
        foreign key (SPECIMEN_ID) 
        references SPECIMEN;

    alter table PROCESSING_EVENT_INPUT_SPECIMEN_AUD 
        add constraint FKACEE64DFA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PROCESSING_TYPE 
        add constraint key1 unique (STUDY_ID, NAME);

    alter table PROCESSING_TYPE 
        add constraint FKAF1318E68B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PROCESSING_TYPE 
        add constraint FKAF1318E6C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table PROCESSING_TYPE 
        add constraint FKAF1318E69F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table PROCESSING_TYPE_AUD 
        add constraint FK4C9C63B7A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table PROPERTY_MODIFIER 
        add constraint FK5DF916018B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table PROPERTY_MODIFIER 
        add constraint FK5DF916012416F9B4 
        foreign key (PROPERTY_TYPE_ID) 
        references PROPERTY_TYPE;

    alter table PROPERTY_TYPE 
        add constraint FK7EBECF648B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REPORT 
        add constraint FK8FDF49348B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REPORT 
        add constraint FK8FDF49341077DD49 
        foreign key (ENTITY_ID) 
        references ENTITY;

    alter table REPORT_COLUMN 
        add constraint FKF0B78C18B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REPORT_COLUMN 
        add constraint FKF0B78C1759D0EEC 
        foreign key (COLUMN_ID) 
        references ENTITY_COLUMN;

    alter table REPORT_COLUMN 
        add constraint FKF0B78C16D61CB94 
        foreign key (PROPERTY_MODIFIER_ID) 
        references PROPERTY_MODIFIER;

    alter table REPORT_COLUMN 
        add constraint FKF0B78C13D3B0FA9 
        foreign key (REPORT_ID) 
        references REPORT;

    alter table REPORT_FILTER 
        add constraint FK13D570E38B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REPORT_FILTER 
        add constraint FK13D570E310B32250 
        foreign key (ENTITY_FILTER_ID) 
        references ENTITY_FILTER;

    alter table REPORT_FILTER 
        add constraint FK13D570E33D3B0FA9 
        foreign key (REPORT_ID) 
        references REPORT;

    alter table REPORT_FILTER_VALUE 
        add constraint FK691EF6F58B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REPORT_FILTER_VALUE 
        add constraint FK691EF6F56C5352F2 
        foreign key (REPORT_FILTER_ID) 
        references REPORT_FILTER;

    alter table REQUEST 
        add constraint FK6C1A7E6F8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REQUEST 
        add constraint FK6C1A7E6FC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table REQUEST 
        add constraint FK6C1A7E6F9F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table REQUEST 
        add constraint FK6C1A7E6FE1B3AE97 
        foreign key (TO_CENTER_LOCATION_ID) 
        references CENTER_LOCATION;

    alter table REQUEST_AUD 
        add constraint FKC4F3DCC0A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table REQUEST_SHIPMENT 
        add constraint SHIPMENT_ID_ unique (SHIPMENT_ID);

    alter table REQUEST_SHIPMENT 
        add constraint FKB7EFE16AD4E0C28 
        foreign key (SHIPMENT_ID) 
        references SHIPMENT;

    alter table REQUEST_SHIPMENT 
        add constraint FKB7EFE16A5780706A 
        foreign key (REQUEST_ID) 
        references REQUEST;

    alter table REQUEST_SHIPMENT_AUD 
        add constraint FK91194A3BA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table REQUEST_SPECIMEN 
        add constraint FK579572D88B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table REQUEST_SPECIMEN 
        add constraint FK579572D8ADA57D39 
        foreign key (CLAIMED_BY_USER_ID) 
        references User;

    alter table REQUEST_SPECIMEN 
        add constraint FK579572D85780706A 
        foreign key (REQUEST_ID) 
        references REQUEST;

    alter table REQUEST_SPECIMEN 
        add constraint FK579572D8CC6E99AA 
        foreign key (SPECIMEN_ID) 
        references SPECIMEN;

    alter table REQUEST_SPECIMEN_AUD 
        add constraint FKD79C74A9A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table REVISION 
        add constraint FK1F1AA7DBA1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table REVISION_MODIFIED_TYPE 
        add constraint FKB43DC18CC876CC04 
        foreign key (REVISION_ID) 
        references REVISION;

    alter table SETTING_AUD 
        add constraint FK791E3001A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SHIPMENT 
        add constraint FKFDF619A8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPMENT 
        add constraint FKFDF619AC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPMENT 
        add constraint FKFDF619ADCA49682 
        foreign key (SHIPPING_METHOD_ID) 
        references SHIPPING_METHOD;

    alter table SHIPMENT 
        add constraint FKFDF619A4AC36108 
        foreign key (FROM_CENTER_LOCATION_ID) 
        references CENTER_LOCATION;

    alter table SHIPMENT 
        add constraint FKFDF619AE1B3AE97 
        foreign key (TO_CENTER_LOCATION_ID) 
        references CENTER_LOCATION;

    alter table SHIPMENT_AUD 
        add constraint FK67ED326BA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SHIPMENT_COMMENT 
        add constraint FKD01A489A8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_COMMENT 
        add constraint FKD01A489AC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_COMMENT 
        add constraint FKD01A489AA1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table SHIPMENT_COMMENT 
        add constraint FKD01A489AD4E0C28 
        foreign key (SHIPMENT_ID) 
        references SHIPMENT;

    alter table SHIPMENT_CONTAINER 
        add constraint key1 unique (SHIPMENT_ID, CONTAINER_ID);

    alter table SHIPMENT_CONTAINER 
        add constraint FK7347FFFC8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_CONTAINER 
        add constraint FK7347FFFCC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_CONTAINER 
        add constraint FK7347FFFCDFE7732C 
        foreign key (CONTAINER_ID) 
        references CONTAINER;

    alter table SHIPMENT_CONTAINER 
        add constraint FK7347FFFCD4E0C28 
        foreign key (SHIPMENT_ID) 
        references SHIPMENT;

    alter table SHIPMENT_CONTAINER_AUD 
        add constraint FK8F3BCFCDA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SHIPMENT_CONTAINER_COMMENT 
        add constraint FK4A3764FC8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_CONTAINER_COMMENT 
        add constraint FK4A3764FCC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_CONTAINER_COMMENT 
        add constraint FK4A3764FCA1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table SHIPMENT_CONTAINER_COMMENT 
        add constraint FK4A3764FCF370FF37 
        foreign key (SHIPMENT_CONTAINER_ID) 
        references SHIPMENT_CONTAINER;

    alter table SHIPMENT_SPECIMEN 
        add constraint key1 unique (SHIPMENT_ID, SPECIMEN_ID);

    alter table SHIPMENT_SPECIMEN 
        add constraint FKD7F5F00D8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_SPECIMEN 
        add constraint FKD7F5F00DC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_SPECIMEN 
        add constraint FKD7F5F00DD4E0C28 
        foreign key (SHIPMENT_ID) 
        references SHIPMENT;

    alter table SHIPMENT_SPECIMEN 
        add constraint FKD7F5F00DF370FF37 
        foreign key (SHIPMENT_CONTAINER_ID) 
        references SHIPMENT_CONTAINER;

    alter table SHIPMENT_SPECIMEN 
        add constraint FKD7F5F00DCC6E99AA 
        foreign key (SPECIMEN_ID) 
        references SPECIMEN;

    alter table SHIPMENT_SPECIMEN_AUD 
        add constraint FKC614F5EA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SHIPMENT_SPECIMEN_COMMENT 
        add constraint FKB280B40D8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_SPECIMEN_COMMENT 
        add constraint FKB280B40DC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPMENT_SPECIMEN_COMMENT 
        add constraint FKB280B40DA1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table SHIPMENT_SPECIMEN_COMMENT 
        add constraint FKB280B40DE9A9B3FD 
        foreign key (SHIPMENT_SPECIMEN_ID) 
        references SHIPMENT_SPECIMEN;

    alter table SHIPPING_METHOD 
        add constraint NAME_ unique (NAME);

    alter table SHIPPING_METHOD 
        add constraint FK68E3D4928B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SHIPPING_METHOD 
        add constraint FK68E3D492C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SHIPPING_METHOD_AUD 
        add constraint FKF30F6963A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN 
        add constraint key1 unique (CONTAINER_ID, CONTAINER_SCHEMA_POSITION_ID);

    alter table SPECIMEN 
        add constraint FKAF84F3088B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN 
        add constraint FKAF84F308C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN 
        add constraint FKAF84F308C29C4704 
        foreign key (CONTAINER_ID) 
        references CONTAINER;

    alter table SPECIMEN 
        add constraint FKAF84F30825406799 
        foreign key (SPECIMEN_GROUP_ID) 
        references SPECIMEN_GROUP;

    alter table SPECIMEN 
        add constraint FKAF84F3086AFE3653 
        foreign key (CENTER_LOCATION_ID) 
        references CENTER_LOCATION;

    alter table SPECIMEN 
        add constraint FKAF84F3088748400C 
        foreign key (ORIGIN_CENTER_LOCATION_ID) 
        references CENTER_LOCATION;

    alter table SPECIMEN 
        add constraint FKAF84F3082E02C12E 
        foreign key (CONTAINER_SCHEMA_POSITION_ID) 
        references CONTAINER_SCHEMA_POSITION;

    alter table SPECIMEN_AUD 
        add constraint FKAE705CD9A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_COLLECTION_EVENT 
        add constraint key1 unique (SPECIMEN_ID, COLLECTION_EVENT_ID);

    alter table SPECIMEN_COLLECTION_EVENT 
        add constraint FK43E569F08B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_COLLECTION_EVENT 
        add constraint FK43E569F0C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_COLLECTION_EVENT 
        add constraint FK43E569F05318490D 
        foreign key (COLLECTION_EVENT_ID) 
        references COLLECTION_EVENT;

    alter table SPECIMEN_COLLECTION_EVENT 
        add constraint FK43E569F0CC6E99AA 
        foreign key (SPECIMEN_ID) 
        references SPECIMEN;

    alter table SPECIMEN_COLLECTION_EVENT_AUD 
        add constraint FK9E6B1FC1A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_COMMENT 
        add constraint FK73068C088B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_COMMENT 
        add constraint FK73068C08C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_COMMENT 
        add constraint FK73068C08A1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table SPECIMEN_COMMENT 
        add constraint FK73068C08CC6E99AA 
        foreign key (SPECIMEN_ID) 
        references SPECIMEN;

    alter table SPECIMEN_GROUP 
        add constraint key1 unique (STUDY_ID, NAME);

    alter table SPECIMEN_GROUP 
        add constraint FKD41FEE688B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_GROUP 
        add constraint FKD41FEE68C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_GROUP 
        add constraint FKD41FEE681F8C95AC 
        foreign key (ANATOMICAL_SOURCE_ID) 
        references ANATOMICAL_SOURCE;

    alter table SPECIMEN_GROUP 
        add constraint FKD41FEE6838445996 
        foreign key (SPECIMEN_TYPE_ID) 
        references SPECIMEN_TYPE;

    alter table SPECIMEN_GROUP 
        add constraint FKD41FEE689F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table SPECIMEN_GROUP_AUD 
        add constraint FKCC5EA839A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE 
        add constraint key1 unique (COLLECTION_EVENT_TYPE_ID, SPECIMEN_GROUP_ID);

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE 
        add constraint FKF7F7BF898B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE 
        add constraint FKF7F7BF89C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE 
        add constraint FKF7F7BF89D99C5050 
        foreign key (SPECIMEN_CONTAINER_TYPE_ID) 
        references CONTAINER_TYPE;

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE 
        add constraint FKF7F7BF8925406799 
        foreign key (SPECIMEN_GROUP_ID) 
        references SPECIMEN_GROUP;

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE 
        add constraint FKF7F7BF89F5A814C0 
        foreign key (COLLECTION_EVENT_TYPE_ID) 
        references COLLECTION_EVENT_TYPE;

    alter table SPECIMEN_GROUP_COLLECTION_EVENT_TYPE_AUD 
        add constraint FKAFB700DAA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_LINK 
        add constraint key1 unique (INPUT_SPECIMEN_ID, OUTPUT_SPECIMEN_ID, TIME_DONE);

    alter table SPECIMEN_LINK 
        add constraint FK1FA012D18B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK 
        add constraint FK1FA012D1C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK 
        add constraint FK1FA012D1476EF0B5 
        foreign key (INPUT_SPECIMEN_ID) 
        references SPECIMEN;

    alter table SPECIMEN_LINK 
        add constraint FK1FA012D19411976C 
        foreign key (OUTPUT_SPECIMEN_ID) 
        references SPECIMEN;

    alter table SPECIMEN_LINK 
        add constraint FK1FA012D19E271765 
        foreign key (PROCESSING_EVENT_ID) 
        references PROCESSING_EVENT;

    alter table SPECIMEN_LINK 
        add constraint FK1FA012D1E5F318DC 
        foreign key (SPECIMEN_PROCESSING_LINK_TYPE_ID) 
        references SPECIMEN_LINK_TYPE;

    alter table SPECIMEN_LINK_ANNOTATION 
        add constraint FKA320DD5D8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK_ANNOTATION 
        add constraint FKA320DD5DC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK_ANNOTATION 
        add constraint FKA320DD5D8EAACEFC 
        foreign key (SELECTED_VALUE) 
        references ANNOTATION_OPTION;

    alter table SPECIMEN_LINK_ANNOTATION 
        add constraint FKA320DD5DA92FA6EB 
        foreign key (ANNOTATION_TYPE_ID) 
        references ANNOTATION_TYPE;

    alter table SPECIMEN_LINK_ANNOTATION 
        add constraint FKA320DD5DC077F95B 
        foreign key (SPECIMEN_LINK_ID) 
        references SPECIMEN_LINK;

    alter table SPECIMEN_LINK_ANNOTATION_AUD 
        add constraint FK42B514AEA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_LINK_AUD 
        add constraint FK58F47022A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_LINK_TYPE 
        add constraint key1 unique (PROCESSING_TYPE_ID, INPUT_SPECIMEN_GROUP_ID, OUTPUT_SPECIMEN_GROUP_ID);

    alter table SPECIMEN_LINK_TYPE 
        add constraint FKC5A247E88B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK_TYPE 
        add constraint FKC5A247E8C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK_TYPE 
        add constraint FKC5A247E8E4480A64 
        foreign key (INPUT_SPECIMEN_GROUP_ID) 
        references SPECIMEN_GROUP;

    alter table SPECIMEN_LINK_TYPE 
        add constraint FKC5A247E8CA6A8BDB 
        foreign key (OUTPUT_SPECIMEN_GROUP_ID) 
        references SPECIMEN_GROUP;

    alter table SPECIMEN_LINK_TYPE 
        add constraint FKC5A247E8F76D54F1 
        foreign key (PROCESSING_TYPE_ID) 
        references PROCESSING_TYPE;

    alter table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE 
        add constraint key1 unique (SPECIMEN_LINK_TYPE_ID, ANNOTATION_TYPE_ID);

    alter table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE 
        add constraint FKE0315BF38B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE 
        add constraint FKE0315BF3C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE 
        add constraint FKE0315BF3A92FA6EB 
        foreign key (ANNOTATION_TYPE_ID) 
        references ANNOTATION_TYPE 
        on delete cascade;

    alter table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE 
        add constraint FKE0315BF3CE52AB3E 
        foreign key (SPECIMEN_LINK_TYPE_ID) 
        references SPECIMEN_LINK_TYPE 
        on delete cascade;

    alter table SPECIMEN_LINK_TYPE_ANNOTATION_TYPE_AUD 
        add constraint FK6F985844A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_LINK_TYPE_AUD 
        add constraint FK1A1841B9A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table SPECIMEN_TYPE 
        add constraint NAME_ unique (NAME);

    alter table SPECIMEN_TYPE 
        add constraint FK1FA3F2118B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_TYPE 
        add constraint FK1FA3F211C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table SPECIMEN_TYPE_AUD 
        add constraint FKE976AF62A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY 
        add constraint NAME_ unique (NAME);

    alter table STUDY 
        add constraint FK4B915A98B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table STUDY 
        add constraint FK4B915A9C579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table STUDY_AUD 
        add constraint FK31A046FAA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY_CENTER 
        add constraint FKD31A7AB8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table STUDY_CENTER 
        add constraint FKD31A7ABC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table STUDY_CENTER_AUD 
        add constraint FK479C07FCA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY_MEMBERSHIP 
        add constraint key1 unique (PRINCIPAL_ID, STUDY_ID);

    alter table STUDY_MEMBERSHIP 
        add constraint FKB263C02C8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table STUDY_MEMBERSHIP 
        add constraint FKB263C02CC579A659 
        foreign key (UPDATED_BY_USER_ID) 
        references User;

    alter table STUDY_MEMBERSHIP 
        add constraint FKB263C02C89FFC277 
        foreign key (PRINCIPAL_ID) 
        references PRINCIPAL;

    alter table STUDY_MEMBERSHIP 
        add constraint FKB263C02C9F2CF22A 
        foreign key (STUDY_ID) 
        references STUDY;

    alter table STUDY_MEMBERSHIP_AUD 
        add constraint FK5D1BF7FDA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY_MEMBERSHIP_PERMISSION 
        add constraint FKCF3AB3C253BDE4C6 
        foreign key (ID) 
        references PRINCIPAL;

    alter table STUDY_MEMBERSHIP_PERMISSION_AUD 
        add constraint FK87833093A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY_MEMBERSHIP_ROLE 
        add constraint FK466AACA958BDCE6A 
        foreign key (STUDY_ROLE_ID) 
        references STUDY_ROLE;

    alter table STUDY_MEMBERSHIP_ROLE 
        add constraint FK466AACA9E1E7FE6A 
        foreign key (STUDY_MEMBERSHIP_ID) 
        references STUDY_MEMBERSHIP;

    alter table STUDY_MEMBERSHIP_ROLE_AUD 
        add constraint FK82AE5DFAA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY_ROLE 
        add constraint NAME_ unique (NAME);

    alter table STUDY_ROLE 
        add constraint FK2703D4C8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table STUDY_ROLE_AUD 
        add constraint FKAA64E51DA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table STUDY_ROLE_PERMISSION 
        add constraint FKF3A2C2A258BDCE6A 
        foreign key (STUDY_ROLE_ID) 
        references STUDY_ROLE;

    alter table STUDY_ROLE_PERMISSION_AUD 
        add constraint FK988CF73A9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table USER_GROUP 
        add constraint key1 unique (USER_ID, GROUP_ID);

    alter table USER_GROUP 
        add constraint FKC62E00EB8B8015DC 
        foreign key (INSERTED_BY_USER_ID) 
        references User;

    alter table USER_GROUP 
        add constraint FKC62E00EBC8001B57 
        foreign key (GROUP_ID) 
        references "GROUP";

    alter table USER_GROUP 
        add constraint FKC62E00EBA1E4F83D 
        foreign key (USER_ID) 
        references User;

    alter table USER_GROUP_AUD 
        add constraint FKC49C13CA9D79CA8 
        foreign key (REV) 
        references REVISION;

    alter table User 
        add constraint EMAIL_ unique (EMAIL);

    alter table User 
        add constraint LOGIN_ unique (LOGIN);

    alter table User 
        add constraint FK285FEB53BDE4C6 
        foreign key (ID) 
        references PRINCIPAL;

    alter table User_AUD 
        add constraint FKF3FCA03CC91FDBA 
        foreign key (ID, REV) 
        references PRINCIPAL_AUD;

    alter table "GROUP" 
        add constraint NAME_ unique (NAME);

    alter table "GROUP" 
        add constraint FK40EFE5F53BDE4C6 
        foreign key (ID) 
        references PRINCIPAL;

    create table hibernate_sequences (
         sequence_name varchar(255) not null ,
         next_val bigint,
        primary key ( sequence_name ) 
    ) ;
