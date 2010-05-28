#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class PvAttrDupFix < ScriptBase

  QUERY = <<QUERY_END
select pv_attr.*
from pv_attr
join (
SELECT value,study_pv_attr_id,patient_visit_id,count(*) as cnt
FROM pv_attr
group by value,study_pv_attr_id,patient_visit_id) A
on A.value=pv_attr.value and A.study_pv_attr_id=pv_attr.study_pv_attr_id
and A.patient_visit_id=pv_attr.patient_visit_id
where A.cnt > 1
QUERY_END

  def initialize
    getDbConnection("biobank2", 'localhost')
    attributes = Hash.new
    res = @dbh.query(QUERY)
    res.each_hash do |row|
      key = "#{row['VALUE']}:#{row['STUDY_PV_ATTR_ID']}:#{row['PATIENT_VISIT_ID']}"
      if (!attributes.has_key? key)
        attributes[key] = Array.new
      end
      attributes[key].push({
                             'id' => row['ID'],
                             'value' => row['VALUE'],
                             'study_pv_attr_id' => row['STUDY_PV_ATTR_ID'],
                             'patient_visit_id' => row['PATIENT_VISIT_ID']
                           })
    end

    attributes.each_key do |key|
      print key
      attributes[key].each do |attr|
        print " #{attr['id']}"
      end
      print "\n"
    end
  end
end

PvAttrDupFix.new
