# Replace the <<database_name>> with proper database name that is to be created. 

#CREATE DATABASE IF NOT EXISTS <<database_name>>;

#USE <<database_name>>;

DROP TABLE IF EXISTS log_message;
CREATE TABLE  log_message (
  LOG_ID bigint(200) NOT NULL auto_increment,
  APPLICATION varchar(25) default NULL,
  SERVER varchar(50) default NULL,
  CATEGORY varchar(255) default NULL,
  THREAD varchar(255) default NULL,
  USERNAME varchar(255) default NULL,
  SESSION_ID varchar(255) default NULL,
  MSG text,
  THROWABLE text,
  NDC text,
  CREATED_ON bigint(20) NOT NULL,
  OBJECT_ID varchar(255) default NULL,
  OBJECT_NAME varchar(255) default NULL,
  ORGANIZATION varchar(255) default NULL,
  OPERATION varchar(50) default NULL,
  PRIMARY KEY  (LOG_ID),
  KEY APPLICATION_LOGTAB_INDX (APPLICATION),
  KEY SERVER_LOGTAB_INDX (SERVER),
  KEY THREAD_LOGTAB_INDX (THREAD),
  KEY CREATED_ON_LOGTAB_INDX (CREATED_ON),
  KEY LOGID_LOGTAB_INDX (LOG_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS object_attribute;
CREATE TABLE  object_attribute (
  OBJECT_ATTRIBUTE_ID bigint(200) NOT NULL auto_increment,
  CURRENT_VALUE varchar(255) default NULL,
  PREVIOUS_VALUE varchar(255) default NULL,
  ATTRIBUTE varchar(255) NOT NULL default '',
  PRIMARY KEY  (OBJECT_ATTRIBUTE_ID),
  KEY OAID_INDX (OBJECT_ATTRIBUTE_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

DROP TABLE IF EXISTS objectattributes;
CREATE TABLE  objectattributes (
  LOG_ID bigint(200) NOT NULL default '0',
  OBJECT_ATTRIBUTE_ID bigint(200) NOT NULL default '0',
  KEY Index_2 (LOG_ID),
  KEY FK_objectattributes_2 (OBJECT_ATTRIBUTE_ID)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;
