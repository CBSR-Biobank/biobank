#
# MySQLDiff 1.5.0
#
# http://www.mysqldiff.org
# (c) 2001-2004, Lippe-Net Online-Service
#
# Create time: 15.10.2009 15:23
#
# --------------------------------------------------------

# Source info
# Host: aicml-med
# Database: biobank2
# --------------------------------------------------------
# Target info
# Host: localhost
# Database: biobank2
# --------------------------------------------------------

#

SET FOREIGN_KEY_CHECKS = 0;

#
# DDL START
#

drop table IF EXISTS abstract_sample;
drop table IF EXISTS abstract_sample_type;
drop table IF EXISTS clinic_study;
drop table IF EXISTS container_num_scheme;
drop table IF EXISTS sample_cell;
drop table IF EXISTS sample_derivative;
drop table IF EXISTS sample_derivative_type;
drop table IF EXISTS sample_fluid;
drop table IF EXISTS sample_molecular;
drop table IF EXISTS sample_tissue;
drop table IF EXISTS sc_holds_storage_type;
drop table IF EXISTS sdata;
drop table IF EXISTS sdata_type;
drop table IF EXISTS shipment;
drop table IF EXISTS site_storage_type;
drop table IF EXISTS st_child_type;
drop table IF EXISTS st_holds_type;
drop table IF EXISTS st_sample_class;
drop table IF EXISTS st_sample_dt;
drop table IF EXISTS storage_container;
drop table IF EXISTS storage_containter;
drop table IF EXISTS storage_type;
drop table IF EXISTS storage_type_storage_type;
drop table IF EXISTS study_sdata;
drop table IF EXISTS worksheet;


ALTER TABLE pv_info
    ADD ALLOWED_VALUES varchar(255) NULL DEFAULT NULL COMMENT '' COLLATE latin1_swedish_ci AFTER LABEL,

    DROP POSSIBLE_VALUES;



ALTER TABLE pv_info_possible
    ADD SITE_ID int(11) NULL DEFAULT NULL COMMENT '',

    ADD INDEX FK546E6B693F52C885 (SITE_ID);


#
# DDL END
#

SET FOREIGN_KEY_CHECKS = 1;
