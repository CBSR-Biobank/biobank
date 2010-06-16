#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class ClmLogging < ScriptBase

  USAGE = <<USAGE_END
Usage: {script_name} HOST

USAGE_END

  QUERY = <<QUERY_END
select *
from log_message
join objectattributes on objectattributes.log_id=log_message.log_id
join object_attribute on object_attribute.object_attribute_id=objectattributes.object_attribute_id
where attribute='inventoryId'
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
      print row['USERNAME'], ", ", row['CURRENT_VALUE'], ", ", Time.at(row['CREATED_ON'].to_f / 1000.0), " - (", row['CREATED_ON'], "), ", row['OPERATION'], "\n"
    end
  end

end

ClmLogging.new
