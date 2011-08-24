#!/usr/bin/ruby -w

#-------------------------------------------------------------------------------
#
# Fix for issue #1278.
#
# Creates as many source specimens in the BioBank 3 database as there were
# source vessels for each patient visit. The quantity field in the
# pv_source_vessel table tells us how many source vessels there are per
# patient visit.
#
#-------------------------------------------------------------------------------

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"
require 'time'
require 'pp'
require 'rubygems'
require 'choice'

class FixSpcIdLowerCase < ScriptBase

  LOWER_CASE_SPC_ID_QRY = <<LOWER_CASE_SPC_ID_QRY_END
select id,inventory_id
from specimen spc
where lower(spc.inventory_id)=spc.inventory_id
and length(spc.inventory_id)=10
and spc.inventory_id!='sw missing';
LOWER_CASE_SPC_ID_QRY_END

  SPC_ID_QRY = <<SPC_ID_QRY_END
select pnumber,topspc.created_at date_drawn,stype.name spc_type,spc.id,
spc.inventory_id,spc.created_at,act.name astatus,spc.comment
from specimen spc
join specimen topspc on topspc.id=spc.top_specimen_id
join collection_event ce on ce.id=topspc.collection_event_id
join patient pt on pt.id=ce.patient_id
join specimen_type stype on stype.id=spc.specimen_type_id
join activity_status act on act.id=spc.activity_status_id
where spc.inventory_id='{inventory_id}';
SPC_ID_QRY_END

  FIELDS = [ 'pnumber', 'date_drawn', 'spc_type',
             'inventory_id', 'created_at', 'astatus', 'comment' ]

  SPC_INSERT_BASE_QRY = <<SPC_INSERT_QRY
SPC_INSERT_QRY

  def initialize
    Choice.options do
      header 'options:'

      separator 'Optional:'

      option :sql do
        short '-s'
        long '--sql'
        desc 'use sql insert statements to add missing source speciemens to database'
      end

      separator 'Common:'

      option :help do
        short '-h'
        long '--help'
        desc 'Show this message.'
      end
    end

    getDbConnection("biobank", 'localhost')
    lower_case_spcs = Array.new
    res = @dbh.query(LOWER_CASE_SPC_ID_QRY)
    res.each_hash do |row|
      lower_case_spcs << { 'id' => row['id'], 'inventory_id' => row['inventory_id'] }
    end

    # this hash has a value of 'true' if there is an upper case equivalent in
    # the database
    lc_spc_id_info = Hash.new

    lower_case_spcs.each do |inv_id|
      res = @dbh.query(SPC_ID_QRY.gsub('{inventory_id}', inv_id['inventory_id'].upcase))
      has_upper = (res.num_rows == 1)
      lc_spc_id_info[inv_id['inventory_id']] = { 'has_upper' => has_upper, 'id' => inv_id['id'] }
    end

    date_str = Time.new.strftime("%Y-%m-%d")

    lc_spc_id_info.each do |lc_inv_id, value|
      if value['has_upper']

        # set activity status set to Closed, set comment of "<date> barcode
        # invalid", and set created_at time will be copied to the uppercase
        # one.
        print "setting #{lc_inv_id} activity status to closed\n"
        @dbh.query("update specimen spc, activity_status astat
set spc.activity_status_id=astat.id,
spc.comment='#{date_str} barcode invalid'
where spc.inventory_id='#{lc_inv_id}'
and astat.name='Closed'")
        @dbh.query("update specimen lc_spc, specimen uc_spc
set uc_spc.created_at=lc_spc.created_at
where lc_spc.inventory_id='#{lc_inv_id}'
and uc_spc.inventory_id='#{lc_inv_id.upcase}'")

      else

        #  the inventory ID will be set to the upper case one.
        print "setting #{lc_inv_id.upcase} created at time\n"
        @dbh.query("update specimen spc set spc.inventory_id='#{lc_inv_id.upcase}' where spc.id=#{value['id']}")

      end
    end
  end
end

FixSpcIdLowerCase.new
