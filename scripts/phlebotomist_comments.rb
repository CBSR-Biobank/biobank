#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"

class PhlebotomistComments < ScriptBase

  BBPDB_QUERY = <<BBPDB_QUERY_END
SELECT patient.dec_chr_nr,patient_visit.*
FROM patient_visit
join study_list on study_list.study_nr=patient_visit.study_nr
join patient on patient.patient_nr=patient_visit.patient_nr
where study_name_short!='BBP' and length(phlebotomist_id)>0
BBPDB_QUERY_END

  def initialize
    getDbConnection("bbpdb", 'localhost')
    res = @dbh.query(BBPDB_QUERY)
    visits = Hash.new
    res.each_hash do |row|
      visits[row['bb2_pv_id']] = {
        'pnumber' => row['dec_chr_nr'],
        'comment' => row['phlebotomist_id'],
        'date_processed' => row['date_received'] }
    end
    res.free

    rowsUpdated = 0
    getDbConnection("biobank2", 'localhost')
    visits.each_key do |id|
      res = @dbh.query("SELECT * FROM patient_visit
join clinic_shipment_patient on clinic_shipment_patient.id=patient_visit.clinic_shipment_patient_id
join patient on patient.id=clinic_shipment_patient.patient_id
where patient_visit.id=#{id}")
      if ((@dbh.affected_rows < 1) || (@dbh.affected_rows > 1))
        raise "patient visit id is invalid: #{id}"
      end

      # make sure pnumber matches
      row = res.fetch_hash
      if (row['pnumber'] == visits[id]['pnumber'])
        raise "patient visit pnumber does not match: #{row['pnumber']}, #{visits[id]['pnumber']}"
      end

      # make sure received matches
      if (row['date_processed'] == visits[id]['date_processed'])
        raise "patient visit date processed does not match: #{row['date_processed']}, #{visits[id]['date_processed']}"
      end

      comment = @dbh.escape_string("#{visits[id]['comment']}")
      @dbh.query("UPDATE patient_visit SET comment='#{comment}' WHERE id=#{id}")
      rowsUpdated += 1
    end
    res.free

    puts "rows updated: #{rowsUpdated}"
  end

end

PhlebotomistComments.new
