#!/usr/bin/ruby -w

require "mysql"
require "csv"

class ScriptBase

  CBSR_ALPHA = "ABCDEFGHJKLMNPQRSTUVWXYZ"

  STUDY_OLD_NAMES = { 'BBPSP' => 'BBP' }

  @dbh = nil

  def getDbConnection(dbname, host = 'localhost')
    begin
      @dbh = Mysql.real_connect(host, "dummy", "ozzy498", dbname)
    rescue Mysql::Error => e
      puts "Error message: #{e.error}"
      exit
    end
  end

  def getStudyOldName(name)
    if STUDY_OLD_NAMES.has_key?(name)
      return STUDY_OLD_NAMES[name]
    end
    return name;
  end

end
