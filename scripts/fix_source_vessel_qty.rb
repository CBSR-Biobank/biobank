#!/usr/bin/ruby -w

$LOAD_PATH.unshift( File.join( File.dirname(__FILE__), 'lib' ) )

require "script_base"
require 'time'
require 'pp'

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
insert into  specimen (inventory_id,created_at,quantity,activity_status_id,
collection_event_id,original_collection_event_id,specimen_type_id,
parent_specimen_id,top_specimen_id,origin_info_id) values
SPC_INSERT_QRY

  FIELDS = [ 'QUANTITY', 'ACTIVITY_STATUS_ID', 'COLLECTION_EVENT_ID', 'ORIGINAL_COLLECTION_EVENT_ID',
             'SPECIMEN_TYPE_ID', 'PARENT_SPECIMEN_ID', 'TOP_SPECIMEN_ID', 'ORIGIN_INFO_ID' ]

  def initialize
    getDbConnection("biobank2", 'localhost')
    res = @dbh.query(BIOBANK2_QRY)
    pvsvs = Array.new
    res.each_hash do |row|
      begin
        created_at = Time.parse(row['created_at']).utc.strftime("%Y-%m-%d %T")
      rescue ArgumentError
        created_at = row['created_at']
      end

      pvsvs << {
        'study' => row['study'],
        'pnumber' => row['pnumber'],
        'created_at_unformatted' => row['created_at'],
        'created_at' => created_at,
        'specimen_type_name' => row['name'],
        'quantity' => row['quantity'].to_i }
    end
    res.free
    #pp pvsvs

    @dbh2 = Mysql.real_connect('localhost', "dummy", "ozzy498", "biobank")

#    @dbh2.query("ALTER TABLE specimen MODIFY COLUMN ID INT(11) NOT NULL auto_increment")

    pvsvs.each do |pvsv|
      qry = BIOBANK_QRY.gsub("{study_name}", pvsv['study']);
      qry = qry.gsub("{pnumber}", pvsv['pnumber']);
      qry = qry.gsub("{specimen_type_name}", pvsv['specimen_type_name']);

      begin
        created_at = Time.parse(pvsv['created_at'])
      rescue ArgumentError
        created_at = Time.parse("1970-01-01")
      end

      x = created_at <=> Time.parse("1970-01-01")
      if x <= 0
        qry = qry.gsub("{created_at}", pvsv['created_at']);
      else
        qry = qry.gsub("{created_at}", pvsv['created_at_unformatted']);
      end

      #print qry, "\n"
      res = @dbh2.query(qry)
      if (res.num_rows < 1)
        print "no result for: ",
        [ pvsv['study'],  pvsv['pnumber'],  pvsv['created_at_unformatted'], pvsv['created_at'],
          pvsv['specimen_type_name'], pvsv['quantity'] ].join(", "), "\n"
      elsif (res.num_rows == pvsv['quantity'])
        print "fixed: ",
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
          if x <= 0
            qry << "'#{row['CREATED_AT']}',"
          else
            qry << "'#{pvsv['created_at_unformatted']}',"
          end
          qry << values.join(",")
          qry << ")"
          #print qry, "\n";
          #@dbh2.query(qry)
        end
      else
        raise "#{res.num_rows}: nothing to be done for #{pvsv['pnumber']}, specimen type #{ pvsv['specimen_type_name']} with date #{pvsv['created_at']} \n"
      end
      res.free
    end

#    @dbh2.query("ALTER TABLE specimen MODIFY COLUMN ID INT(11) NOT NULL")
  end
end

FixSourceVesselQty.new
