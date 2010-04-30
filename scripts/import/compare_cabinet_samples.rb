#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"
require 'date'

# used to determine differences between BBPDB and BioBank2 databases. This scripts extracts all
# cabinet samples since 2010-04-15 from both databases into CSV files. The two files can then
# be diff'ed to see if any differences exist between the two.

class Script < ScriptBase

  BBPDB_QUERY = <<BBPDB_QUERY_END
SELECT inventory_id,date(process_date),study_name_short,dec_chr_nr,date_received,sample_name_short,
cnum,drawer, bin, binpos
FROM cabinet
join patient_visit on patient_visit.visit_nr=cabinet.visit_nr
join patient on patient.patient_nr=cabinet.patient_nr
join study_list on study_list.study_nr=cabinet.study_nr
join sample_list on sample_list.sample_nr=cabinet.sample_nr
where process_date>='2010-04-15'
order by inventory_id
BBPDB_QUERY_END

  BB2_QUERY = <<BB2_QUERY
select inventory_id,date(link_date),study.name_short,patient.pnumber,
patient_visit.date_processed,sample_type.name_short,container.label,row
from aliquot
join patient_visit on patient_visit.id=aliquot.patient_visit_id
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
join sample_type on sample_type.id=aliquot.sample_type_id
join aliquot_position on aliquot_position.aliquot_id=aliquot.id
join abstract_position on abstract_position.id=aliquot_position.abstract_position_id
join container on container.id=aliquot_position.container_id
join container_path on container_path.container_id=container.id
where link_date>='2010-04-15'
and locate('/', path)<>0
and substr(path, 1, locate('/', path)-1) in
(SELECT container.id
    FROM container
    join container_type on container_type.id=container.container_type_id
    where name like 'Cabinet%')
order by inventory_id
BB2_QUERY

  def initialize
    getBbpdbCabinetSamples
    getBiobank2CabinetSamples
  end

  def getBbpdbCabinetSamples
    getDbConnection("bbpdb")
    res = @dbh.query(BBPDB_QUERY)
    f = File.new("bbpdb_cabinet.csv", "w")
    while row = res.fetch_row do
      buf = ','
      label = sprintf("%02d%s%02d%s", row[6], row[7], row[8], row[9])
      f.write CSV.generate_line([row[0], row[1], row[2], row[3], row[5], label], ',') << "\n"
    end
    f.close
  end

  def getBiobank2CabinetSamples
    getDbConnection("biobank2")
    res = @dbh.query(BB2_QUERY)
    f = File.new("biobank2_cabinet.csv", "w")
    while row = res.fetch_row do
      buf = ','
      label = sprintf("%s%c%c", row[6], CBSR_ALPHA[row[7].to_i / CBSR_ALPHA.length], CBSR_ALPHA[row[7].to_i % CBSR_ALPHA.length])
      f.write CSV.generate_line([row[0], row[1], getStudyOldName(row[2]), row[3], row[5], label], ',') << "\n"
    end
    f.close
  end

end

Script.new
