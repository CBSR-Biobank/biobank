-- drop table if exists LOG_MESSAGE;
-- drop table if exists OBJECT_ATTRIBUTE;
-- drop table if exists OBJECTATTRIBUTES;

drop table if exists LOG;
create table LOG (ID integer not null AUTO_INCREMENT, 
USERNAME varchar(100), 
DATE datetime, ACTION varchar(100), PATIENT_NUMBER varchar(100), INVENTORY_ID varchar(100), LOCATION_LABEL varchar(255), DETAILS text, TYPE varchar(100), primary key (ID));
