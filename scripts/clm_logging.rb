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


end

PvAttrDupFix.new
