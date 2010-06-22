#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class ClmLogging < ScriptBase

  USAGE = <<USAGE_END
Usage: {script_name} HOST

USAGE_END

  # divide created_on by 1000 to ignore the milliseconds
  CLM_QUERY = <<CLM_QUERY_END
select *,FROM_UNIXTIME(created_on/1000) as created_on_date
from log_message
join objectattributes on objectattributes.log_id=log_message.log_id
join object_attribute on object_attribute.object_attribute_id=objectattributes.object_attribute_id
join aliquot on aliquot.inventory_id=current_value
join patient_visit on patient_visit.id=aliquot.patient_visit_id
join patient on patient.id=patient_visit.patient_id
where attribute='inventoryId'
and FROM_UNIXTIME(created_on/1000) >= "2010-05-17"
CLM_QUERY_END

  LOG_QUERY = <<LOG_QUERY_END
select * from log where inventory_id like binary '{inventory_id}' and action='insert'
LOG_QUERY_END

  def initialize
    unless ARGV.length == 1
      print USAGE.gsub("{script_name}", File.basename(__FILE__))
      exit
    end

    host = ARGV[0]
    getDbConnection("biobank2", host)
    res = @dbh.query(CLM_QUERY)
    count = 0
    res.each_hash do |row|
      username = row['USERNAME']
      date = Time.at(row['CREATED_ON'].to_f / 1000.0).strftime("%Y-%m-%d %H:%M");
      pnumber = row['PNUMBER']
      inventory_id = row['CURRENT_VALUE']
      #print row['USERNAME'], ", ", Time.at(row['CREATED_ON'].to_f / 1000.0), ", ", row['OPERATION'],
      #", ", row['PNUMBER'], ", ", inventory_id, "\n"

      # should first make sure this inventory id is not in the new logging table
      logres = @dbh.query(LOG_QUERY.gsub('{inventory_id}', inventory_id))
      if (logres.num_rows > 0)
        print "  inventory id: #{inventory_id} already in log table\n";
        next
      end

      @dbh.query("insert into log (username,date,action,patient_number,inventory_id) values ('#{username}','#{date}','insert','#{pnumber}','#{inventory_id}')");
      count = count +1
      print "  added insert for #{inventory_id}\n"

    end

    print "  records added to log: #{count}\n"
  end

end

ClmLogging.new
