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

class FixSourceVesselQty < ScriptBase

  BIOBANK2_QRY = <<BIOBANK2_QRY_END
select pvsv.id,st.name_short study,pt.pnumber,
if(pvsv.time_drawn is null,pv.date_drawn,
   addtime(timestamp(date(pv.date_drawn)), time(pvsv.time_drawn))) created_at,
quantity,sv.name
FROM pv_source_vessel as pvsv
join patient_visit as pv on pv.id=pvsv.patient_visit_id
join clinic_shipment_patient csp on csp.id=pv.clinic_shipment_patient_id
join patient pt on pt.id=csp.patient_id
join study st on st.id=pt.study_id
JOIN source_vessel as sv on sv.id=pvsv.source_vessel_id
where quantity > 1
order by created_at
BIOBANK2_QRY_END

  BIOBANK_QRY = <<BIOBANK_QRY_END
select s.name_short,pt.pnumber,st.name,spc.*
from specimen spc
join specimen_type st on st.id=spc.specimen_type_id
join collection_event ce on ce.id=spc.collection_event_id
join patient pt on pt.id=ce.patient_id
join study s on s.id=pt.study_id
where spc.id=spc.top_specimen_id
and s.name_short='{study_name}' and pt.pnumber='{pnumber}'
and spc.created_at='{created_at}'
and st.name='{specimen_type_name}'
BIOBANK_QRY_END

  SPC_INSERT_BASE_QRY = <<SPC_INSERT_QRY
insert into  specimen (inventory_id,created_at,top_specimen_id,quantity,activity_status_id,
collection_event_id,original_collection_event_id,processing_event_id,
specimen_type_id,parent_specimen_id,origin_info_id,
current_center_id) values
SPC_INSERT_QRY

  FIELDS = [ 'QUANTITY', 'ACTIVITY_STATUS_ID', 'COLLECTION_EVENT_ID',
             'ORIGINAL_COLLECTION_EVENT_ID', 'PROCESSING_EVENT_ID',
             'SPECIMEN_TYPE_ID', 'PARENT_SPECIMEN_ID',
             'ORIGIN_INFO_ID', 'CURRENT_CENTER_ID' ]

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

    print "inserting missing source specimens in database\n" if (Choice.choices[:sql])

    getDbConnection("biobank2", 'localhost')
    res = @dbh.query(BIOBANK2_QRY)
    pvsvs = Array.new
    res.each_hash do |row|

      invalid_date = false
      year = row['created_at'][0..4].to_i
      if (year < 1970)
        created_at = row['created_at']
        invalid_date = false
      else
        created_at = Time.parse(row['created_at']).utc.strftime("%Y-%m-%d %T")
      end

      pvsvs << {
        'study' => row['study'],
        'pnumber' => row['pnumber'],
        'specimen_type_name' => row['name'],
        'quantity' => row['quantity'].to_i,
        'created_at_unformatted' => row['created_at'],
        'created_at' => created_at,
        'invalid_date' => invalid_date
      }
    end
    res.free
    #pp pvsvs

    @dbh2 = Mysql.real_connect('localhost', "dummy", "ozzy498", "biobank")
    @dbh2.query("ALTER TABLE specimen MODIFY COLUMN ID INT(11) NOT NULL auto_increment") if (Choice.choices[:sql])

    pvsvs.each do |pvsv|
      qry = BIOBANK_QRY.gsub("{study_name}", pvsv['study']);
      qry = qry.gsub("{pnumber}", pvsv['pnumber']);
      qry = qry.gsub("{specimen_type_name}", pvsv['specimen_type_name']);

      if pvsv['invalid_date']
        qry = qry.gsub("{created_at}", pvsv['created_at_unformatted']);
      else
        qry = qry.gsub("{created_at}", pvsv['created_at']);
      end

      #print qry, "\n"

      res = @dbh2.query(qry)
      if (res.num_rows < 1)
        print "no result for: ",
        [ pvsv['study'],  pvsv['pnumber'],  pvsv['created_at_unformatted'], pvsv['created_at'],
          pvsv['specimen_type_name'], pvsv['quantity'] ].join(", "), "\n"
      elsif (res.num_rows == pvsv['quantity'])
        print "already fixed: ",
        [ pvsv['study'],  pvsv['pnumber'],  pvsv['created_at_unformatted'], pvsv['created_at'],
          pvsv['specimen_type_name'], pvsv['quantity'] ].join(", "), "\n"
      elsif (res.num_rows < pvsv['quantity'])
        # add pvsv['quantity']-res.num_rows specimens to this collection event
        row = res.fetch_hash

        print "patient #{pvsv['pnumber']}, adding #{pvsv['quantity']-res.num_rows} specimen(s) of type '#{ pvsv['specimen_type_name']}' with date '#{ pvsv['created_at']}'\n"

        for i in 1..pvsv['quantity']-res.num_rows do
          values = Array.new
          for j in FIELDS do
            if row[j].nil? then
              values << "NULL"
            else
              values << "#{row[j]}"
            end
          end

          qry = String.new
          qry << SPC_INSERT_BASE_QRY
          qry << "('#{row['INVENTORY_ID']}-#{i+1}',"
          qry << "'#{row['CREATED_AT']}',"

          # assign top_specimen_id
          qry << "NULL,"

          qry << values.join(",")
          qry << ")"
          #print qry, ";\n";
          @dbh2.query(qry) if (Choice.choices[:sql])
        end
      else
        raise "#{res.num_rows}: nothing to be done for #{pvsv['pnumber']}, specimen type #{ pvsv['specimen_type_name']} with date #{pvsv['created_at']} \n"
      end
      res.free
    end

    if (Choice.choices[:sql])
      # set top specimen on all specimens with NULL for top_specimen
      @dbh2.query("update specimen set top_specimen_id=id where top_specimen_id is null")
      @dbh2.query("ALTER TABLE specimen MODIFY COLUMN ID INT(11) NOT NULL")
    end
  end
end

FixSourceVesselQty.new
