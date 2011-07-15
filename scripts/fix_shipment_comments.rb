#!/usr/bin/ruby -w

#-------------------------------------------------------------------------------
#
# Fix for issue #1314.
#
# Copies the comments for shipments in the biobank2 database (versionn 1.3.9) to
# the biobank database (version 3.0.5).
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
SELECT cl.name_short CLINIC_NAME,
aship.WAYBILL,
aship.COMMENT,
aship.DATE_RECEIVED,
aship.SITE_ID,
aship.ACTIVITY_STATUS_ID,
aship.DATE_SHIPPED,
aship.SHIPPING_METHOD_ID
FROM abstract_shipment aship
join clinic cl on cl.id=aship.clinic_id
where aship.discriminator='ClinicShipment'
and aship.comment is not null;
BIOBANK2_QRY_END

  BIOBANK_QRY = <<BIOBANK_QRY_END
select * from shipment_info where received_at='{received_at}'
BIOBANK_QRY_END

  FIELDS = [
            "CLINIC_NAME",
            "WAYBILL",
            "COMMENT",
            "DATE_RECEIVED",
            "SITE_ID",
            "ACTIVITY_STATUS_ID",
            "DATE_SHIPPED",
            "SHIPPING_METHOD_ID"
           ]

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
    bb2_ships = Array.new
    res = @dbh.query(BIOBANK2_QRY)
    res.each_hash do |row|
      bb2_ships << row
      bb2_ships.last['DATE_RECEIVED_UTC'] = Time.parse(bb2_ships.last['DATE_RECEIVED']).utc.strftime("%Y-%m-%d %T")
      if !bb2_ships.last['DATE_SHIPPED'].nil?
        bb2_ships.last['DATE_SHIPPED_UTC'] = Time.parse(bb2_ships.last['DATE_SHIPPED']).utc.strftime("%Y-%m-%d %T")
      end
    end
    res.free

    @dbh2 = Mysql.real_connect('localhost', "dummy", "ozzy498", "biobank")

    bb2_ships.each do |bb2_ship|
      received_at = bb2_ship['DATE_RECEIVED_UTC'];

      qry = String.new
      qry << BIOBANK_QRY.gsub('{received_at}', received_at)

      if bb2_ship['WAYBILL'].nil?
        qry << " and waybill is null"
      else
        qry << " and waybill='#{bb2_ship['WAYBILL']}'"
      end

      if bb2_ship['DATE_SHIPPED_UTC'].nil?
        packed_at = 'NULL'
        qry << " and packed_at is null"
      else
        year = bb2_ship['DATE_SHIPPED'][0..4].to_i

        if (year > 2011)
          packed_at = bb2_ship['DATE_SHIPPED']
        else
          packed_at = bb2_ship['DATE_SHIPPED_UTC']
        end
        qry << " and packed_at='#{packed_at}'"
      end

      print "searching for shipment from clinic #{bb2_ship['CLINIC_NAME']} with received at #{received_at} and packed at #{packed_at}\n"
      #print qry, "\n"

      res = @dbh2.query(qry)
      if res.num_rows != 1
        print "ERROR: NOT FOUND\n"
      else
        row = res.fetch_hash
        print "updating shipment_info with id #{row['ID']}\n"
        update_qry = "update shipment_info set comment=\"#{bb2_ship['COMMENT']}\" where id=#{row['ID']}"
        #print update_qry, "\n"
        @dbh2.query(update_qry) if (Choice.choices[:sql])
      end

    end

    if (Choice.choices[:sql])
    end
  end
end

FixSourceVesselQty.new
