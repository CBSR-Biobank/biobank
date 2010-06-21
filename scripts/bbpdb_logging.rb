#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class Script < ScriptBase

  BBPDB_QUERY = <<BBPDB_QUERY_END
SELECT login_id,timestamp,form_name,actions.action,patient_nr,inventory_id,findex_nr,cindex_nr,punches,details
FROM logging
join users on users.user_nr=logging.user_nr
join forms on forms.form_nr=logging.form_nr
join actions on actions.shortform=logging.action
order by timestamp desc
limit 2000
BBPDB_QUERY_END

  HEADINGS = [ 'login_id', 'timestamp', 'patient_nr', 'inventory_id', 'action', 'form_name', 'cindex_nr',
               'findex_nr', 'punches', 'details' ]

  def initialize
    getDbConnection("bbpdb", 'localhost')
    res = @dbh.query(BBPDB_QUERY)
    f = File.new("bbpdb_logging.csv", "w")
    f.write CSV.generate_line(HEADINGS, ',') << "\n"
    while row = res.fetch_hash do
      values = Array.new
      HEADINGS.each do | heading |
        values << row[heading]
      end
      f.write CSV.generate_line(values, ',') << "\n"
    end
    f.close
    res.free
  end

end

Script.new
