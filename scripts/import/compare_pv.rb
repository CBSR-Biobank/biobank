#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"
require 'date'

class Script < ScriptBase

  BBPDB_QUERY = <<BBPDB_QUERY_END
SELECT dec_chr_nr,date(date_received),study_name_short
FROM patient_visit
join patient on patient.patient_nr=patient_visit.patient_nr
join study_list on study_list.study_nr=patient_visit.study_nr
where date_received>='2010-04-15'
order by dec_chr_nr
BBPDB_QUERY_END

  BB2_QUERY = <<BB2_QUERY
select patient.pnumber,date(date_processed),study.name_short
from patient_visit
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
where date_processed>='2010-04-15'
order by pnumber
BB2_QUERY

  def initialize
    getBbpdbCabinetSamples
    getBiobank2CabinetSamples
  end

  def getBbpdbCabinetSamples
    getDbConnection("bbpdb")
    res = @dbh.query(BBPDB_QUERY)
    f = File.new("bbpdb_pv.csv", "w")
    while row = res.fetch_row do
      buf = ','
      f.write CSV.generate_line([row[0], row[1], row[2]], ',') << "\n"
    end
    f.close
  end

  def getBiobank2CabinetSamples
    getDbConnection("biobank2")
    res = @dbh.query(BB2_QUERY)
    f = File.new("biobank2_pv.csv", "w")
    while row = res.fetch_row do
      buf = ','
      label = sprintf("%s%c%c", row[6], CBSR_ALPHA[row[7].to_i / CBSR_ALPHA.length], CBSR_ALPHA[row[7].to_i % CBSR_ALPHA.length])
      f.write CSV.generate_line([row[0], row[1], getStudyOldName(row[2])], ',') << "\n"
    end
    f.close
  end

end

Script.new
