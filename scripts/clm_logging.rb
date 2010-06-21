#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class ClmLogging < ScriptBase

  USAGE = <<USAGE_END
Usage: {script_name} HOST

USAGE_END

  # divide created_on by 1000 to ignore the milliseconds
  QUERY = <<QUERY_END
select *,FROM_UNIXTIME(created_on/1000) as created_on_date
from log_message
join objectattributes on objectattributes.log_id=log_message.log_id
join object_attribute on object_attribute.object_attribute_id=objectattributes.object_attribute_id
join aliquot on aliquot.inventory_id=current_value
join patient_visit on patient_visit.id=aliquot.patient_visit_id
join patient on patient.id=patient_visit.patient_id
where attribute='inventoryId'
and FROM_UNIXTIME(created_on/1000) >= "2010-05-17"
QUERY_END


  def initialize
    unless ARGV.length == 1
      print USAGE.gsub("{script_name}", File.basename(__FILE__))
      exit
    end

    host = ARGV[0]
    getDbConnection("biobank2", host)
    res = @dbh.query(QUERY)
    res.each_hash do |row|
      print row['USERNAME'], ", ", Time.at(row['CREATED_ON'].to_f / 1000.0), ", ", row['OPERATION'],
      ", ", row['PNUMBER'], ", ", row['CURRENT_VALUE'], "\n"
    end
  end

end

# columns for logging are:
# User Date Action Type Patient# Inventory ID Location Details
#
#   Actions is "insert" for all clm imported inventory ids
#   Location is null
#   Details is null

ClmLogging.new
