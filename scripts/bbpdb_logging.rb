#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

# BioBank2 logging table
#CREATE TABLE `log` (
#  `ID` int(11) NOT NULL AUTO_INCREMENT,
#  `USERNAME` varchar(100) DEFAULT NULL,
#  `DATE` datetime DEFAULT NULL,
#  `ACTION` varchar(100) DEFAULT NULL,
#  `PATIENT_NUMBER` varchar(100) DEFAULT NULL,
#  `INVENTORY_ID` varchar(100) DEFAULT NULL,
#  `LOCATION_LABEL` varchar(255) DEFAULT NULL,
#  `DETAILS` text,
#  `TYPE` varchar(100) DEFAULT NULL,
#  PRIMARY KEY (`ID`)
#) ENGINE=MyISAM AUTO_INCREMENT=11563 DEFAULT CHARSET=latin1;


class Script < ScriptBase

  USAGE = <<USAGE_END
Usage: {script_name} HOST

USAGE_END

  BBPDB_LOGGING_QUERY = <<BBPDB_QUERY_END
SELECT login_id,timestamp,form_name,actions.action,logging.patient_nr,
logging.inventory_id,findex_nr,cindex_nr,punches,details,
fnum,rack,box,cell,cnum,drawer,bin,binpos
FROM logging
join users on users.user_nr=logging.user_nr
join forms on forms.form_nr=logging.form_nr
join actions on actions.shortform=logging.action
left join freezer on freezer.index_nr=logging.findex_nr
left join cabinet on cabinet.index_nr=logging.cindex_nr
where timestamp < "2010-05-18"
order by timestamp
BBPDB_QUERY_END

  HEADINGS = [ 'login_id', 'timestamp', 'action', 'patient_nr', 'inventory_id',
               'location', 'details', 'form_name' ]

  def initialize
    unless ARGV.length == 1
      print USAGE.gsub("{script_name}", File.basename(__FILE__))
      exit
    end

    host = ARGV[0]
    getDbConnection("bbpdb", host)
    res = @dbh.query(BBPDB_LOGGING_QUERY)
    f = File.new("bbpdb_logging.csv", "w")
    f.write CSV.generate_line(HEADINGS, ',') << "\n"
    while row = res.fetch_hash do
      values = Array.new
      HEADINGS.each do | heading |
        if (heading == "location")
          if (!row['fnum'].nil? and !row['rack'].nil? and !row['box'].nil? and !row['cell'].nil?)
            values << sprintf("%02d%s%02d%s", row['fnum'], row['rack'], row['box'], row['cell'])
          elsif (!row['cnum'].nil? and !row['drawer'].nil? and !row['bin'].nil? and !row['binpos'].nil?)
            values << sprintf("%02d%s%02d%s", row['cnum'], row['drawer'], row['bin'], row['binpos'])
          else
            values << ""
          end
        else
          values << row[heading]
        end
      end
      f.write CSV.generate_line(values, ',') << "\n"
    end
    f.close
    res.free
  end

end

Script.new
