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
limit 1000
BBPDB_QUERY_END

  def initialize
    headings = false
    getDbConnection("bbpdb", 'aicml-med')
    res = @dbh.query(BBPDB_QUERY)
    f = File.new("bbpdb_logging.csv", "w")
    while row = res.fetch_hash do
      if (!headings)
        f.write CSV.generate_line(row.keys, ',') << "\n"
        headings = true
      end
      f.write CSV.generate_line(row.values, ',') << "\n"
    end
    f.close
    res.free
  end

end

Script.new
