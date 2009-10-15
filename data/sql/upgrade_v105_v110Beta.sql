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
