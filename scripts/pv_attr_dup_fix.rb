#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class PvAttrDupFix < ScriptBase

  USAGE = <<USAGE_END
Usage: {script_name} HOST

Removes duplicate rows in PV_ATTR table. HOST is the DNS name of the machine running
the MySQL database.
USAGE_END

  QUERY = <<QUERY_END
SELECT pv_attr.*
FROM pv_attr
JOIN (
SELECT value,study_pv_attr_id,patient_visit_id,count(*) as cnt
FROM pv_attr
GROUP by value,study_pv_attr_id,patient_visit_id) A
ON A.value=pv_attr.value AND A.study_pv_attr_id=pv_attr.study_pv_attr_id
AND A.patient_visit_id=pv_attr.patient_visit_id
WHERE A.cnt > 1
QUERY_END

  def initialize
    unless ARGV.length == 1
      print USAGE.gsub("{script_name}", File.basename(__FILE__))
      exit
    end

    host = ARGV[0]

    getDbConnection("biobank2", host)
    attributes = Hash.new
    res = @dbh.query(QUERY)
    res.each_hash do |row|
      key = "#{row['VALUE']}:#{row['STUDY_PV_ATTR_ID']}:#{row['PATIENT_VISIT_ID']}"
      if (!attributes.has_key? key)
        attributes[key] = Array.new
      end
      attributes[key].push({
                             'id' => row['ID'].to_i,
                             'value' => row['VALUE'],
                             'study_pv_attr_id' => row['STUDY_PV_ATTR_ID'],
                             'patient_visit_id' => row['PATIENT_VISIT_ID']
                           })
    end

    # determine the pv_attr with maximum ID
    max_ids = Hash.new
    attributes.each_key do |key|
      max_id = 0;
      attributes[key].each do |attr|
        if (attr['id'] > max_id)
          max_id = attr['id']
        end
      end
      max_ids[key] = max_id
    end

    # delete all pv_attr but the one with maximum ID
    attributes.each_key do |key|
      attributes[key].each do |attr|
        if (attr['id'] < max_ids[key])
          puts "#{key}: #{attr['id']} deleted"
          @dbh.query("DELETE FROM pv_attr WHERE id=#{attr['id']} ")
        end
      end
    end
  end
end

PvAttrDupFix.new
