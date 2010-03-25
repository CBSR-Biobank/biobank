#!/usr/bin/ruby -w

require "mysql"

class Script

  YEAR_QUERY = <<YEAR_QUERY_END
select year(date_processed)
from patient_visit
group by year(date_processed)
YEAR_QUERY_END

  PV_QUERY = <<PV_QUERY_END
select name_short, count(*) as count
from patient_visit
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
where year(date_processed)={year}
group by study.id
order by name_short
PV_QUERY_END

  PV_QUERY_TOT_BY_YEAR = <<PV_QUERY_TOT_BY_YEAR_END
select count(*) as count
from patient_visit
where year(date_processed)={year}
PV_QUERY_TOT_BY_YEAR_END

PV_QUERY_TOT_BY_STUDY = <<PV_QUERY_TOT_BY_STUDY_END
select name_short, count(*) as count
from patient_visit
join patient on patient.id=patient_visit.patient_id
join study on study.id=patient.study_id
group by study.id
order by name_short
PV_QUERY_TOT_BY_STUDY_END

   PATIENT_QUERY = <<PATIENT_QUERY_END
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
PATIENT_QUERY_END

   PATIENT_QUERY_TOT_BY_YEAR = <<PATIENT_QUERY_TOT_BY_YEAR_END
select count(*) as count
from
(select name_short, pnumber, count(*)
 from patient
 join patient_visit on patient_visit.patient_id=patient.id
 join study on study.id=patient.study_id
 where year(date_processed)={year}
 group by study.id, patient.id
) as A
PATIENT_QUERY_TOT_BY_YEAR_END

   PATIENT_QUERY_TOT_BY_STUDY = <<PATIENT_QUERY_TOT_BY_STUDY_END
select name_short, count(*) as count
from
(select name_short, pnumber, count(*)
 from patient
 join patient_visit on patient_visit.patient_id=patient.id
 join study on study.id=patient.study_id
 group by study.id, patient.id
) as A
group by name_short
order by name_short
PATIENT_QUERY_TOT_BY_STUDY_END

  @dbh = nil

  def initialize
    getDbConnection
    years = getYears

    report = getVisitTotalsByYear years
    printReport report, years, "Patient Visits"

    report = getPatientTotalsByYear years
    printReport report, years, "\nPatients"
  end

  private

  def getYears
    years = []
    res = @dbh.query(YEAR_QUERY);
    while row = res.fetch_row do
      years << Integer(row[0])
    end
    years
  end

  def getVisitTotalsByYear(years)
    report = Hash.new
    report["Totals"] = [ nil ] * (years.length + 1);
    years.each do |year|
      res = @dbh.query(PV_QUERY.gsub("{year}", year.to_s()));
      while row = res.fetch_row do
        if (report[row[0]] == nil)
          report[row[0]] = [ nil ] * (years.length + 1); # plus 1 for totals column
        end
        report[row[0]][years.max - year] = row[1]
      end

      res = @dbh.query(PV_QUERY_TOT_BY_YEAR.gsub("{year}", year.to_s()));
      while row = res.fetch_row do
        report["Totals"][years.max - year] = row[0]
      end
    end

    years.each do |year|
      res = @dbh.query(PV_QUERY_TOT_BY_STUDY);
      while row = res.fetch_row do
        report[row[0]][years.length] = row[1]
      end
    end

    report
  end

  def getPatientTotalsByYear(years)
    report = Hash.new
    report["Totals"] = [ nil ] * (years.length + 1);
    years.each do |year|
      res = @dbh.query(PATIENT_QUERY.gsub("{year}", year.to_s()));
      while row = res.fetch_row do
        if (report[row[0]] == nil)
          report[row[0]] = [ nil ] * 8;
        end
        report[row[0]][years.max - year] = row[1]
      end


      res = @dbh.query(PATIENT_QUERY_TOT_BY_YEAR.gsub("{year}", year.to_s()));
      while row = res.fetch_row do
        report["Totals"][years.max - year] = row[0]
      end
    end

    years.each do |year|
      res = @dbh.query(PATIENT_QUERY_TOT_BY_STUDY);
      while row = res.fetch_row do
        report[row[0]][years.length] = row[1]
      end
    end

    report
  end

  def printReport(data, years, heading)
    puts heading
    print "study,", years.reverse.join(","), ",Total\n"
    data.keys.sort.each do |study|
      if (!study.eql? "Totals")
        print study, ",", data[study].join(","), "\n"
      end
    end
    if (data["Totals"] != nil)
      print "Totals,", data["Totals"].join(","), "\n"
    end
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
