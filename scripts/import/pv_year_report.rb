#!/usr/bin/ruby -w

require "mysql"

class Script

  PV_QUERY = "
select name_short, count(*) as count
from patient_visit
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
where year(date_processed)={year}
group by study.id
order by name_short
";

   PATIENT_QUERY = "
select name_short, count(*) as count
from
(select name_short, pnumber, count(*)
 from patient
 join patient_visit on patient_visit.patient_id=patient.id
 join study on study.id=patient.study_id
 where year(date_processed)={year}
 group by study.id, patient.id
) as A
group by name_short
order by name_short
";

  @dbh = nil

  def initialize
    getDbConnection
    printReport getVisitTotalsByYear
    printReport getPatientTotalsByYear
  end

  private

  def getVisitTotalsByYear
    report = Hash.new
    year = 2010
    while year >= 2000 do
      res = @dbh.query(PV_QUERY.gsub("{year}", year.to_s()));
      while row = res.fetch_row do
        if (report[row[0]] == nil)
          report[row[0]] = [ nil ] * 8;
        end
        report[row[0]][2010 - year] = row[1]
      end
      year = year - 1
    end
    p report
  end

  def getPatientTotalsByYear
    report = Hash.new
    year = 2010
    while year >= 2000 do
      res = @dbh.query(PATIENT_QUERY.gsub("{year}", year.to_s()));
      while row = res.fetch_row do
        if (report[row[0]] == nil)
          report[row[0]] = [ nil ] * 8;
        end
        report[row[0]][2010 - year] = row[1]
      end
      year = year - 1
    end
    p report
  end

  def printReport(data)
    data.keys.
  end

  def getDbConnection
    begin
      @dbh = Mysql.real_connect("localhost", "dummy", "ozzy498", "biobank2")
    rescue Mysql::Error => e
      puts "Error message: #{e.error}"
      exit
    end
  end

end

Script.new
